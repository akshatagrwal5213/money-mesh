package com.bank.model;

public enum CreditFactorType {
    PAYMENT_HISTORY,        // 35% weight
    CREDIT_UTILIZATION,     // 30% weight
    CREDIT_HISTORY_LENGTH,  // 15% weight
    CREDIT_MIX,             // 10% weight
    RECENT_INQUIRIES        // 10% weight
}
