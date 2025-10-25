package com.bank.model;

public enum PaymentStatus {
    INITIATED,         // Payment initiated
    PENDING,           // Awaiting confirmation
    PROCESSING,        // Being processed
    SUCCESS,           // Payment successful
    FAILED,            // Payment failed
    CANCELLED,         // Payment cancelled by user
    REFUNDED,          // Payment refunded
    EXPIRED            // Payment link/session expired
}
