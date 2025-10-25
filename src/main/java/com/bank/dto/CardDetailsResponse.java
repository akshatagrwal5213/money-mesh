package com.bank.dto;

import com.bank.model.CardType;
import com.bank.model.CardStatus;
import com.bank.model.CardProvider;
import java.time.LocalDate;

public class CardDetailsResponse {
    
    private Long id;
    private String cardNumber;  // Masked for security
    private String last4Digits;
    private LocalDate expiryDate;
    private LocalDate issuedDate;
    private CardType cardType;
    private CardStatus cardStatus;
    private CardProvider cardProvider;
    private Double cardLimit;
    private Double dailyLimit;
    private Double monthlyLimit;
    private Double availableLimit;
    private String holderName;
    private Long accountId;
    private String accountNumber;
    private Boolean contactlessEnabled;
    private Boolean internationalEnabled;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getLast4Digits() {
        return last4Digits;
    }

    public void setLast4Digits(String last4Digits) {
        this.last4Digits = last4Digits;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }

    public CardProvider getCardProvider() {
        return cardProvider;
    }

    public void setCardProvider(CardProvider cardProvider) {
        this.cardProvider = cardProvider;
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

    public Double getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(Double availableLimit) {
        this.availableLimit = availableLimit;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
