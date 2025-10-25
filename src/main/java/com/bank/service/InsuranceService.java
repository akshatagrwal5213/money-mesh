package com.bank.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.ClaimDetailsResponse;
import com.bank.dto.ClaimRequest;
import com.bank.dto.PolicyApplicationRequest;
import com.bank.dto.PolicyDetailsResponse;
import com.bank.dto.PremiumPaymentRequest;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.ClaimStatus;
import com.bank.model.Customer;
import com.bank.model.InsuranceClaim;
import com.bank.model.InsurancePolicy;
import com.bank.model.InsurancePolicyStatus;
import com.bank.model.InsurancePremiumPayment;
import com.bank.model.InsuranceType;
import com.bank.model.PremiumFrequency;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.InsuranceClaimRepository;
import com.bank.repository.InsurancePolicyRepository;
import com.bank.repository.InsurancePremiumPaymentRepository;

@Service
public class InsuranceService {
    
    @Autowired
    private InsurancePolicyRepository policyRepository;
    
    @Autowired
    private InsuranceClaimRepository claimRepository;
    
    @Autowired
    private InsurancePremiumPaymentRepository premiumPaymentRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    // Apply for Insurance Policy
    @Transactional
    public PolicyDetailsResponse applyForPolicy(String username, PolicyApplicationRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        InsurancePolicy policy = new InsurancePolicy();
        policy.setCustomer(customer);
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setInsuranceType(request.getInsuranceType());
        policy.setPolicyName(request.getPolicyName());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setTermYears(request.getTermYears());
        policy.setPremiumFrequency(request.getPremiumFrequency());
        policy.setNominee(request.getNominee());
        policy.setNomineeRelation(request.getNomineeRelation());
        policy.setNomineePercentage(request.getNomineePercentage() != null ? request.getNomineePercentage() : new BigDecimal("100.00"));
        policy.setRemarks(request.getRemarks());
        policy.setStatus(InsurancePolicyStatus.PENDING_APPROVAL);
        
        // Calculate premium based on coverage, type, and term
        BigDecimal premiumAmount = calculatePremiumAmount(
            request.getCoverageAmount(),
            request.getInsuranceType(),
            request.getTermYears(),
            request.getPremiumFrequency()
        );
        policy.setPremiumAmount(premiumAmount);
        
        // Set dates (policy starts after approval)
        LocalDate startDate = LocalDate.now().plusDays(7); // Start 7 days from now
        policy.setStartDate(startDate);
        policy.setEndDate(startDate.plusYears(request.getTermYears()));
        
        policy = policyRepository.save(policy);
        
        return mapToPolicyDetailsResponse(policy);
    }
    
    // Calculate Premium Amount
    private BigDecimal calculatePremiumAmount(BigDecimal coverageAmount, InsuranceType type, Integer termYears, PremiumFrequency frequency) {
        // Base rate per 1 lakh coverage per year
        BigDecimal baseRate = switch (type) {
            case LIFE -> new BigDecimal("0.05");           // 5% per year
            case HEALTH -> new BigDecimal("0.08");         // 8% per year
            case TERM -> new BigDecimal("0.03");           // 3% per year
            case AUTO -> new BigDecimal("0.06");           // 6% per year
            case HOME -> new BigDecimal("0.04");           // 4% per year
            case TRAVEL -> new BigDecimal("0.10");         // 10% per year
            case ACCIDENT -> new BigDecimal("0.05");       // 5% per year
            case CRITICAL_ILLNESS -> new BigDecimal("0.12"); // 12% per year
            case DISABILITY -> new BigDecimal("0.07");     // 7% per year
            case LOAN_PROTECTION -> new BigDecimal("0.04"); // 4% per year
        };
        
        // Annual premium = Coverage * Rate
        BigDecimal annualPremium = coverageAmount.multiply(baseRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // Adjust based on frequency
        BigDecimal premiumAmount = switch (frequency) {
            case MONTHLY -> annualPremium.divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
            case QUARTERLY -> annualPremium.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
            case HALF_YEARLY -> annualPremium.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            case YEARLY -> annualPremium;
            case ONE_TIME -> annualPremium.multiply(new BigDecimal(termYears)).multiply(new BigDecimal("0.9")); // 10% discount for one-time
        };
        
        return premiumAmount;
    }
    
    // Approve Policy (Admin)
    @Transactional
    public PolicyDetailsResponse approvePolicy(Long policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        if (policy.getStatus() != InsurancePolicyStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Policy is not pending approval");
        }
        
        policy.setStatus(InsurancePolicyStatus.ACTIVE);
        policy.setApprovalDate(LocalDateTime.now());
        
        // Set next premium due date
        policy.setNextPremiumDueDate(calculateNextPremiumDate(policy.getStartDate(), policy.getPremiumFrequency()));
        
        policy = policyRepository.save(policy);
        
        return mapToPolicyDetailsResponse(policy);
    }
    
    // Reject Policy (Admin)
    @Transactional
    public PolicyDetailsResponse rejectPolicy(Long policyId, String reason) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        policy.setStatus(InsurancePolicyStatus.REJECTED);
        policy.setRejectionReason(reason);
        
        policy = policyRepository.save(policy);
        
        return mapToPolicyDetailsResponse(policy);
    }
    
    // Pay Premium
    @Transactional
    public String payPremium(String username, PremiumPaymentRequest request) {
        InsurancePolicy policy = policyRepository.findById(request.getPolicyId())
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to pay premium for this policy");
        }
        
        if (policy.getStatus() != InsurancePolicyStatus.ACTIVE && policy.getStatus() != InsurancePolicyStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Policy is not active");
        }
        
        // Create premium payment record
        InsurancePremiumPayment payment = new InsurancePremiumPayment();
        payment.setPolicy(policy);
        payment.setPaymentReference(generatePaymentReference());
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setRemarks(request.getRemarks());
        payment.setStatus("SUCCESS");
        payment.setTransactionId("TXN" + System.currentTimeMillis());
        
        // Set premium period
        LocalDate periodStart = policy.getNextPremiumDueDate() != null ? policy.getNextPremiumDueDate() : LocalDate.now();
        payment.setPeriodStartDate(periodStart);
        payment.setPeriodEndDate(calculatePeriodEndDate(periodStart, policy.getPremiumFrequency()));
        
        // If payment via account debit
        if ("ACCOUNT_DEBIT".equals(request.getPaymentMethod()) && request.getAccountNumber() != null) {
            Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            
            if (!account.getCustomer().getId().equals(customer.getId())) {
                throw new IllegalStateException("Account does not belong to you");
            }
            
            if (account.getBalance() < request.getAmount().doubleValue()) {
                throw new IllegalStateException("Insufficient balance");
            }
            
            // Deduct from account
            account.setBalance(account.getBalance() - request.getAmount().doubleValue());
            accountRepository.save(account);
            payment.setAccount(account);
        }
        
        premiumPaymentRepository.save(payment);
        
        // Update policy status and next due date
        policy.setStatus(InsurancePolicyStatus.ACTIVE);
        policy.setNextPremiumDueDate(calculateNextPremiumDate(periodStart, policy.getPremiumFrequency()));
        policyRepository.save(policy);
        
        return payment.getPaymentReference();
    }
    
    // File Claim
    @Transactional
    public ClaimDetailsResponse fileClaim(String username, ClaimRequest request) {
        InsurancePolicy policy = policyRepository.findById(request.getPolicyId())
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to file claim for this policy");
        }
        
        if (policy.getStatus() != InsurancePolicyStatus.ACTIVE) {
            throw new IllegalStateException("Policy is not active");
        }
        
        // Check if claim amount is within coverage
        if (request.getClaimAmount().compareTo(policy.getCoverageAmount()) > 0) {
            throw new IllegalStateException("Claim amount exceeds coverage amount");
        }
        
        InsuranceClaim claim = new InsuranceClaim();
        claim.setPolicy(policy);
        claim.setClaimNumber(generateClaimNumber());
        claim.setClaimAmount(request.getClaimAmount());
        claim.setIncidentDate(request.getIncidentDate());
        claim.setDescription(request.getDescription());
        claim.setHospitalName(request.getHospitalName());
        claim.setDoctorName(request.getDoctorName());
        claim.setDocumentsSubmitted(request.getDocumentsSubmitted());
        claim.setStatus(ClaimStatus.SUBMITTED);
        
        claim = claimRepository.save(claim);
        
        return mapToClaimDetailsResponse(claim);
    }
    
    // Approve Claim (Admin)
    @Transactional
    public ClaimDetailsResponse approveClaim(Long claimId, BigDecimal approvedAmount, String remarks) {
        InsuranceClaim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setApprovedAmount(approvedAmount);
        claim.setReviewerRemarks(remarks);
        claim.setApprovedDate(LocalDateTime.now());
        
        claim = claimRepository.save(claim);
        
        return mapToClaimDetailsResponse(claim);
    }
    
    // Reject Claim (Admin)
    @Transactional
    public ClaimDetailsResponse rejectClaim(Long claimId, String reason) {
        InsuranceClaim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setRejectionReason(reason);
        claim.setReviewedDate(LocalDateTime.now());
        
        claim = claimRepository.save(claim);
        
        return mapToClaimDetailsResponse(claim);
    }
    
    // Pay Claim (Admin)
    @Transactional
    public ClaimDetailsResponse payClaim(Long claimId) {
        InsuranceClaim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new ResourceNotFoundException("Claim not found"));
        
        if (claim.getStatus() != ClaimStatus.APPROVED) {
            throw new IllegalStateException("Claim is not approved");
        }
        
        claim.setStatus(ClaimStatus.PAID);
        claim.setPaidAmount(claim.getApprovedAmount());
        claim.setPaymentDate(LocalDateTime.now());
        
        claim = claimRepository.save(claim);
        
        return mapToClaimDetailsResponse(claim);
    }
    
    // Get All User Policies
    public List<PolicyDetailsResponse> getUserPolicies(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        List<InsurancePolicy> policies = policyRepository.findByCustomerId(customer.getId());
        return policies.stream()
            .map(this::mapToPolicyDetailsResponse)
            .collect(Collectors.toList());
    }
    
    // Get Policy by ID
    public PolicyDetailsResponse getPolicyById(String username, Long policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to view this policy");
        }
        
        return mapToPolicyDetailsResponse(policy);
    }
    
    // Get All User Claims
    public List<ClaimDetailsResponse> getUserClaims(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        List<InsuranceClaim> claims = claimRepository.findByCustomerId(customer.getId());
        return claims.stream()
            .map(this::mapToClaimDetailsResponse)
            .collect(Collectors.toList());
    }
    
    // Get Claims by Policy
    public List<ClaimDetailsResponse> getClaimsByPolicy(String username, Long policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to view claims for this policy");
        }
        
        List<InsuranceClaim> claims = claimRepository.findByPolicyId(policyId);
        return claims.stream()
            .map(this::mapToClaimDetailsResponse)
            .collect(Collectors.toList());
    }
    
    // Get Premium Payment History
    public List<InsurancePremiumPayment> getPremiumHistory(String username, Long policyId) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to view premium history for this policy");
        }
        
        return premiumPaymentRepository.findByPolicyId(policyId);
    }
    
    // Cancel Policy
    @Transactional
    public PolicyDetailsResponse cancelPolicy(String username, Long policyId, String reason) {
        InsurancePolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found"));
        
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (!policy.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalStateException("You are not authorized to cancel this policy");
        }
        
        policy.setStatus(InsurancePolicyStatus.CANCELLED);
        policy.setRemarks(reason);
        
        policy = policyRepository.save(policy);
        
        return mapToPolicyDetailsResponse(policy);
    }
    
    // Helper Methods
    private String generatePolicyNumber() {
        return "POL" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private String generateClaimNumber() {
        return "CLM" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private String generatePaymentReference() {
        return "PRM" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private LocalDate calculateNextPremiumDate(LocalDate currentDate, PremiumFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> currentDate.plusMonths(1);
            case QUARTERLY -> currentDate.plusMonths(3);
            case HALF_YEARLY -> currentDate.plusMonths(6);
            case YEARLY -> currentDate.plusYears(1);
            case ONE_TIME -> null; // One-time premium has no next date
        };
    }
    
    private LocalDate calculatePeriodEndDate(LocalDate startDate, PremiumFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> startDate.plusMonths(1).minusDays(1);
            case QUARTERLY -> startDate.plusMonths(3).minusDays(1);
            case HALF_YEARLY -> startDate.plusMonths(6).minusDays(1);
            case YEARLY -> startDate.plusYears(1).minusDays(1);
            case ONE_TIME -> startDate.plusYears(100); // Long period for one-time
        };
    }
    
    private PolicyDetailsResponse mapToPolicyDetailsResponse(InsurancePolicy policy) {
        PolicyDetailsResponse response = new PolicyDetailsResponse();
        response.setId(policy.getId());
        response.setPolicyNumber(policy.getPolicyNumber());
        response.setInsuranceType(policy.getInsuranceType());
        response.setPolicyName(policy.getPolicyName());
        response.setCoverageAmount(policy.getCoverageAmount());
        response.setPremiumAmount(policy.getPremiumAmount());
        response.setPremiumFrequency(policy.getPremiumFrequency());
        response.setStartDate(policy.getStartDate());
        response.setEndDate(policy.getEndDate());
        response.setNextPremiumDueDate(policy.getNextPremiumDueDate());
        response.setTermYears(policy.getTermYears());
        response.setStatus(policy.getStatus());
        response.setNominee(policy.getNominee());
        response.setNomineeRelation(policy.getNomineeRelation());
        response.setNomineePercentage(policy.getNomineePercentage());
        response.setApplicationDate(policy.getApplicationDate());
        response.setApprovalDate(policy.getApprovalDate());
        response.setRejectionReason(policy.getRejectionReason());
        response.setRemarks(policy.getRemarks());
        response.setCustomerId(policy.getCustomer().getId());
        response.setCustomerName(policy.getCustomer().getName());
        return response;
    }
    
    private ClaimDetailsResponse mapToClaimDetailsResponse(InsuranceClaim claim) {
        ClaimDetailsResponse response = new ClaimDetailsResponse();
        response.setId(claim.getId());
        response.setClaimNumber(claim.getClaimNumber());
        response.setPolicyId(claim.getPolicy().getId());
        response.setPolicyNumber(claim.getPolicy().getPolicyNumber());
        response.setPolicyName(claim.getPolicy().getPolicyName());
        response.setClaimAmount(claim.getClaimAmount());
        response.setIncidentDate(claim.getIncidentDate());
        response.setDescription(claim.getDescription());
        response.setHospitalName(claim.getHospitalName());
        response.setDoctorName(claim.getDoctorName());
        response.setDocumentsSubmitted(claim.getDocumentsSubmitted());
        response.setStatus(claim.getStatus());
        response.setApprovedAmount(claim.getApprovedAmount());
        response.setPaidAmount(claim.getPaidAmount());
        response.setSubmittedDate(claim.getSubmittedDate());
        response.setReviewedDate(claim.getReviewedDate());
        response.setApprovedDate(claim.getApprovedDate());
        response.setPaymentDate(claim.getPaymentDate());
        response.setRejectionReason(claim.getRejectionReason());
        response.setReviewerRemarks(claim.getReviewerRemarks());
        return response;
    }
}
