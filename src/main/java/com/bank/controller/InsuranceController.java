package com.bank.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.ClaimDetailsResponse;
import com.bank.dto.ClaimRequest;
import com.bank.dto.PolicyApplicationRequest;
import com.bank.dto.PolicyDetailsResponse;
import com.bank.dto.PremiumPaymentRequest;
import com.bank.model.InsurancePremiumPayment;
import com.bank.service.InsuranceService;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    
    @Autowired
    private InsuranceService insuranceService;
    
    // Apply for Insurance Policy
    @PostMapping("/apply")
    public ResponseEntity<PolicyDetailsResponse> applyForPolicy(
            @RequestBody PolicyApplicationRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        PolicyDetailsResponse response = insuranceService.applyForPolicy(username, request);
        return ResponseEntity.ok(response);
    }
    
    // Get All User Policies
    @GetMapping("/policies")
    public ResponseEntity<List<PolicyDetailsResponse>> getUserPolicies(Authentication authentication) {
        String username = authentication.getName();
        List<PolicyDetailsResponse> policies = insuranceService.getUserPolicies(username);
        return ResponseEntity.ok(policies);
    }
    
    // Get Policy by ID
    @GetMapping("/policies/{policyId}")
    public ResponseEntity<PolicyDetailsResponse> getPolicyById(
            @PathVariable Long policyId,
            Authentication authentication) {
        String username = authentication.getName();
        PolicyDetailsResponse policy = insuranceService.getPolicyById(username, policyId);
        return ResponseEntity.ok(policy);
    }
    
    // Pay Premium
    @PostMapping("/premium/pay")
    public ResponseEntity<Map<String, String>> payPremium(
            @RequestBody PremiumPaymentRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        String paymentReference = insuranceService.payPremium(username, request);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "paymentReference", paymentReference,
            "message", "Premium paid successfully"
        ));
    }
    
    // Get Premium Payment History
    @GetMapping("/premium/history/{policyId}")
    public ResponseEntity<List<InsurancePremiumPayment>> getPremiumHistory(
            @PathVariable Long policyId,
            Authentication authentication) {
        String username = authentication.getName();
        List<InsurancePremiumPayment> history = insuranceService.getPremiumHistory(username, policyId);
        return ResponseEntity.ok(history);
    }
    
    // File Insurance Claim
    @PostMapping("/claims/file")
    public ResponseEntity<ClaimDetailsResponse> fileClaim(
            @RequestBody ClaimRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        ClaimDetailsResponse claim = insuranceService.fileClaim(username, request);
        return ResponseEntity.ok(claim);
    }
    
    // Get All User Claims
    @GetMapping("/claims")
    public ResponseEntity<List<ClaimDetailsResponse>> getUserClaims(Authentication authentication) {
        String username = authentication.getName();
        List<ClaimDetailsResponse> claims = insuranceService.getUserClaims(username);
        return ResponseEntity.ok(claims);
    }
    
    // Get Claims by Policy
    @GetMapping("/claims/policy/{policyId}")
    public ResponseEntity<List<ClaimDetailsResponse>> getClaimsByPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {
        String username = authentication.getName();
        List<ClaimDetailsResponse> claims = insuranceService.getClaimsByPolicy(username, policyId);
        return ResponseEntity.ok(claims);
    }
    
    // Cancel Policy
    @PostMapping("/policies/{policyId}/cancel")
    public ResponseEntity<PolicyDetailsResponse> cancelPolicy(
            @PathVariable Long policyId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        String username = authentication.getName();
        String reason = request.get("reason");
        PolicyDetailsResponse policy = insuranceService.cancelPolicy(username, policyId, reason);
        return ResponseEntity.ok(policy);
    }
    
    // ===== ADMIN ENDPOINTS =====
    
    // Approve Policy (Admin)
    @PostMapping("/admin/policies/{policyId}/approve")
    public ResponseEntity<PolicyDetailsResponse> approvePolicy(@PathVariable Long policyId) {
        PolicyDetailsResponse policy = insuranceService.approvePolicy(policyId);
        return ResponseEntity.ok(policy);
    }
    
    // Reject Policy (Admin)
    @PostMapping("/admin/policies/{policyId}/reject")
    public ResponseEntity<PolicyDetailsResponse> rejectPolicy(
            @PathVariable Long policyId,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        PolicyDetailsResponse policy = insuranceService.rejectPolicy(policyId, reason);
        return ResponseEntity.ok(policy);
    }
    
    // Approve Claim (Admin)
    @PostMapping("/admin/claims/{claimId}/approve")
    public ResponseEntity<ClaimDetailsResponse> approveClaim(
            @PathVariable Long claimId,
            @RequestBody Map<String, Object> request) {
        BigDecimal approvedAmount = new BigDecimal(request.get("approvedAmount").toString());
        String remarks = (String) request.get("remarks");
        ClaimDetailsResponse claim = insuranceService.approveClaim(claimId, approvedAmount, remarks);
        return ResponseEntity.ok(claim);
    }
    
    // Reject Claim (Admin)
    @PostMapping("/admin/claims/{claimId}/reject")
    public ResponseEntity<ClaimDetailsResponse> rejectClaim(
            @PathVariable Long claimId,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        ClaimDetailsResponse claim = insuranceService.rejectClaim(claimId, reason);
        return ResponseEntity.ok(claim);
    }
    
    // Pay Claim (Admin)
    @PostMapping("/admin/claims/{claimId}/pay")
    public ResponseEntity<ClaimDetailsResponse> payClaim(@PathVariable Long claimId) {
        ClaimDetailsResponse claim = insuranceService.payClaim(claimId);
        return ResponseEntity.ok(claim);
    }
}
