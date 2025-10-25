package com.bank.model;

public enum OverdueStatus {
    CURRENT,        // No overdue (on time)
    OVERDUE_1_30,   // 1-30 days late
    OVERDUE_31_60,  // 31-60 days late
    OVERDUE_61_90,  // 61-90 days late
    OVERDUE_90_PLUS, // 90+ days late (critical)
    NPA             // Non-Performing Asset
}
