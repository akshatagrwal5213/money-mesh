package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreditDisputeRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotBlank(message = "Account reference is required")
    private String accountReference;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String supportingDocuments;

    // Constructors
    public CreditDisputeRequest() {}

    public CreditDisputeRequest(Long customerId, String accountReference, String reason) {
        this.customerId = customerId;
        this.accountReference = accountReference;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountReference() {
        return accountReference;
    }

    public void setAccountReference(String accountReference) {
        this.accountReference = accountReference;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSupportingDocuments() {
        return supportingDocuments;
    }

    public void setSupportingDocuments(String supportingDocuments) {
        this.supportingDocuments = supportingDocuments;
    }
}
