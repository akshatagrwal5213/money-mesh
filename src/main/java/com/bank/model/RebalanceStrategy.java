package com.bank.model;

public enum RebalanceStrategy {
    MONTHLY,          // Rebalance every month
    QUARTERLY,        // Rebalance every quarter
    SEMI_ANNUALLY,    // Rebalance twice a year
    ANNUALLY,         // Rebalance once a year
    THRESHOLD_BASED   // Rebalance when deviation exceeds threshold
}
