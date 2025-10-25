package com.bank.model;

public enum TaxDocumentType {
    FORM_16,           // TDS certificate from employer
    FORM_16A,          // TDS certificate for non-salary income
    FORM_26AS,         // Annual tax statement
    ITR_1,             // Income Tax Return - Sahaj
    ITR_2,             // Income Tax Return - For capital gains
    ITR_3,             // Income Tax Return - For business income
    ITR_4,             // Income Tax Return - Sugam
    COMPUTATION_SHEET, // Tax computation summary
    ADVANCE_TAX_CHALLAN, // Advance tax payment receipt
    SELF_ASSESSMENT_CHALLAN, // Self-assessment tax payment
    TDS_CERTIFICATE,   // Generic TDS certificate
    CAPITAL_GAINS_STATEMENT
}
