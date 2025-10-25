package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_prepayments")
public class LoanPrepayment {
    
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
    private PrepaymentType prepaymentType;
    
    @Column(nullable = false)
    private LocalDate prepaymentDate;
    
    @Column(nullable = false)
    private Double prepaymentAmount;
    
    private Double prepaymentCharges;  // Fee for prepayment
    
    private Double totalAmountPaid;  // prepaymentAmount + prepaymentCharges
    
    @Column(nullable = false)
    private Double outstandingBeforePrepayment;
    
    @Column(nullable = false)
    private Double outstandingAfterPrepayment;
    
    private Double interestSaved;  // Total interest saved due to prepayment
    
    private Integer tenureReduced;  // Months saved if tenure reduced
    
    private Double newEmiAmount;  // New EMI if EMI is reduced
    
    @Column(length = 1000)
    private String reason;
    
    @Column(nullable = false)
    private String transactionReference;
    
    @Column(length = 500)
    private String remarks;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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

    public PrepaymentType getPrepaymentType() {
        return prepaymentType;
    }

    public void setPrepaymentType(PrepaymentType prepaymentType) {
        this.prepaymentType = prepaymentType;
    }

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public Double getPrepaymentAmount() {
        return prepaymentAmount;
    }

    public void setPrepaymentAmount(Double prepaymentAmount) {
        this.prepaymentAmount = prepaymentAmount;
    }

    public Double getPrepaymentCharges() {
        return prepaymentCharges;
    }

    public void setPrepaymentCharges(Double prepaymentCharges) {
        this.prepaymentCharges = prepaymentCharges;
    }

    public Double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(Double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public Double getOutstandingBeforePrepayment() {
        return outstandingBeforePrepayment;
    }

    public void setOutstandingBeforePrepayment(Double outstandingBeforePrepayment) {
        this.outstandingBeforePrepayment = outstandingBeforePrepayment;
    }

    public Double getOutstandingAfterPrepayment() {
        return outstandingAfterPrepayment;
    }

    public void setOutstandingAfterPrepayment(Double outstandingAfterPrepayment) {
        this.outstandingAfterPrepayment = outstandingAfterPrepayment;
    }

    public Double getInterestSaved() {
        return interestSaved;
    }

    public void setInterestSaved(Double interestSaved) {
        this.interestSaved = interestSaved;
    }

    public Integer getTenureReduced() {
        return tenureReduced;
    }

    public void setTenureReduced(Integer tenureReduced) {
        this.tenureReduced = tenureReduced;
    }

    public Double getNewEmiAmount() {
        return newEmiAmount;
    }

    public void setNewEmiAmount(Double newEmiAmount) {
        this.newEmiAmount = newEmiAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
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
}
