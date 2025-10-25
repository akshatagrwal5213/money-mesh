package com.bank.model;

public enum RedemptionType {
    CASH,               // Convert to cash in account
    BILL_PAYMENT,       // Pay bills with points
    LOAN_EMI,           // Pay EMI with points
    VOUCHER,            // Gift vouchers
    DONATION,           // Donate to charity
    PRODUCT,            // Physical products
    TRAVEL,             // Travel bookings
    STATEMENT_CREDIT    // Credit to account statement
}
