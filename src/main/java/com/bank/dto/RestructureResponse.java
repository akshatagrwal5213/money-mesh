package com.bank.dto;

import com.bank.model.RestructureReason;
import java.time.LocalDate;

public class RestructureResponse {
    
    private Long id;
    private Long loanId;
    private RestructureReason reason;
    private LocalDate requestDate;
    private LocalDate approvalDate;
    private LocalDate effectiveDate;
    private Double originalEmiAmount;
    private Integer originalTenureMonths;
    private Double originalInterestRate;
    private Double originalOutstanding;
    private Double newEmiAmount;
    private Integer newTenureMonths;
    private Double newInterestRate;
    private Double restructuringCharges;
    private Double additionalInterest;
    private Boolean isApproved;
    private Boolean isImplemented;
    private String customerJustification;
    private String bankRemarks;
    
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
}
