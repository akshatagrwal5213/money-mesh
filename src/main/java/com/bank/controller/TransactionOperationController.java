package com.bank.controller;

import com.bank.dto.DepositRequest;
import com.bank.dto.TransactionDto;
import com.bank.dto.TransferRequestDto;
import com.bank.dto.WithdrawalRequest;
import com.bank.service.TransactionOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionOperationController {

    @Autowired
    private TransactionOperationService transactionService;

    /**
     * Deposit money
     */
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            TransactionDto transaction = transactionService.deposit(
                request.getAccountId(),
                request.getAmount(),
                username,
                request.getDescription()
            );
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Withdraw money
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WithdrawalRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            TransactionDto transaction = transactionService.withdraw(
                request.getAccountId(),
                request.getAmount(),
                username,
                request.getDescription()
            );
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Transfer money between accounts
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @Valid @RequestBody TransferRequestDto request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            TransactionDto transaction = transactionService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount(),
                username,
                request.getDescription()
            );
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get transaction history with pagination
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<TransactionDto> transactions = transactionService.getTransactionHistory(
                accountId, username, page, size);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get transactions by date range
     */
    @GetMapping("/account/{accountId}/range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<TransactionDto> transactions = transactionService.getTransactionsByDateRange(
                accountId, username, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
