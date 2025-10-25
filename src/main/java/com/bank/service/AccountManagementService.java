package com.bank.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.AccountCreationRequest;
import com.bank.dto.AccountDetailsResponse;
import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TransactionRepository;

@Service
public class AccountManagementService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final SecureRandom random = new SecureRandom();

    /**
     * Create a new account
     */
    @Transactional
    public AccountDetailsResponse createAccount(AccountCreationRequest request, String username) {
        // Find or create customer
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            customer = new Customer();
            customer.setUser(user);
            customer.setName(request.getCustomerName());
            customer.setEmail(request.getCustomerEmail());
            customer.setPhone(request.getCustomerPhone());
            customer = customerRepository.save(customer);
            
            user.setCustomer(customer);
            userRepository.save(user);
        }

        // Create account
        Account account = new Account();
            account.setAccountNumber(generateAccountNumber());
            account.setCustomer(customer);
            Double init = request.getInitialDeposit();
            account.setBalance(init != null ? init : 0.0);
        
        account = accountRepository.save(account);

        return mapToDetailsResponse(account);
    }

    /**
     * Get account details
     */
    public AccountDetailsResponse getAccountDetails(Long accountId, String username) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Verify ownership
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }

        return mapToDetailsResponse(account);
    }

    /**
     * Get all accounts for a user
     */
    public List<AccountDetailsResponse> getUserAccounts(String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            return List.of();
        }

        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
            .map(this::mapToDetailsResponse)
            .collect(Collectors.toList());
    }

    /**
     * Update account nickname
     */
    @Transactional
    public AccountDetailsResponse updateAccountNickname(Long accountId, String nickname, String username) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Verify ownership
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }

        accountRepository.save(account);
        return mapToDetailsResponse(account);
    }

    /**
     * Get account balance
     */
    public Double getAccountBalance(Long accountId, String username) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));

        // Verify ownership
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }

        return account.getBalance();
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        // Generate 12-digit account number
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            accountNumber.append(random.nextInt(10));
        }
        
        // Check if it already exists, regenerate if needed
        String number = accountNumber.toString();
        while (accountRepository.findByAccountNumber(number).isPresent()) {
            accountNumber = new StringBuilder();
            for (int i = 0; i < 12; i++) {
                accountNumber.append(random.nextInt(10));
            }
            number = accountNumber.toString();
        }
        
        return number;
    }

    /**
     * Map Account to AccountDetailsResponse
     */
    private AccountDetailsResponse mapToDetailsResponse(Account account) {
        AccountDetailsResponse response = new AccountDetailsResponse();
        response.setId(account.getId());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType()); // Set account type
        response.setStatus(account.getStatus()); // Set account status
        response.setNickname(account.getNickname()); // Set nickname
        response.setBranchCode(account.getBranchCode()); // Set branch code
        response.setCreatedAt(account.getCreatedAt()); // Set created date
        response.setBalance(account.getBalance());
        response.setAvailableBalance(account.getBalance()); // Can be different if holds are implemented

        Customer customer = account.getCustomer();
        if (customer != null) {
            AccountDetailsResponse.CustomerInfo customerInfo = new AccountDetailsResponse.CustomerInfo();
            customerInfo.setId(customer.getId());
            customerInfo.setName(customer.getName());
            customerInfo.setEmail(customer.getEmail());
            customerInfo.setPhone(customer.getPhone());
            response.setCustomer(customerInfo);
        }

        // Get last transaction date
        Optional<Transaction> lastTransaction = transactionRepository
            .findTopByAccountOrderByDateDesc(account);
        lastTransaction.ifPresent(transaction -> 
            response.setLastTransactionDate(transaction.getDate())
        );

        return response;
    }
}
