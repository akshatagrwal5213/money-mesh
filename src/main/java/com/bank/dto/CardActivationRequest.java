package com.bank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CardActivationRequest {
    
    @NotNull(message = "Card ID is required")
    private Long cardId;
    
    @NotNull(message = "CVV is required for activation")
    @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
    private String cvv;
    
    @NotNull(message = "Last 4 digits of card number required")
    @Size(min = 4, max = 4, message = "Must provide last 4 digits")
    private String last4Digits;

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }
}
