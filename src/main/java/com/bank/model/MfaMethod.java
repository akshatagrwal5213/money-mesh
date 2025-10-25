package com.bank.model;

public enum MfaMethod {
    EMAIL,
    SMS,
    TOTP, // Time-based One-Time Password (Google Authenticator, etc.)
    NONE
}
