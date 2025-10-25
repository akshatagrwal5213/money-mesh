package com.bank.dto;

import com.bank.model.TaxPaymentType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TaxPaymentRequest {
    
    @NotNull(message = "Payment type is required")
    private TaxPaymentType paymentType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Financial year is required")
    private String financialYear;
    
    @NotNull(message = "Assessment year is required")
    private String assessmentYear;
    
    private String challanNumber;
    private String acknowledgementNumber;
    private String remarks;
    
    // Getters and Setters
    
    public TaxPaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(TaxPaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getAssessmentYear() {
        return assessmentYear;
    }

    public void setAssessmentYear(String assessmentYear) {
        this.assessmentYear = assessmentYear;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public String getAcknowledgementNumber() {
        return acknowledgementNumber;
    }

    public void setAcknowledgementNumber(String acknowledgementNumber) {
        this.acknowledgementNumber = acknowledgementNumber;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
