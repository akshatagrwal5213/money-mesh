package com.bank.dto;

import com.bank.model.ForeclosureStatus;
import java.time.LocalDate;

public class ForeclosureResponse {
    
    private Long id;
    private Long loanId;
    private ForeclosureStatus status;
    private LocalDate requestDate;
    private LocalDate approvalDate;
    private LocalDate foreclosureDate;
    private Double outstandingPrincipal;
    private Double pendingInterest;
    private Double foreclosureCharges;
    private Double prepaymentPenalty;
    private Double totalAmountDue;
    private Double amountPaid;
    private LocalDate paymentDate;
    private String paymentReference;
    private Integer remainingEmis;
    private Double interestSaved;
    private String reason;
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
}
