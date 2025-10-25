package com.bank.dto;

import com.bank.model.BillType;
import com.bank.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class BillPaymentRequest {
    
    @NotNull(message = "Bill type is required")
    private BillType billType;
    
    @NotBlank(message = "Biller name is required")
    private String billerName;
    
    @NotBlank(message = "Biller code is required")
    private String billerCode;
    
    @NotBlank(message = "Consumer number is required")
    private String consumerNumber;
    
    @NotNull(message = "Bill amount is required")
    @Positive(message = "Bill amount must be positive")
    private Double billAmount;
    
    private Double convenienceFee;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    private LocalDate dueDate;
    
    private String remarks;
    
    private boolean enableAutoPay;

    // Getters and Setters
    public BillType getBillType() {
        return billType;
    }

    public void setBillType(BillType billType) {
        this.billType = billType;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(String consumerNumber) {
        this.consumerNumber = consumerNumber;
    }

    public Double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(Double billAmount) {
        this.billAmount = billAmount;
    }

    public Double getConvenienceFee() {
        return convenienceFee;
    }

    public void setConvenienceFee(Double convenienceFee) {
        this.convenienceFee = convenienceFee;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isEnableAutoPay() {
        return enableAutoPay;
    }

    public void setEnableAutoPay(boolean enableAutoPay) {
        this.enableAutoPay = enableAutoPay;
    }
}
