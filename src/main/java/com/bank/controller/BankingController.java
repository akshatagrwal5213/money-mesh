package com.bank.controller;

// ...existing code...
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CustomerRequest;
import com.bank.dto.PendingTransferConfirmRequest;
import com.bank.dto.PendingTransferRequest;
import com.bank.dto.TransactionDto;
import com.bank.dto.TransactionRequest;
import com.bank.dto.TransferRequest;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.AppUser;
import com.bank.model.Card;
import com.bank.model.Customer;
import com.bank.model.PendingTransfer;
import com.bank.model.Transaction;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.AppUserRepository;
import com.bank.service.BankingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value={"/api"})
public class BankingController {
    @Autowired
    private BankingService bankingService;
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(value={"/customers"})
    @PreAuthorize(value = "hasRole('ADMIN')")
    public List<Customer> getAllCustomers() {
        return this.bankingService.getAllCustomers();
    }

    @PostMapping(value={"/customers"})
    @PreAuthorize(value = "hasRole('ADMIN')")
    public Customer createCustomer(@Valid @RequestBody CustomerRequest c) {
        Customer entity = new Customer();
        entity.setName(c.getName());
        entity.setEmail(c.getEmail());
        entity.setPhone(c.getPhone());
        return this.bankingService.createCustomer(entity);
    }

    // Commented out - duplicate endpoint, use AccountManagementController instead
    // @GetMapping(value={"/accounts"})
    // @PreAuthorize(value = "hasRole('ADMIN')")
    // public List<Account> getAllAccounts() {
    //     return this.bankingService.getAllAccounts();
    // }

    // Commented out - duplicate endpoint, use AccountManagementController instead
    // @PostMapping(value={"/accounts"})
    // @PreAuthorize(value = "hasRole('ADMIN')")
    // public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequest a) {
    //     Account saved;
    //     Card card;
    //     Account entity = new Account();
    //     entity.setAccountNumber(a.getAccountNumber());
    //     entity.setBalance(a.getBalance());
    //     if (a.getCustomerId() != null) {
    //         Customer c = new Customer();
    //         c.setId(a.getCustomerId());
    //         entity.setCustomer(c);
    //     }
    //     if ((card = this.bankingService.getCardForAccount((saved = this.bankingService.createAccount(entity)).getId())) != null) {
    //         return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(Map.of("account", saved, "debitCard", Map.of("cardNumber", this.maskCardNumber(card.getCardNumber()), "expiry", card.getExpiryDate().toString())));
    //     }
    //     return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(Map.of("account", saved));
    // }

    @GetMapping(value={"/accounts/{accountId}/card"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> getCardForAccount(@PathVariable Long accountId) {
        Card card = this.bankingService.getCardForAccount(accountId);
        if (card == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("cardNumber", this.maskCardNumber(card.getCardNumber()), "expiry", card.getExpiryDate().toString(), "type", card.getType().name(), "limit", card.getCardLimit()));
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + last4;
    }

    @GetMapping(value={"/accounts/{accountId}/credit/eligibility"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> checkCreditEligibility(@PathVariable Long accountId, @RequestParam double requiredAvailable) {
        boolean ok = this.bankingService.isCreditEligible(accountId, requiredAvailable);
        return ResponseEntity.ok(Map.of("eligible", ok));
    }

    @PostMapping(value={"/accounts/{accountId}/credit/request"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> requestCreditCard(@PathVariable Long accountId, @RequestParam double limit) {
        Card card = this.bankingService.createCreditCard(accountId, limit);
        return ResponseEntity.status((HttpStatusCode)HttpStatus.CREATED).body(Map.of("cardNumber", card.getCardNumber(), "cvv", card.getCvv(), "expiry", card.getExpiryDate().toString(), "limit", card.getCardLimit()));
    }

    @PreAuthorize(value = "hasRole('ADMIN')")
    @GetMapping(value={"/transactions"})
    public List<Transaction> getAllTransactions() {
        return this.bankingService.getAllTransactions();
    }

    @GetMapping(value={"/accounts/{accountNumber}/transactions"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public Page<TransactionDto> getTransactionsForAccount(@PathVariable String accountNumber, Pageable pageable) {
        return this.bankingService.getTransactionsForAccount(accountNumber, pageable);
    }

    @PostMapping(value={"/transactions"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody TransactionRequest req) {
        Transaction t = new Transaction();
        try {
            TransactionType tt = TransactionType.valueOf(req.getType().toUpperCase().replace('-', '_'));
            t.setType(tt);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Invalid transaction type: " + req.getType());
        }
        t.setAmount(req.getAmount());
        Account acc = new Account();
        acc.setId(req.getAccountId());
        t.setAccount(acc);
        Transaction tx = this.bankingService.createTransaction(t);
        return ResponseEntity.status(HttpStatus.CREATED).body(tx);
    }

    @PostMapping(value={"/transfer"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<String> transferMoney(@Valid @RequestBody TransferRequest req) {
        String msg = this.bankingService.transferMoney(req.getFromAccount(), req.getToAccount(), req.getAmount());
        return ResponseEntity.ok(msg);
    }

    @PostMapping(value={"/transfer/initiate"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> initiateTransfer(@Valid @RequestBody PendingTransferRequest req, Authentication authentication) {
        BigDecimal threshold = new BigDecimal("10000");
        if (req.getAmount().compareTo(threshold) <= 0) {
            String msg = this.bankingService.transferMoney(req.getFromAccount(), req.getToAccount(), req.getAmount().doubleValue());
            return ResponseEntity.ok(Map.of("status", "completed", "message", msg));
        }
        String username = authentication.getName();
        PendingTransfer pt = this.bankingService.initiatePendingTransfer(username, req.getFromAccount(), req.getToAccount(), req.getAmount());
        return ResponseEntity.ok(Map.of("pendingId", pt.getId(), "otp", pt.getOtp()));
    }

    @PostMapping(value={"/transfer/confirm"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<?> confirmTransfer(@Valid @RequestBody PendingTransferConfirmRequest req, Authentication authentication) {
        String username = authentication.getName();
        String msg = this.bankingService.confirmPendingTransfer(req.getPendingId(), req.getOtp(), username);
        return ResponseEntity.ok(Map.of("status", "completed", "message", msg));
    }

    @DeleteMapping(value={"/account/{accountId}"})
    @PreAuthorize(value = "hasAnyRole('ADMIN','CUSTOMER')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId, @RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            String password = request.get("password");
            
            // Verify password before deletion
            if (password == null || password.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Password is required"));
            }
            
            // Get user and verify password
            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password"));
            }
            
            // Check if account has balance before deletion
            Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            
            if (account.getBalance() > 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "message", "Cannot delete account with remaining balance. Please transfer or withdraw all funds first.",
                        "balance", account.getBalance()
                    ));
            }
            
            // Password verified and balance is zero, delete the specific account
            this.bankingService.deleteSingleAccount(accountId, username);
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete account: " + e.getMessage()));
        }
    }

    @DeleteMapping(value={"/customer/delete-profile"})
    @PreAuthorize(value = "hasRole('CUSTOMER')")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> deleteProfile(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            String password = request.get("password");
            
            // Check if user has any bank accounts first
            boolean hasAccounts = this.bankingService.customerHasAccounts(username);
            if (hasAccounts) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Cannot delete profile. You have active bank accounts. Please delete all accounts first."));
            }
            
            // Verify password before deletion
            if (password == null || password.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Password is required"));
            }
            
            // Get user and verify password
            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Incorrect password"));
            }
            
            // Password verified, delete entire profile
            this.bankingService.deleteCustomerProfile(username);
            return ResponseEntity.ok(Map.of("message", "Profile deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to delete profile: " + e.getMessage()));
        }
    }
}
