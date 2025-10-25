package com.bank.model;

public enum TaxPaymentType {
    ADVANCE_TAX_Q1,        // Advance tax - Quarter 1 (15th June)
    ADVANCE_TAX_Q2,        // Advance tax - Quarter 2 (15th September)
    ADVANCE_TAX_Q3,        // Advance tax - Quarter 3 (15th December)
    ADVANCE_TAX_Q4,        // Advance tax - Quarter 4 (15th March)
    SELF_ASSESSMENT_TAX,   // Self-assessment tax
    TDS_PAYMENT,           // TDS payment by employer
    TCS_PAYMENT,           // TCS payment
    REGULAR_ASSESSMENT,    // After income tax notice
    PENALTY,               // Penalty payment
    INTEREST_234A,         // Interest for delay in filing return
    INTEREST_234B,         // Interest for delay in payment of advance tax
    INTEREST_234C          // Interest for deferment of advance tax
}
