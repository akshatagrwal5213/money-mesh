package com.bank.dto;

import com.bank.model.DeductionType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TaxDeductionRequest {
    
    @NotNull(message = "Deduction type is required")
    private DeductionType deductionType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotNull(message = "Financial year is required")
    private String financialYear;
    
    private String description;
    private String proofDocument;
    
    // Getters and Setters
    
    public DeductionType getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(DeductionType deductionType) {
        this.deductionType = deductionType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProofDocument() {
        return proofDocument;
    }

    public void setProofDocument(String proofDocument) {
        this.proofDocument = proofDocument;
    }
}
