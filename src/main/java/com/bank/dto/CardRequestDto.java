package com.bank.dto;

import com.bank.model.CardType;
import com.bank.model.CardProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CardRequestDto {
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Card type is required")
    private CardType cardType;
    
    @NotNull(message = "Card provider is required")
    private CardProvider cardProvider;
    
    private String holderName;
    
    @Positive(message = "Card limit must be positive")
    private Double cardLimit;
    
    @Positive(message = "Daily limit must be positive")
    private Double dailyLimit;
    
    @Positive(message = "Monthly limit must be positive")
    private Double monthlyLimit;
    
    private Boolean contactlessEnabled = true;
    private Boolean internationalEnabled = false;

    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardProvider getCardProvider() {
        return cardProvider;
    }

    public void setCardProvider(CardProvider cardProvider) {
        this.cardProvider = cardProvider;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
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

    public Boolean getContactlessEnabled() {
        return contactlessEnabled;
    }

    public void setContactlessEnabled(Boolean contactlessEnabled) {
        this.contactlessEnabled = contactlessEnabled;
    }

    public Boolean getInternationalEnabled() {
        return internationalEnabled;
    }

    public void setInternationalEnabled(Boolean internationalEnabled) {
        this.internationalEnabled = internationalEnabled;
    }
}
