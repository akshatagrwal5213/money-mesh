package com.bank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_eligibilities")
public class LoanEligibility {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityStatus eligibilityStatus;
    
    @Column(nullable = false)
    private Boolean eligible;
    
    private Double maxEligibleAmount;
    
    private Double interestRate;  // Annual interest rate offered
    
    private Integer maxTenureMonths;
    
    private Integer creditScoreAtCheck;
    
    @Column(length = 1000)
    private String eligibilityReasons;  // Reasons for eligibility/ineligibility
    
    @Column(length = 500)
    private String conditions;  // Any conditions for approval
    
    @Column(nullable = false)
    private LocalDate checkDate;
    
    private LocalDate expiryDate;  // Pre-qualification expires after X days
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (checkDate == null) {
            checkDate = LocalDate.now();
        }
        if (expiryDate == null) {
            expiryDate = checkDate.plusDays(30);  // Valid for 30 days
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public Boolean getEligible() {
        return eligible;
    }

    public void setEligible(Boolean eligible) {
        this.eligible = eligible;
    }

    public Double getMaxEligibleAmount() {
        return maxEligibleAmount;
    }

    public void setMaxEligibleAmount(Double maxEligibleAmount) {
        this.maxEligibleAmount = maxEligibleAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getMaxTenureMonths() {
        return maxTenureMonths;
    }

    public void setMaxTenureMonths(Integer maxTenureMonths) {
        this.maxTenureMonths = maxTenureMonths;
    }

    public Integer getCreditScoreAtCheck() {
        return creditScoreAtCheck;
    }

    public void setCreditScoreAtCheck(Integer creditScoreAtCheck) {
        this.creditScoreAtCheck = creditScoreAtCheck;
    }

    public String getEligibilityReasons() {
        return eligibilityReasons;
    }

    public void setEligibilityReasons(String eligibilityReasons) {
        this.eligibilityReasons = eligibilityReasons;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
