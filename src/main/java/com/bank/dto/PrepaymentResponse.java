package com.bank.dto;

import com.bank.model.PrepaymentType;
import java.time.LocalDate;

public class PrepaymentResponse {
    
    private Long id;
    private Long loanId;
    private PrepaymentType prepaymentType;
    private Double prepaymentAmount;
    private Double prepaymentCharges;
    private Double outstandingBeforePrepayment;
    private Double outstandingAfterPrepayment;
    private Double interestSaved;
    private Integer tenureReduced;
    private Double newEmiAmount;
    private LocalDate prepaymentDate;
    private String transactionReference;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public PrepaymentType getPrepaymentType() {
        return prepaymentType;
    }

    public void setPrepaymentType(PrepaymentType prepaymentType) {
        this.prepaymentType = prepaymentType;
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

    public LocalDate getPrepaymentDate() {
        return prepaymentDate;
    }

    public void setPrepaymentDate(LocalDate prepaymentDate) {
        this.prepaymentDate = prepaymentDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}
