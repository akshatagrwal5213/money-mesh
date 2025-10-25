package com.bank.model;

public enum CardStatus {
    ACTIVE,          // Card is active and can be used
    BLOCKED,         // Card is temporarily blocked by user or system
    EXPIRED,         // Card has passed expiry date
    PENDING,         // Card issued but not yet activated
    CANCELLED,       // Card permanently cancelled
    LOST,            // Card reported as lost
    STOLEN           // Card reported as stolen
}
