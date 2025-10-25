package com.bank.model;

public enum EligibilityStatus {
    ELIGIBLE,                   // Fully eligible
    CONDITIONALLY_ELIGIBLE,     // Eligible with conditions (higher rate, lower amount)
    NOT_ELIGIBLE                // Not eligible
}
