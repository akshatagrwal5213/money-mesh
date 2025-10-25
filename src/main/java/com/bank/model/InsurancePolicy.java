package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "insurance_policies")
public class InsurancePolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 20)
    private String policyNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InsuranceType insuranceType;
    
    @Column(nullable = false, length = 100)
    private String policyName;
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal coverageAmount;  // Sum assured
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PremiumFrequency premiumFrequency;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column
    private LocalDate nextPremiumDueDate;
    
    @Column(nullable = false)
    private Integer termYears;  // Policy term in years
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InsurancePolicyStatus status;
    
    @Column(length = 100)
    private String nominee;
    
    @Column(length = 50)
    private String nomineeRelation;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal nomineePercentage = new BigDecimal("100.00");
    
    @Column(nullable = false)
    private LocalDateTime applicationDate;
    
    @Column
    private LocalDateTime approvalDate;
    
    @Column(length = 200)
    private String rejectionReason;
    
    @Column(length = 500)
    private String remarks;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (applicationDate == null) {
            applicationDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
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
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
