package com.bank.dto;

import com.bank.model.LoanType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LoanEligibilityRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotNull(message = "Loan type is required")
    private LoanType loanType;
    
    @NotNull(message = "Requested amount is required")
    @Positive(message = "Requested amount must be positive")
    private Double requestedAmount;
    
    private Integer requestedTenureMonths;
    
    private Double monthlyIncome;  // Optional override

    // Constructors
    public LoanEligibilityRequest() {}

    public LoanEligibilityRequest(Long customerId, LoanType loanType, Double requestedAmount) {
        this.customerId = customerId;
        this.loanType = loanType;
        this.requestedAmount = requestedAmount;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Integer getRequestedTenureMonths() {
        return requestedTenureMonths;
    }

    public void setRequestedTenureMonths(Integer requestedTenureMonths) {
        this.requestedTenureMonths = requestedTenureMonths;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }
}
