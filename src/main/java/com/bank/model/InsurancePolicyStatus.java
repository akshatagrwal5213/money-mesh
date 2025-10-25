package com.bank.model;

public enum InsurancePolicyStatus {
    PENDING_APPROVAL,   // Application submitted, awaiting approval
    ACTIVE,             // Policy is active and valid
    PENDING_PAYMENT,    // Awaiting premium payment
    LAPSED,             // Policy lapsed due to non-payment
    EXPIRED,            // Policy expired naturally
    CANCELLED,          // Policy cancelled by user
    REJECTED,           // Application rejected
    MATURED             // Policy matured (term completed)
}
