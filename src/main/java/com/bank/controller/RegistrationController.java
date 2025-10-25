package com.bank.controller;

// ...existing code...
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CustomerRequest;
import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Customer;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.repository.CustomerRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*")
public class RegistrationController {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Complete registration endpoint - creates user and customer
     * Account creation is now OPTIONAL - can be skipped and added later
     */
    @PostMapping("/complete")
    @Transactional
    public ResponseEntity<?> completeRegistration(@Valid @RequestBody RegistrationRequest request) {
    try {
            // Step 1: Create AppUser
            AppUser user = new AppUser();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(request.getRole() != null ? request.getRole() : "ROLE_CUSTOMER");
            
            // Check if username already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
            }
            
            AppUser savedUser = userRepository.save(user);

            // Step 2: Create Customer
            Customer customer = new Customer();
            customer.setName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());
            customer.setUser(savedUser);
            
            // Check if email already exists
            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
            }
            
            Customer savedCustomer = customerRepository.save(customer);

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", savedUser.getId());
            response.put("customerId", savedCustomer.getId());
            response.put("username", savedUser.getUsername());

            // Step 3: Create Account (OPTIONAL - only if account details provided)
            if (request.getSkipAccount() == null || !request.getSkipAccount()) {
                if (request.getInitialDeposit() != null && request.getInitialDeposit() > 0) {
                    Account account = new Account();
                    account.setAccountNumber(request.getAccountNumber() != null ? 
                        request.getAccountNumber() : generateAccountNumber());
                    Double balObj = request.getInitialDeposit();
                    account.setBalance(balObj != null ? balObj : 0.0);
                    account.setCustomer(savedCustomer);
                    
                    Account savedAccount = accountRepository.save(account);
                    
                    response.put("message", "Registration completed successfully with account!");
                    response.put("accountNumber", savedAccount.getAccountNumber());
                    response.put("accountId", savedAccount.getId());
                    response.put("hasAccount", true);
                } else {
                    response.put("message", "Profile created! You can add an account later from your dashboard.");
                    response.put("hasAccount", false);
                }
            } else {
                response.put("message", "Profile created! You can add an account later from your dashboard.");
                response.put("hasAccount", false);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    /**
     * Create customer only (no authentication required for registration)
     */
    @PostMapping("/customers")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest request) {
        try {
            // Check if email already exists
            if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
            }

            Customer customer = new Customer();
            customer.setName(request.getName());
            customer.setEmail(request.getEmail());
            customer.setPhone(request.getPhone());

            Customer saved = customerRepository.save(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create customer: " + e.getMessage()));
        }
    }

    /**
     * Link customer to user (for multi-step registration)
     */
    @PutMapping("/customers/{customerId}/link-user")
    @SuppressWarnings("unchecked")
    public ResponseEntity<?> linkCustomerToUser(
            @PathVariable Long customerId,
            @RequestBody Map<String, Object> request) {
        try {
            Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            Map<String, Object> userMap = (Map<String, Object>) request.get("user");
            Long userId = ((Number) userMap.get("id")).longValue();

            AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            customer.setUser(user);
            Customer updated = customerRepository.save(customer);

            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to link customer to user: " + e.getMessage()));
        }
    }

    /**
     * Create account (no authentication required for registration)
     */
    @PostMapping("/accounts")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountCreationRequest request) {
        try {
            Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

            Account account = new Account();
            account.setAccountNumber(request.getAccountNumber() != null ? 
                request.getAccountNumber() : generateAccountNumber());
            Double bal = request.getBalance();
            account.setBalance(bal != null ? bal : 0.0);
            account.setCustomer(customer);

            Account saved = accountRepository.save(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create account: " + e.getMessage()));
        }
    }

    /**
     * Check username availability
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean available = !userRepository.findByUsername(username).isPresent();
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Check email availability
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean available = !customerRepository.findByEmail(email).isPresent();
        return ResponseEntity.ok(Map.of("available", available));
    }

    /**
     * Generate account number
     */
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + (int)(Math.random() * 10000);
    }

    // Inner classes for request bodies

    public static class RegistrationRequest {
        private String username;
        private String password;
        private String role;
        private String fullName;
        private String email;
        private String phone;
        private String accountNumber;
        private Double initialDeposit;
        private Boolean skipAccount; // NEW: Allow users to skip account creation

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        
        public Double getInitialDeposit() { return initialDeposit; }
        public void setInitialDeposit(Double initialDeposit) { this.initialDeposit = initialDeposit; }
        
        public Boolean getSkipAccount() { return skipAccount; }
        public void setSkipAccount(Boolean skipAccount) { this.skipAccount = skipAccount; }
    }

    public static class AccountCreationRequest {
        private Long customerId;
        private String accountNumber;
        private Double balance;

        // Getters and setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        
        public Double getBalance() { return balance; }
        public void setBalance(Double balance) { this.balance = balance; }
    }
}
