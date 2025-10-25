package com.bank.dto;

import com.bank.model.PaymentStatus;
import com.bank.model.UpiProvider;
import java.time.LocalDateTime;

public class UpiPaymentResponse {
    
    private String upiTransactionId;
    private String upiId;
    private String receiverUpiId;
    private Double amount;
    private UpiProvider provider;
    private PaymentStatus status;
    private String referenceNumber;
    private String description;
    private LocalDateTime transactionDate;
    private String message;

    // Constructors
    public UpiPaymentResponse() {}

    public UpiPaymentResponse(String upiTransactionId, PaymentStatus status, String referenceNumber, String message) {
        this.upiTransactionId = upiTransactionId;
        this.status = status;
        this.referenceNumber = referenceNumber;
        this.message = message;
    }

    // Getters and Setters
    public String getUpiTransactionId() {
        return upiTransactionId;
    }

    public void setUpiTransactionId(String upiTransactionId) {
        this.upiTransactionId = upiTransactionId;
    }

    public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }

    public String getReceiverUpiId() {
        return receiverUpiId;
    }

    public void setReceiverUpiId(String receiverUpiId) {
        this.receiverUpiId = receiverUpiId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public UpiProvider getProvider() {
        return provider;
    }

    public void setProvider(UpiProvider provider) {
        this.provider = provider;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
