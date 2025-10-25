package com.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bank.model.InsurancePolicyStatus;
import com.bank.model.InsuranceType;
import com.bank.model.PremiumFrequency;

public class PolicyDetailsResponse {
    private Long id;
    private String policyNumber;
    private InsuranceType insuranceType;
    private String policyName;
    private BigDecimal coverageAmount;
    private BigDecimal premiumAmount;
    private PremiumFrequency premiumFrequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate nextPremiumDueDate;
    private Integer termYears;
    private InsurancePolicyStatus status;
    private String nominee;
    private String nomineeRelation;
    private BigDecimal nomineePercentage;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private String rejectionReason;
    private String remarks;
    private Long customerId;
    private String customerName;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPolicyNumber() {
        return policyNumber;
    }
    
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    
    public InsuranceType getInsuranceType() {
        return insuranceType;
    }
    
    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }
    
    public String getPolicyName() {
        return policyName;
    }
    
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    
    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }
    
    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }
    
    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }
    
    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }
    
    public PremiumFrequency getPremiumFrequency() {
        return premiumFrequency;
    }
    
    public void setPremiumFrequency(PremiumFrequency premiumFrequency) {
        this.premiumFrequency = premiumFrequency;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public LocalDate getNextPremiumDueDate() {
        return nextPremiumDueDate;
    }
    
    public void setNextPremiumDueDate(LocalDate nextPremiumDueDate) {
        this.nextPremiumDueDate = nextPremiumDueDate;
    }
    
    public Integer getTermYears() {
        return termYears;
    }
    
    public void setTermYears(Integer termYears) {
        this.termYears = termYears;
    }
    
    public InsurancePolicyStatus getStatus() {
        return status;
    }
    
    public void setStatus(InsurancePolicyStatus status) {
        this.status = status;
    }
    
    public String getNominee() {
        return nominee;
    }
    
    public void setNominee(String nominee) {
        this.nominee = nominee;
    }
    
    public String getNomineeRelation() {
        return nomineeRelation;
    }
    
    public void setNomineeRelation(String nomineeRelation) {
        this.nomineeRelation = nomineeRelation;
    }
    
    public BigDecimal getNomineePercentage() {
        return nomineePercentage;
    }
    
    public void setNomineePercentage(BigDecimal nomineePercentage) {
        this.nomineePercentage = nomineePercentage;
    }
    
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
