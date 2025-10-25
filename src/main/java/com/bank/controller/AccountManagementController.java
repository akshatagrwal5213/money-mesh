package com.bank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.AccountCreationRequest;
import com.bank.dto.AccountDetailsResponse;
import com.bank.service.AccountManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountManagementController {

    @Autowired
    private AccountManagementService accountManagementService;
    private static final Logger logger = LoggerFactory.getLogger(AccountManagementController.class);

    /**
     * Create a new account
     */
    @PostMapping
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody AccountCreationRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            AccountDetailsResponse response = accountManagementService.createAccount(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all accounts for current user
     */
    @GetMapping
    public ResponseEntity<?> getUserAccounts(Authentication authentication) {
        try {
            logger.debug("/api/accounts called - authentication={}", authentication);
            if (authentication == null) {
                logger.debug("Authentication is null");
                return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
            }
            String username = authentication.getName();
            logger.debug("Username: {}", username);
            List<AccountDetailsResponse> accounts = accountManagementService.getUserAccounts(username);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            logger.error("Exception in /api/accounts", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get specific account details
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<?> getAccountDetails(
            @PathVariable Long accountId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            AccountDetailsResponse response = accountManagementService.getAccountDetails(accountId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get account balance
     */
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getAccountBalance(
            @PathVariable Long accountId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Double balance = accountManagementService.getAccountBalance(accountId, username);
            return ResponseEntity.ok(Map.of("balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update account nickname
     */
    @PutMapping("/{accountId}/nickname")
    public ResponseEntity<?> updateAccountNickname(
            @PathVariable Long accountId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String nickname = request.get("nickname");
            AccountDetailsResponse response = accountManagementService.updateAccountNickname(
                accountId, nickname, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
