package com.bank.service;

import com.bank.dto.TransactionDto;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionOperationService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AuditService auditService;

    private static final double MINIMUM_BALANCE = 100.0; // Minimum balance requirement

    /**
     * Deposit money into account
     */
    @Transactional
    public TransactionDto deposit(Long accountId, Double amount, String username, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        verifyAccountOwnership(account, username);

        // Update balance
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description != null ? description : "Deposit");
        transaction = transactionRepository.save(transaction);

        // Audit log
        auditService.logAction(username, "DEPOSIT", 
            "Deposited " + amount + " to account " + account.getAccountNumber());

        return mapToDto(transaction);
    }

    /**
     * Withdraw money from account
     */
    @Transactional
    public TransactionDto withdraw(Long accountId, Double amount, String username, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        verifyAccountOwnership(account, username);

        // Check sufficient balance
        if (account.getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException("Insufficient funds. Minimum balance of " + 
                MINIMUM_BALANCE + " must be maintained");
        }

        // Update balance
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setDescription(description != null ? description : "Withdrawal");
        transaction = transactionRepository.save(transaction);

        // Audit log
        auditService.logAction(username, "WITHDRAWAL", 
            "Withdrew " + amount + " from account " + account.getAccountNumber());

        return mapToDto(transaction);
    }

    /**
     * Transfer money between accounts
     */
    @Transactional
    public TransactionDto transfer(Long fromAccountId, Long toAccountId, Double amount, 
                                   String username, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        verifyAccountOwnership(fromAccount, username);

        // Check sufficient balance
        if (fromAccount.getBalance() - amount < MINIMUM_BALANCE) {
            throw new InsufficientFundsException("Insufficient funds. Minimum balance of " + 
                MINIMUM_BALANCE + " must be maintained");
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        // Create withdrawal transaction for source account
        Transaction withdrawalTransaction = new Transaction();
        withdrawalTransaction.setAccount(fromAccount);
        withdrawalTransaction.setType(TransactionType.TRANSFER_OUT);
        withdrawalTransaction.setAmount(amount);
        withdrawalTransaction.setDate(LocalDateTime.now());
        withdrawalTransaction.setDescription(description != null ? description : 
            "Transfer to " + toAccount.getAccountNumber());
        transactionRepository.save(withdrawalTransaction);

        // Create deposit transaction for destination account
        Transaction depositTransaction = new Transaction();
        depositTransaction.setAccount(toAccount);
        depositTransaction.setType(TransactionType.TRANSFER_IN);
        depositTransaction.setAmount(amount);
        depositTransaction.setDate(LocalDateTime.now());
        depositTransaction.setDescription(description != null ? description : 
            "Transfer from " + fromAccount.getAccountNumber());
        transactionRepository.save(depositTransaction);

        // Audit log
        auditService.logAction(username, "TRANSFER", 
            "Transferred " + amount + " from " + fromAccount.getAccountNumber() + 
            " to " + toAccount.getAccountNumber());

        return mapToDto(withdrawalTransaction);
    }

    /**
     * Get transaction history with pagination
     */
    public List<TransactionDto> getTransactionHistory(Long accountId, String username, 
                                                     int page, int size) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        verifyAccountOwnership(account, username);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        Page<Transaction> transactions = transactionRepository.findByAccount(account, pageable);

        return transactions.getContent().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Get transactions by date range
     */
    public List<TransactionDto> getTransactionsByDateRange(Long accountId, String username,
                                                          LocalDateTime startDate, 
                                                          LocalDateTime endDate) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        verifyAccountOwnership(account, username);

        List<Transaction> transactions = transactionRepository
            .findByAccountAndDateBetween(account, startDate, endDate);

        return transactions.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
    }

    /**
     * Verify account ownership
     */
    private void verifyAccountOwnership(Account account, String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (account.getCustomer() == null || 
            !account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }
    }

    /**
     * Map Transaction to DTO
     */
    private TransactionDto mapToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setType(transaction.getType().name());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setDescription(transaction.getDescription());
        dto.setBalance(transaction.getAccount().getBalance());
        return dto;
    }
}
