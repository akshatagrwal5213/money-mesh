package com.bank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CardLimitsUpdateRequest {
    
    @NotNull(message = "Card ID is required")
    private Long cardId;
    
    @Positive(message = "Card limit must be positive")
    private Double cardLimit;
    
    @Positive(message = "Daily limit must be positive")
    private Double dailyLimit;
    
    @Positive(message = "Monthly limit must be positive")
    private Double monthlyLimit;

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Double getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(Double cardLimit) {
        this.cardLimit = cardLimit;
    }

    public Double getDailyLimit() {
        return dailyLimit;
    }

    public void setDailyLimit(Double dailyLimit) {
        this.dailyLimit = dailyLimit;
    }

    public Double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(Double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}
