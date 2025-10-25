package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_restructures")
public class LoanRestructure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestructureReason reason;
    
    @Column(nullable = false)
    private LocalDate requestDate;
    
    private LocalDate approvalDate;
    
    private LocalDate effectiveDate;
    
    // Original loan terms
    @Column(nullable = false)
    private Double originalEmiAmount;
    
    @Column(nullable = false)
    private Integer originalTenureMonths;
    
    @Column(nullable = false)
    private Double originalInterestRate;
    
    @Column(nullable = false)
    private Double originalOutstanding;
    
    // New loan terms
    private Double newEmiAmount;
    
    private Integer newTenureMonths;
    
    private Double newInterestRate;
    
    private Double restructuringCharges;
    
    private Double additionalInterest;  // Extra interest due to restructuring
    
    @Column(length = 1000)
    private String customerJustification;
    
    @Column(length = 1000)
    private String bankRemarks;
    
    @Column(nullable = false)
    private Boolean isApproved = false;
    
    @Column(nullable = false)
    private Boolean isImplemented = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RestructureReason getReason() {
        return reason;
    }

    public void setReason(RestructureReason reason) {
        this.reason = reason;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Double getOriginalEmiAmount() {
        return originalEmiAmount;
    }

    public void setOriginalEmiAmount(Double originalEmiAmount) {
        this.originalEmiAmount = originalEmiAmount;
    }

    public Integer getOriginalTenureMonths() {
        return originalTenureMonths;
    }

    public void setOriginalTenureMonths(Integer originalTenureMonths) {
        this.originalTenureMonths = originalTenureMonths;
    }

    public Double getOriginalInterestRate() {
        return originalInterestRate;
    }

    public void setOriginalInterestRate(Double originalInterestRate) {
        this.originalInterestRate = originalInterestRate;
    }

    public Double getOriginalOutstanding() {
        return originalOutstanding;
    }

    public void setOriginalOutstanding(Double originalOutstanding) {
        this.originalOutstanding = originalOutstanding;
    }

    public Double getNewEmiAmount() {
        return newEmiAmount;
    }

    public void setNewEmiAmount(Double newEmiAmount) {
        this.newEmiAmount = newEmiAmount;
    }

    public Integer getNewTenureMonths() {
        return newTenureMonths;
    }

    public void setNewTenureMonths(Integer newTenureMonths) {
        this.newTenureMonths = newTenureMonths;
    }

    public Double getNewInterestRate() {
        return newInterestRate;
    }

    public void setNewInterestRate(Double newInterestRate) {
        this.newInterestRate = newInterestRate;
    }

    public Double getRestructuringCharges() {
        return restructuringCharges;
    }

    public void setRestructuringCharges(Double restructuringCharges) {
        this.restructuringCharges = restructuringCharges;
    }

    public Double getAdditionalInterest() {
        return additionalInterest;
    }

    public void setAdditionalInterest(Double additionalInterest) {
        this.additionalInterest = additionalInterest;
    }

    public String getCustomerJustification() {
        return customerJustification;
    }

    public void setCustomerJustification(String customerJustification) {
        this.customerJustification = customerJustification;
    }

    public String getBankRemarks() {
        return bankRemarks;
    }

    public void setBankRemarks(String bankRemarks) {
        this.bankRemarks = bankRemarks;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Boolean getIsImplemented() {
        return isImplemented;
    }

    public void setIsImplemented(Boolean isImplemented) {
        this.isImplemented = isImplemented;
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
