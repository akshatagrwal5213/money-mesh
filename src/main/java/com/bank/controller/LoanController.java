package com.bank.controller;

import com.bank.dto.LoanApplicationRequest;
import com.bank.dto.LoanDetailsResponse;
import com.bank.dto.LoanEmiCalculationRequest;
import com.bank.dto.LoanEmiCalculationResponse;
import com.bank.dto.LoanRepaymentRequest;
import com.bank.model.LoanRepayment;
import com.bank.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    private LoanService loanService;

    /**
     * Apply for a new loan
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyForLoan(
            @Valid @RequestBody LoanApplicationRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            LoanDetailsResponse response = loanService.applyForLoan(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Calculate EMI
     */
    @PostMapping("/calculate-emi")
    public ResponseEntity<?> calculateEmi(
            @Valid @RequestBody LoanEmiCalculationRequest request) {
        try {
            LoanEmiCalculationResponse response = loanService.calculateEmiDetails(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Make loan repayment
     */
    @PostMapping("/repay")
    public ResponseEntity<?> makeRepayment(
            @Valid @RequestBody LoanRepaymentRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            LoanDetailsResponse response = loanService.makeRepayment(request, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get loan details
     */
    @GetMapping("/{loanId}")
    public ResponseEntity<?> getLoanDetails(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            LoanDetailsResponse response = loanService.getLoanDetails(loanId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all loans for user
     */
    @GetMapping
    public ResponseEntity<?> getUserLoans(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<LoanDetailsResponse> loans = loanService.getUserLoans(username);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get loan repayment history
     */
    @GetMapping("/{loanId}/repayments")
    public ResponseEntity<?> getLoanRepaymentHistory(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            List<LoanRepayment> repayments = loanService.getLoanRepaymentHistory(loanId, username);
            return ResponseEntity.ok(repayments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Approve loan (Admin endpoint)
     */
    @PostMapping("/{loanId}/approve")
    public ResponseEntity<?> approveLoan(
            @PathVariable Long loanId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String remarks = request.get("remarks");
            LoanDetailsResponse response = loanService.approveLoan(loanId, remarks, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Reject loan (Admin endpoint)
     */
    @PostMapping("/{loanId}/reject")
    public ResponseEntity<?> rejectLoan(
            @PathVariable Long loanId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            String reason = request.get("reason");
            LoanDetailsResponse response = loanService.rejectLoan(loanId, reason, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Disburse loan (Admin endpoint)
     */
    @PostMapping("/{loanId}/disburse")
    public ResponseEntity<?> disburseLoan(
            @PathVariable Long loanId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            LoanDetailsResponse response = loanService.disburseLoan(loanId, username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
