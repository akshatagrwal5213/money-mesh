package com.bank.dto;

import jakarta.validation.constraints.NotNull;

public class ForeclosureRequest {
    
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    
    private String reason;
    
    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
