package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_foreclosures")
public class LoanForeclosure {
    
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
    private ForeclosureStatus status;
    
    @Column(nullable = false)
    private LocalDate requestDate;
    
    private LocalDate approvalDate;
    
    private LocalDate foreclosureDate;
    
    @Column(nullable = false)
    private Double outstandingPrincipal;
    
    @Column(nullable = false)
    private Double pendingInterest;
    
    private Double foreclosureCharges;
    
    private Double prepaymentPenalty;
    
    @Column(nullable = false)
    private Double totalAmountDue;
    
    private Double amountPaid;
    
    private LocalDate paymentDate;
    
    @Column(length = 100)
    private String paymentReference;
    
    @Column(nullable = false)
    private Integer remainingEmis;  // EMIs remaining at time of foreclosure
    
    private Double interestSaved;
    
    @Column(length = 1000)
    private String reason;
    
    @Column(length = 500)
    private String bankRemarks;
    
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

    public ForeclosureStatus getStatus() {
        return status;
    }

    public void setStatus(ForeclosureStatus status) {
        this.status = status;
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

    public LocalDate getForeclosureDate() {
        return foreclosureDate;
    }

    public void setForeclosureDate(LocalDate foreclosureDate) {
        this.foreclosureDate = foreclosureDate;
    }

    public Double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(Double outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public Double getPendingInterest() {
        return pendingInterest;
    }

    public void setPendingInterest(Double pendingInterest) {
        this.pendingInterest = pendingInterest;
    }

    public Double getForeclosureCharges() {
        return foreclosureCharges;
    }

    public void setForeclosureCharges(Double foreclosureCharges) {
        this.foreclosureCharges = foreclosureCharges;
    }

    public Double getPrepaymentPenalty() {
        return prepaymentPenalty;
    }

    public void setPrepaymentPenalty(Double prepaymentPenalty) {
        this.prepaymentPenalty = prepaymentPenalty;
    }

    public Double getTotalAmountDue() {
        return totalAmountDue;
    }

    public void setTotalAmountDue(Double totalAmountDue) {
        this.totalAmountDue = totalAmountDue;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Integer getRemainingEmis() {
        return remainingEmis;
    }

    public void setRemainingEmis(Integer remainingEmis) {
        this.remainingEmis = remainingEmis;
    }

    public Double getInterestSaved() {
        return interestSaved;
    }

    public void setInterestSaved(Double interestSaved) {
        this.interestSaved = interestSaved;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getBankRemarks() {
        return bankRemarks;
    }

    public void setBankRemarks(String bankRemarks) {
        this.bankRemarks = bankRemarks;
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
