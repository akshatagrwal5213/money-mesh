package com.bank.dto;

import com.bank.model.RestructureReason;
import jakarta.validation.constraints.NotNull;

public class RestructureRequest {
    
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    
    @NotNull(message = "Reason is required")
    private RestructureReason reason;
    
    private Integer newTenureMonths;
    private Double newInterestRate;
    
    @NotNull(message = "Justification is required")
    private String customerJustification;
    
    // Getters and Setters
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

    public String getCustomerJustification() {
        return customerJustification;
    }

    public void setCustomerJustification(String customerJustification) {
        this.customerJustification = customerJustification;
    }
}
