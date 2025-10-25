package com.bank.service;

import com.bank.dto.TransactionDto;
import com.bank.event.CardCreatedEvent;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidOtpException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Card;
import com.bank.model.CardType;
import com.bank.model.Customer;
import com.bank.model.PendingTransfer;
import com.bank.model.PendingTransferStatus;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CardRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.PendingTransferRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.AuditLogRepository;
import com.bank.repository.DeviceSessionRepository;
import com.bank.repository.UserPreferencesRepository;
import com.bank.model.AppUser;
import com.bank.model.AuditLog;
import com.bank.model.DeviceSession;
import com.bank.model.UserPreferences;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankingService {
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private CardRepository cardRepo;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TransactionRepository transactionRepo;
    @Autowired
    private PendingTransferRepository pendingTransferRepo;
    @Autowired
    private AppUserRepository userRepo;
    @Autowired
    private AuditLogRepository auditLogRepo;
    @Autowired
    private DeviceSessionRepository deviceSessionRepo;
    @Autowired
    private UserPreferencesRepository userPreferencesRepo;
    private final SecureRandom random = new SecureRandom();
    private final SecureRandom secureRandom = new SecureRandom();

    public List<Customer> getAllCustomers() {
        return this.customerRepo.findAll();
    }

    public Customer createCustomer(Customer customer) {
        return (Customer)this.customerRepo.save(customer);
    }

    public Account createAccount(Account account) {
        Account saved = (Account)this.accountRepo.save(account);
        if (saved.getBalance() >= 5000.0) {
            this.createDebitCardForAccount(saved);
        }
        return saved;
    }

    private Card createDebitCardForAccount(Account account) {
        Card c = new Card();
        c.setAccount(account);
        c.setType(CardType.DEBIT);
        c.setCardNumber(this.generateUniqueCardNumber());
        c.setCvv(this.generateCvv());
        c.setExpiryDate(this.generateExpiryDate());
        c.setCardLimit(null);
        Card saved = (Card)this.cardRepo.save(c);
        this.eventPublisher.publishEvent((ApplicationEvent)new CardCreatedEvent(this, saved.getId(), account.getId()));
        return saved;
    }

    public boolean isCreditEligible(Long accountId, double requiredAvailable) {
        Account a = this.accountRepo.findById(accountId).orElse(null);
        if (a == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        return a.getBalance() >= requiredAvailable;
    }

    public Card createCreditCard(Long accountId, double cardLimit) {
        Account a = this.accountRepo.findById(accountId).orElse(null);
        if (a == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        double required = cardLimit * 0.2;
        if (a.getBalance() < required) {
            throw new IllegalArgumentException("Account not eligible for requested credit limit");
        }
        Card c = new Card();
        c.setAccount(a);
        c.setType(CardType.CREDIT);
        c.setCardNumber(this.generateUniqueCardNumber());
        c.setCvv(this.generateCvv());
        c.setExpiryDate(this.generateExpiryDatePlusYears(4));
        c.setCardLimit(cardLimit);
        Card saved = (Card)this.cardRepo.save(c);
        this.eventPublisher.publishEvent((ApplicationEvent)new CardCreatedEvent(this, saved.getId(), a.getId()));
        return saved;
    }

    public Card getCardForAccount(Long accountId) {
        return this.cardRepo.findByAccount_Id(accountId);
    }

    private String generateUniqueCardNumber() {
        String num;
        int attempts = 0;
        do {
            num = "";
            for (int i = 0; i < 16; ++i) {
                num = num + String.valueOf(this.secureRandom.nextInt(10));
            }
        } while (++attempts <= 5 && this.cardRepo.findByCardNumber(num) != null);
        return num;
    }

    private String generateCvv() {
        int v = 100 + this.secureRandom.nextInt(900);
        return String.valueOf(v);
    }

    private LocalDate generateExpiryDate() {
        return this.generateExpiryDatePlusYears(3);
    }

    private LocalDate generateExpiryDatePlusYears(int years) {
        return LocalDate.now().plusYears(years);
    }

    @Transactional
    public Transaction createTransaction(Transaction t) {
        Account account = this.accountRepo.findById(t.getAccount().getId()).orElse(null);
        if (account == null) {
            throw new ResourceNotFoundException("Account not found for transaction");
        }
        TransactionType type = t.getType();
        if (type == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance() + t.getAmount());
        } else if (type == TransactionType.WITHDRAW) {
            if (account.getBalance() < t.getAmount()) {
                throw new InsufficientFundsException("Insufficient balance for withdraw");
            }
            account.setBalance(account.getBalance() - t.getAmount());
        }
        this.accountRepo.save(account);
        return (Transaction)this.transactionRepo.save(t);
    }

    public List<Transaction> getAllTransactions() {
        return this.transactionRepo.findAll();
    }

    public Page<TransactionDto> getTransactionsForAccount(String accountNumber, Pageable pageable) {
        Page<Transaction> page = this.transactionRepo.findByAccount_AccountNumber(accountNumber, pageable);
        List<TransactionDto> dtos = page.stream().map(tx -> {
            TransactionDto d = new TransactionDto();
            d.setId(tx.getId());
            if (tx.getType() != null) {
                d.setType(tx.getType().name());
            }
            d.setAmount(tx.getAmount());
            d.setDate(tx.getDate());
            if (tx.getAccount() != null) {
                d.setAccountNumber(tx.getAccount().getAccountNumber());
            }
            return d;
        }).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public List<Account> getAllAccounts() {
        return this.accountRepo.findAll();
    }

    @Transactional
    public String transferMoney(String fromAccountNum, String toAccountNum, double amount) {
        Account fromAcc = this.accountRepo.findByAccountNumber(fromAccountNum)
            .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));
        Account toAcc = this.accountRepo.findByAccountNumber(toAccountNum)
            .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));
        if (fromAcc.getBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance in sender's account");
        }
        fromAcc.setBalance(fromAcc.getBalance() - amount);
        toAcc.setBalance(toAcc.getBalance() + amount);
        this.accountRepo.save(fromAcc);
        this.accountRepo.save(toAcc);
        Transaction t1 = new Transaction();
        t1.setType(TransactionType.TRANSFER_OUT);
        t1.setAmount(amount);
        t1.setAccount(fromAcc);
        this.transactionRepo.save(t1);
        Transaction t2 = new Transaction();
        t2.setType(TransactionType.TRANSFER_IN);
        t2.setAmount(amount);
        t2.setAccount(toAcc);
        this.transactionRepo.save(t2);
        return "\u2705 \u20b9" + amount + " transferred successfully from " + fromAccountNum + " to " + toAccountNum + "!";
    }

    @Transactional
    public PendingTransfer initiatePendingTransfer(String initiatedBy, String fromAccount, String toAccount, BigDecimal amount) {
        PendingTransfer pt = new PendingTransfer();
        pt.setFromAccount(fromAccount);
        pt.setToAccount(toAccount);
        pt.setAmount(amount);
        pt.setInitiatedBy(initiatedBy);
        int code = 100000 + this.random.nextInt(900000);
        pt.setOtp(String.valueOf(code));
        PendingTransfer saved = (PendingTransfer)this.pendingTransferRepo.save(pt);
        return saved;
    }

    @Transactional
    public String confirmPendingTransfer(Long pendingId, String otp, String actingUser) {
        Optional<PendingTransfer> o = this.pendingTransferRepo.findById(pendingId);
        if (o.isEmpty()) {
            throw new ResourceNotFoundException("Pending transfer not found");
        }
        PendingTransfer pt = o.get();
        if (!pt.getInitiatedBy().equals(actingUser)) {
            throw new InvalidOtpException("You are not authorized to confirm this transfer");
        }
        if (!pt.getOtp().equals(otp)) {
            throw new InvalidOtpException("Invalid OTP");
        }
        if (pt.getStatus() != PendingTransferStatus.PENDING) {
            throw new InvalidOtpException("Pending transfer is not in a confirmable state");
        }
        double amt = pt.getAmount().doubleValue();
        String result = this.transferMoney(pt.getFromAccount(), pt.getToAccount(), amt);
        pt.setStatus(PendingTransferStatus.COMPLETED);
        this.pendingTransferRepo.save(pt);
        return result;
    }

    public boolean customerHasAccounts(String username) {
        // Find customer by username
        Customer customer = customerRepo.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
        
        // Check if customer has any accounts
        List<Account> accounts = accountRepo.findAll().stream()
            .filter(a -> a.getCustomer() != null && a.getCustomer().getId().equals(customer.getId()))
            .collect(Collectors.toList());
        
        return !accounts.isEmpty();
    }

    @Transactional
    public void deleteSingleAccount(Long accountId, String username) {
        System.out.println("🏦 Starting single account deletion for account ID: " + accountId);
        
        // Find the account
        Account account = accountRepo.findById(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        
        // Verify the account belongs to this user
        Customer customer = customerRepo.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
        
        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("This account does not belong to you");
        }
        
        System.out.println("   Account Number: " + account.getAccountNumber());
        
        // Delete cards for this account
        List<Card> cards = cardRepo.findAll().stream()
            .filter(c -> c.getAccount() != null && c.getAccount().getId().equals(account.getId()))
            .collect(Collectors.toList());
        cardRepo.deleteAll(cards);
        System.out.println("   Deleted " + cards.size() + " card(s)");
        
        // Delete transactions for this account
        List<Transaction> transactions = transactionRepo.findAll().stream()
            .filter(t -> t.getAccount() != null && t.getAccount().getId().equals(account.getId()))
            .collect(Collectors.toList());
        transactionRepo.deleteAll(transactions);
        System.out.println("   Deleted " + transactions.size() + " transaction(s)");
        
        // Delete pending transfers for this account
        List<PendingTransfer> transfers = pendingTransferRepo.findAll().stream()
            .filter(pt -> pt.getFromAccount().equals(account.getAccountNumber()) || 
                         pt.getToAccount().equals(account.getAccountNumber()))
            .collect(Collectors.toList());
        pendingTransferRepo.deleteAll(transfers);
        System.out.println("   Deleted " + transfers.size() + " pending transfer(s)");
        
        // Delete the account
        accountRepo.delete(account);
        System.out.println("✅ Account deletion completed: " + account.getAccountNumber());
    }

    @Transactional
    public void deleteCustomerBankAccounts(String username) {
        System.out.println("🏦 Starting bank accounts deletion for user: " + username);
        
        // Find customer by username
        Customer customer = customerRepo.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
        
        System.out.println("   Found customer ID: " + customer.getId());
        
        // Get all accounts for this customer
        List<Account> accounts = accountRepo.findAll().stream()
            .filter(a -> a.getCustomer() != null && a.getCustomer().getId().equals(customer.getId()))
            .collect(Collectors.toList());
        System.out.println("   Found " + accounts.size() + " account(s)");
        
        // Delete all associated data for each account
        for (Account account : accounts) {
            // Delete cards
            List<Card> cards = cardRepo.findAll().stream()
                .filter(c -> c.getAccount() != null && c.getAccount().getId().equals(account.getId()))
                .collect(Collectors.toList());
            cardRepo.deleteAll(cards);
            System.out.println("   Deleted " + cards.size() + " card(s) for account " + account.getAccountNumber());
            
            // Delete transactions
            List<Transaction> transactions = transactionRepo.findAll().stream()
                .filter(t -> t.getAccount() != null && t.getAccount().getId().equals(account.getId()))
                .collect(Collectors.toList());
            transactionRepo.deleteAll(transactions);
            System.out.println("   Deleted " + transactions.size() + " transaction(s) for account " + account.getAccountNumber());
        }
        
        // Delete pending transfers
        List<PendingTransfer> transfers = pendingTransferRepo.findAll().stream()
            .filter(pt -> {
                for (Account acc : accounts) {
                    if (pt.getFromAccount().equals(acc.getAccountNumber()) || 
                        pt.getToAccount().equals(acc.getAccountNumber())) {
                        return true;
                    }
                }
                return false;
            })
            .collect(Collectors.toList());
        pendingTransferRepo.deleteAll(transfers);
        System.out.println("   Deleted " + transfers.size() + " pending transfer(s)");
        
        // Delete all accounts
        accountRepo.deleteAll(accounts);
        System.out.println("   Deleted " + accounts.size() + " account(s)");
        
        System.out.println("✅ Bank accounts deletion completed. Profile retained for user: " + username);
    }

    @Transactional
    public void deleteCustomerProfile(String username) {
        System.out.println("🗑️ Starting COMPLETE profile deletion for user: " + username);
        
        // Find customer by username
        Customer customer = customerRepo.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with username: " + username));
        
        AppUser user = customer.getUser();
        System.out.println("   Found customer ID: " + customer.getId() + ", User ID: " + user.getId());
        
        // Get all accounts for this customer
        List<Account> accounts = accountRepo.findAll().stream()
            .filter(a -> a.getCustomer() != null && a.getCustomer().getId().equals(customer.getId()))
            .collect(Collectors.toList());
        System.out.println("   Found " + accounts.size() + " account(s)");
        
        // Delete all associated data for each account
        for (Account account : accounts) {
            // Delete cards
            List<Card> cards = cardRepo.findAll().stream()
                .filter(c -> c.getAccount() != null && c.getAccount().getId().equals(account.getId()))
                .collect(Collectors.toList());
            cardRepo.deleteAll(cards);
            System.out.println("   Deleted " + cards.size() + " card(s) for account " + account.getAccountNumber());
            
            // Delete transactions
            List<Transaction> transactions = transactionRepo.findAll().stream()
                .filter(t -> t.getAccount() != null && t.getAccount().getId().equals(account.getId()))
                .collect(Collectors.toList());
            transactionRepo.deleteAll(transactions);
            System.out.println("   Deleted " + transactions.size() + " transaction(s) for account " + account.getAccountNumber());
        }
        
        // Delete pending transfers
        List<PendingTransfer> transfers = pendingTransferRepo.findAll().stream()
            .filter(pt -> {
                for (Account acc : accounts) {
                    if (pt.getFromAccount().equals(acc.getAccountNumber()) || 
                        pt.getToAccount().equals(acc.getAccountNumber())) {
                        return true;
                    }
                }
                return false;
            })
            .collect(Collectors.toList());
        pendingTransferRepo.deleteAll(transfers);
        System.out.println("   Deleted " + transfers.size() + " pending transfer(s)");
        
        // Delete all accounts
        accountRepo.deleteAll(accounts);
        System.out.println("   Deleted " + accounts.size() + " account(s)");
        
        // Delete user preferences
        List<UserPreferences> preferences = userPreferencesRepo.findAll().stream()
            .filter(p -> p.getCustomer() != null && p.getCustomer().getId().equals(customer.getId()))
            .collect(Collectors.toList());
        userPreferencesRepo.deleteAll(preferences);
        System.out.println("   Deleted " + preferences.size() + " user preference(s)");
        
        // Delete audit logs
        List<AuditLog> auditLogs = auditLogRepo.findAll().stream()
            .filter(log -> log.getUser() != null && log.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());
        auditLogRepo.deleteAll(auditLogs);
        System.out.println("   Deleted " + auditLogs.size() + " audit log(s)");
        
        // Delete device sessions
        List<DeviceSession> deviceSessions = deviceSessionRepo.findAll().stream()
            .filter(ds -> ds.getUser() != null && ds.getUser().getId().equals(user.getId()))
            .collect(Collectors.toList());
        deviceSessionRepo.deleteAll(deviceSessions);
        System.out.println("   Deleted " + deviceSessions.size() + " device session(s)");
        
        // Delete the customer (cascade should handle user, but we'll be explicit)
        customerRepo.delete(customer);
        System.out.println("   Deleted customer record");
        
        // Delete the user
        userRepo.delete(user);
        System.out.println("   Deleted user record");
        
        System.out.println("✅ COMPLETE profile deletion completed for user: " + username);
    }
}
