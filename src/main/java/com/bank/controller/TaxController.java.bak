package com.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.CapitalGainRequest;
import com.bank.dto.TaxCalculationRequest;
import com.bank.dto.TaxCalculationResponse;
import com.bank.dto.TaxDeductionRequest;
import com.bank.dto.TaxPaymentRequest;
import com.bank.dto.TaxSummaryResponse;
import com.bank.model.CapitalGain;
import com.bank.model.TaxDeduction;
import com.bank.model.TaxPayment;
import com.bank.service.TaxService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tax")
@CrossOrigin(origins = "*")
public class TaxController {

    @Autowired
    private TaxService taxService;

    /**
     * Calculate tax
     */
    @PostMapping("/calculate")
    public ResponseEntity<TaxCalculationResponse> calculateTax(
            @Valid @RequestBody TaxCalculationRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        TaxCalculationResponse response = taxService.calculateTaxByUsername(username, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Add tax deduction
     */
    @PostMapping("/deductions")
    public ResponseEntity<TaxDeduction> addDeduction(
            @Valid @RequestBody TaxDeductionRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        TaxDeduction deduction = taxService.addDeductionByUsername(username, request);
        return ResponseEntity.ok(deduction);
    }

    /**
     * Get all deductions for a financial year
     */
    @GetMapping("/deductions/{financialYear}")
    public ResponseEntity<List<TaxDeduction>> getDeductions(
            @PathVariable String financialYear,
            Authentication authentication) {
        
        String username = authentication.getName();
        List<TaxDeduction> deductions = taxService.getDeductionsByUsername(username, financialYear);
        return ResponseEntity.ok(deductions);
    }

    /**
     * Record tax payment
     */
    @PostMapping("/payments")
    public ResponseEntity<TaxPayment> recordPayment(
            @Valid @RequestBody TaxPaymentRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        TaxPayment payment = taxService.recordPaymentByUsername(username, request);
        return ResponseEntity.ok(payment);
    }

    /**
     * Get all tax payments for a financial year
     */
    @GetMapping("/payments/{financialYear}")
    public ResponseEntity<List<TaxPayment>> getPayments(
            @PathVariable String financialYear,
            Authentication authentication) {
        
        String username = authentication.getName();
        List<TaxPayment> payments = taxService.getPaymentsByUsername(username, financialYear);
        return ResponseEntity.ok(payments);
    }

    /**
     * Calculate capital gains
     */
    @PostMapping("/capital-gains")
    public ResponseEntity<CapitalGain> calculateCapitalGain(
            @Valid @RequestBody CapitalGainRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        CapitalGain capitalGain = taxService.calculateCapitalGainByUsername(username, request);
        return ResponseEntity.ok(capitalGain);
    }

    /**
     * Get all capital gains for a financial year
     */
    @GetMapping("/capital-gains/{financialYear}")
    public ResponseEntity<List<CapitalGain>> getCapitalGains(
            @PathVariable String financialYear,
            Authentication authentication) {
        
        String username = authentication.getName();
        List<CapitalGain> capitalGains = taxService.getCapitalGainsByUsername(username, financialYear);
        return ResponseEntity.ok(capitalGains);
    }

    /**
     * Get tax summary for a financial year
     */
    @GetMapping("/summary/{financialYear}")
    public ResponseEntity<TaxSummaryResponse> getTaxSummary(
            @PathVariable String financialYear,
            Authentication authentication) {
        
        String username = authentication.getName();
        TaxSummaryResponse summary = taxService.getTaxSummaryByUsername(username, financialYear);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get current financial year
     */
    @GetMapping("/current-year")
    public ResponseEntity<String> getCurrentFinancialYear() {
        // Simple logic: Apr-Mar financial year
        java.time.LocalDate now = java.time.LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        String fy;
        if (month >= 4) {
            fy = year + "-" + String.valueOf(year + 1).substring(2);
        } else {
            fy = (year - 1) + "-" + String.valueOf(year).substring(2);
        }
        
        return ResponseEntity.ok(fy);
    }
}
