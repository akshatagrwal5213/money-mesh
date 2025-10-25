package com.bank.dto;

import com.bank.model.QrCodeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class QrCodeRequest {
    
    @NotNull(message = "QR code type is required")
    private QrCodeType type;
    
    @Positive(message = "Amount must be positive")
    private Double amount;  // Required for STATIC, optional for DYNAMIC
    
    private String merchantName;
    
    private String merchantVpa;  // UPI VPA for merchant QR
    
    private Integer validityMinutes;  // For dynamic QR codes
    
    private Integer maxUsageLimit;  // Max number of times QR can be used

    // Getters and Setters
    public QrCodeType getType() {
        return type;
    }

    public void setType(QrCodeType type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantVpa() {
        return merchantVpa;
    }

    public void setMerchantVpa(String merchantVpa) {
        this.merchantVpa = merchantVpa;
    }

    public Integer getValidityMinutes() {
        return validityMinutes;
    }

    public void setValidityMinutes(Integer validityMinutes) {
        this.validityMinutes = validityMinutes;
    }

    public Integer getMaxUsageLimit() {
        return maxUsageLimit;
    }

    public void setMaxUsageLimit(Integer maxUsageLimit) {
        this.maxUsageLimit = maxUsageLimit;
    }
}
