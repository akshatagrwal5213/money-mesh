package com.bank.dto;

import com.bank.model.QrCodeType;
import java.time.LocalDateTime;

public class QrCodeResponse {
    
    private String qrCodeId;
    private QrCodeType type;
    private String qrData;  // The actual QR code data/string
    private String qrImageBase64;  // Base64 encoded QR code image (optional)
    private Double amount;
    private String merchantName;
    private LocalDateTime generatedDate;
    private LocalDateTime expiryDate;
    private boolean isActive;
    private Integer usageCount;
    private String message;

    // Constructors
    public QrCodeResponse() {}

    public QrCodeResponse(String qrCodeId, String qrData, String message) {
        this.qrCodeId = qrCodeId;
        this.qrData = qrData;
        this.message = message;
    }

    // Getters and Setters
    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public QrCodeType getType() {
        return type;
    }

    public void setType(QrCodeType type) {
        this.type = type;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getQrImageBase64() {
        return qrImageBase64;
    }

    public void setQrImageBase64(String qrImageBase64) {
        this.qrImageBase64 = qrImageBase64;
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

    public LocalDateTime getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDateTime generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
