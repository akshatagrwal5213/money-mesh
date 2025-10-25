package com.bank.model;

public enum DeductionType {
    // Section 80C - Max 1.5 lakh
    SECTION_80C_PPF,           // Public Provident Fund
    SECTION_80C_EPF,           // Employee Provident Fund
    SECTION_80C_ELSS,          // Equity Linked Saving Scheme
    SECTION_80C_LIC,           // Life Insurance Premium
    SECTION_80C_NSC,           // National Savings Certificate
    SECTION_80C_FD,            // 5-year Fixed Deposit
    SECTION_80C_TUITION_FEE,   // Children's Tuition Fee
    SECTION_80C_HOME_LOAN_PRINCIPAL, // Home Loan Principal
    
    // Section 80D - Health Insurance
    SECTION_80D_SELF,          // Health Insurance for self (Max 25k)
    SECTION_80D_PARENTS,       // Health Insurance for parents (Max 25k/50k)
    SECTION_80D_PREVENTIVE,    // Preventive Health Checkup (Max 5k)
    
    // Section 80E - Education Loan Interest
    SECTION_80E_EDUCATION_LOAN,
    
    // Section 80G - Donations
    SECTION_80G_DONATION,
    
    // Section 80TTA/TTB - Interest on Savings
    SECTION_80TTA_SAVINGS_INTEREST, // For individuals below 60
    SECTION_80TTB_SENIOR_INTEREST,  // For senior citizens
    
    // Section 24 - Home Loan Interest
    SECTION_24_HOME_LOAN_INTEREST,  // Max 2 lakh
    
    // Standard Deduction
    STANDARD_DEDUCTION,        // 50,000 for salaried
    
    // HRA - House Rent Allowance
    HRA_EXEMPTION,
    
    // Other
    NPS_EMPLOYEE_CONTRIBUTION, // Additional 50k under 80CCD(1B)
    NPS_EMPLOYER_CONTRIBUTION
}
