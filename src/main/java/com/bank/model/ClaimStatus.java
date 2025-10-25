package com.bank.model;

public enum ClaimStatus {
    SUBMITTED,          // Claim submitted
    UNDER_REVIEW,       // Being reviewed
    PENDING_DOCUMENTS,  // Waiting for additional documents
    APPROVED,           // Claim approved
    REJECTED,           // Claim rejected
    PAID,               // Claim amount paid
    CLOSED              // Claim closed
}
