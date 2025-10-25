package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.CreditDispute;
import com.bank.service.CreditScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit")
@CrossOrigin(origins = "http://localhost:5173")
public class CreditScoreController {

    @Autowired
    private CreditScoreService creditScoreService;

    /**
     * Calculate credit score for a customer
     */
    @PostMapping("/score/{customerId}")
    public ResponseEntity<CreditScoreResponse> calculateCreditScore(@PathVariable Long customerId) {
        CreditScoreResponse response = creditScoreService.calculateCreditScore(customerId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get credit score history for a customer
     */
    @GetMapping("/score/history/{customerId}")
    public ResponseEntity<List<CreditScoreResponse>> getCreditScoreHistory(
            @PathVariable Long customerId,
            @RequestParam(required = false, defaultValue = "6") Integer months) {
        List<CreditScoreResponse> history = creditScoreService.getCreditScoreHistory(customerId, months);
        return ResponseEntity.ok(history);
    }

    /**
     * Check loan eligibility
     */
    @PostMapping("/eligibility")
    public ResponseEntity<LoanEligibilityResponse> checkLoanEligibility(
            @Valid @RequestBody LoanEligibilityRequest request) {
        LoanEligibilityResponse response = creditScoreService.checkLoanEligibility(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get prequalified loan offers
     */
    @GetMapping("/prequalified/{customerId}")
    public ResponseEntity<List<LoanEligibilityResponse>> getPrequalifiedOffers(@PathVariable Long customerId) {
        // Check eligibility for all loan types
        List<LoanEligibilityResponse> offers = List.of(
            checkAllLoanTypes(customerId)
        ).stream().flatMap(List::stream).toList();
        return ResponseEntity.ok(offers);
    }

    /**
     * File a credit dispute
     */
    @PostMapping("/dispute")
    public ResponseEntity<CreditDispute> fileDispute(@Valid @RequestBody CreditDisputeRequest request) {
        CreditDispute dispute = creditScoreService.fileDispute(request);
        return ResponseEntity.ok(dispute);
    }

    /**
     * Get credit improvement plan
     */
    @GetMapping("/improvement-plan/{customerId}")
    public ResponseEntity<CreditImprovementPlan> getImprovementPlan(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer targetScore) {
        CreditImprovementPlan plan = creditScoreService.getImprovementPlan(customerId, targetScore);
        return ResponseEntity.ok(plan);
    }

    /**
     * Simulate score improvement (what-if analysis)
     */
    @PostMapping("/simulate/{customerId}")
    public ResponseEntity<CreditImprovementPlan> simulateScoreImprovement(
            @PathVariable Long customerId,
            @RequestParam Integer targetScore) {
        CreditImprovementPlan plan = creditScoreService.getImprovementPlan(customerId, targetScore);
        return ResponseEntity.ok(plan);
    }

    /**
     * Helper method to check all loan types
     */
    private List<LoanEligibilityResponse> checkAllLoanTypes(Long customerId) {
        List<LoanEligibilityResponse> responses = new java.util.ArrayList<>();
        
        com.bank.model.LoanType[] loanTypes = com.bank.model.LoanType.values();
        for (com.bank.model.LoanType loanType : loanTypes) {
            try {
                LoanEligibilityRequest request = new LoanEligibilityRequest();
                request.setCustomerId(customerId);
                request.setLoanType(loanType);
                request.setRequestedAmount(1000000.0); // Default check amount
                
                LoanEligibilityResponse response = creditScoreService.checkLoanEligibility(request);
                if (response.getEligible()) {
                    responses.add(response);
                }
            } catch (Exception e) {
                // Skip if error (e.g., no wealth profile)
            }
        }
        
        return responses;
    }
}
