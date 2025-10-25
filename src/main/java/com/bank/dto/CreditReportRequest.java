package com.bank.dto;

import jakarta.validation.constraints.NotNull;

public class CreditReportRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private Boolean includeSuggestions = true;
    
    private Boolean includeHistory = true;
    
    private Integer historyMonths = 6;  // Default 6 months history

    // Constructors
    public CreditReportRequest() {}

    public CreditReportRequest(Long customerId) {
        this.customerId = customerId;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Boolean getIncludeSuggestions() {
        return includeSuggestions;
    }

    public void setIncludeSuggestions(Boolean includeSuggestions) {
        this.includeSuggestions = includeSuggestions;
    }

    public Boolean getIncludeHistory() {
        return includeHistory;
    }

    public void setIncludeHistory(Boolean includeHistory) {
        this.includeHistory = includeHistory;
    }

    public Integer getHistoryMonths() {
        return historyMonths;
    }

    public void setHistoryMonths(Integer historyMonths) {
        this.historyMonths = historyMonths;
    }
}
