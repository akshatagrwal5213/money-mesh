package com.bank.model;

public enum ReferralStatus {
    PENDING,        // Referral sent, not yet signed up
    REGISTERED,     // Friend registered account
    QUALIFIED,      // Friend met qualification criteria (e.g., first transaction)
    REWARDED,       // Bonus points credited
    EXPIRED,        // Referral link expired
    REJECTED        // Referral invalid/rejected
}
