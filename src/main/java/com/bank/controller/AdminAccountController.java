package com.bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CustomerRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AdminAccountController.class);

    /**
     * Create customer with account (complete onboarding by admin)
     */
    @PostMapping("/customers/create-with-account")
    @Transactional
    public ResponseEntity<?> createCustomerWithAccount(@Valid @RequestBody AdminCustomerAccountRequest request) {
        try {
            // Step 1: Create AppUser if username/password provided
            AppUser user = null;
            if (request.getUsername() != null && !request.getUsername().isEmpty()) {
                // Check if username already exists
                if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Username already exists"));
                }
                
                user = new AppUser();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole("ROLE_CUSTOMER");
                user = userRepository.save(user);
            }

            // Step 2: Create Customer
            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
            }

            Customer customer = new Customer();
            customer.setName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            if (user != null) {
                customer.setUser(user);
            }
            customer = customerRepository.save(customer);

            // Step 3: Create Account
            Account account = new Account();
            account.setAccountNumber(generateAccountNumber());
            Double initialDeposit = request.getInitialDeposit();
            account.setBalance(initialDeposit != null ? initialDeposit : 0.0);
            account.setCustomer(customer);
            account = accountRepository.save(account);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer and account created successfully!");
            response.put("customerId", customer.getId());
            response.put("customerName", customer.getName());
            response.put("accountNumber", account.getAccountNumber());
            response.put("accountId", account.getId());
            response.put("balance", account.getBalance());
            if (user != null) {
                response.put("userId", user.getId());
                response.put("username", user.getUsername());
                response.put("loginEnabled", true);
            } else {
                response.put("loginEnabled", false);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Failed to create customer and account", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create customer and account: " + e.getMessage()));
        }
    }

    /**
     * Add account to existing customer
     */
    @PostMapping("/customers/{customerId}/accounts")
    public ResponseEntity<?> addAccountToCustomer(
            @PathVariable Long customerId,
            @Valid @RequestBody AdminAccountRequest request) {
        try {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            Account account = new Account();
            account.setAccountNumber(generateAccountNumber());
            Double init = request.getInitialDeposit();
            account.setBalance(init != null ? init : 0.0);
            account.setCustomer(customer);
            account = accountRepository.save(account);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account added successfully!");
            response.put("accountNumber", account.getAccountNumber());
            response.put("accountId", account.getId());
            response.put("balance", account.getBalance());
            response.put("customerName", customer.getName());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to add account: " + e.getMessage()));
        }
    }

    /**
     * Get all accounts (admin view)
     */
    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts() {
        try {
            List<Account> accounts = accountRepository.findAll();
            
            List<Map<String, Object>> accountList = accounts.stream().map(account -> {
                Map<String, Object> accountInfo = new HashMap<>();
                accountInfo.put("id", account.getId());
                accountInfo.put("accountNumber", account.getAccountNumber());
                accountInfo.put("balance", account.getBalance());
                accountInfo.put("customerId", account.getCustomer().getId());
                accountInfo.put("customerName", account.getCustomer().getName());
                accountInfo.put("customerEmail", account.getCustomer().getEmail());
                accountInfo.put("customerPhone", account.getCustomer().getPhone());
                return accountInfo;
            }).toList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", accountList.size(),
                "accounts", accountList
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve accounts: " + e.getMessage()));
        }
    }

    /**
     * Get accounts for specific customer
     */
    @GetMapping("/customers/{customerId}/accounts")
    public ResponseEntity<?> getCustomerAccounts(@PathVariable Long customerId) {
        try {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            List<Account> accounts = accountRepository.findByCustomer(customer);

            List<Map<String, Object>> accountList = accounts.stream().map(account -> {
                Map<String, Object> accountInfo = new HashMap<>();
                accountInfo.put("id", account.getId());
                accountInfo.put("accountNumber", account.getAccountNumber());
                accountInfo.put("balance", account.getBalance());
                return accountInfo;
            }).toList();

            return ResponseEntity.ok(Map.of(
                "success", true,
                "customerId", customerId,
                "customerName", customer.getName(),
                "accountCount", accountList.size(),
                "accounts", accountList
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve customer accounts: " + e.getMessage()));
        }
    }

    /**
     * Delete account (admin only)
     */
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        try {
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

            // Check if account has balance
            if (account.getBalance() > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Cannot delete account with positive balance. Please transfer funds first."));
            }

            String accountNumber = account.getAccountNumber();
            accountRepository.delete(account);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Account " + accountNumber + " deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete account: " + e.getMessage()));
        }
    }

    /**
     * Update account balance (admin adjustment)
     */
    @PutMapping("/accounts/{accountId}/adjust-balance")
    public ResponseEntity<?> adjustAccountBalance(
            @PathVariable Long accountId,
            @RequestBody Map<String, Object> request) {
        try {
            Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

            Double adjustment = ((Number) request.get("adjustment")).doubleValue();
            String reason = (String) request.get("reason");

            Double oldBalance = account.getBalance();
            Double newBalance = oldBalance + adjustment;

            if (newBalance < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Adjustment would result in negative balance"));
            }

            account.setBalance(newBalance);
            accountRepository.save(account);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Balance adjusted successfully",
                "accountNumber", account.getAccountNumber(),
                "oldBalance", oldBalance,
                "adjustment", adjustment,
                "newBalance", newBalance,
                "reason", reason != null ? reason : "Admin adjustment"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to adjust balance: " + e.getMessage()));
        }
    }

    /**
     * Generate unique account number
     */
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }

    // Inner classes for request bodies

    public static class AdminCustomerAccountRequest {
        private String fullName;
        private String email;
        private String phone;
        private String username;  // Optional
        private String password;  // Optional
        private Double initialDeposit;

        // Getters and setters
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public Double getInitialDeposit() { return initialDeposit; }
        public void setInitialDeposit(Double initialDeposit) { this.initialDeposit = initialDeposit; }
    }

    public static class AdminAccountRequest {
        private Double initialDeposit;

        public Double getInitialDeposit() { return initialDeposit; }
        public void setInitialDeposit(Double initialDeposit) { this.initialDeposit = initialDeposit; }
    }
}
