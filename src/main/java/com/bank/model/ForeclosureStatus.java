package com.bank.model;

public enum ForeclosureStatus {
    REQUESTED,      // Foreclosure request submitted
    APPROVED,       // Bank approved foreclosure
    PROCESSING,     // Processing foreclosure payment
    COMPLETED,      // Loan fully closed
    REJECTED        // Request rejected
}
