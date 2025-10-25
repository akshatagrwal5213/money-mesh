package com.bank.dto;

import com.bank.model.BillType;
import com.bank.model.PaymentStatus;
import com.bank.model.PaymentMethod;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BillPaymentResponse {
    
    private String billPaymentId;
    private BillType billType;
    private String billerName;
    private String consumerNumber;
    private Double billAmount;
    private Double convenienceFee;
    private Double totalAmount;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private String referenceNumber;
    private String transactionId;
    private String receiptNumber;
    private String message;

    // Constructors
    public BillPaymentResponse() {}

    public BillPaymentResponse(String billPaymentId, PaymentStatus paymentStatus, String receiptNumber, String message) {
        this.billPaymentId = billPaymentId;
        this.paymentStatus = paymentStatus;
        this.receiptNumber = receiptNumber;
        this.message = message;
    }

    // Getters and Setters
    public String getBillPaymentId() {
        return billPaymentId;
    }

    public void setBillPaymentId(String billPaymentId) {
        this.billPaymentId = billPaymentId;
    }

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

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
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

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
