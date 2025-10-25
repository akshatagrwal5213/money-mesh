package com.bank.dto;

import com.bank.model.PrepaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PrepaymentRequest {
    
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    
    @NotNull(message = "Prepayment type is required")
    private PrepaymentType prepaymentType;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double prepaymentAmount;
    
    private String paymentReference;
    
    // Getters and Setters
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

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
