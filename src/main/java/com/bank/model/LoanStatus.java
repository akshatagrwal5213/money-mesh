package com.bank.model;

public enum LoanStatus {
    PENDING,           // Application submitted, awaiting review
    UNDER_REVIEW,      // Being reviewed by bank
    APPROVED,          // Approved, awaiting disbursement
    DISBURSED,         // Loan amount disbursed
    ACTIVE,            // Active loan with ongoing repayments
    CLOSED,            // Loan fully repaid
    REJECTED,          // Application rejected
    DEFAULTED,         // Loan defaulted
    CANCELLED          // Application cancelled by user
}
