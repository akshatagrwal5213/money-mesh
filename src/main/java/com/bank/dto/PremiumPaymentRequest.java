package com.bank.dto;

import java.math.BigDecimal;

public class PremiumPaymentRequest {
    private Long policyId;
    private BigDecimal amount;
    private String paymentMethod;  // ACCOUNT_DEBIT, CARD, UPI, NETBANKING
    private String accountNumber;  // If paying via account debit
    private String remarks;
    
    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
