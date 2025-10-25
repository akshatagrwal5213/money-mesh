package com.bank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "credit_scores")
public class CreditScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private Integer score;  // 300-900
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditScoreCategory category;
    
    @Column(nullable = false)
    private LocalDate calculationDate;
    
    // Factor scores (out of their max contribution)
    private Integer paymentHistoryScore;      // Max 315 (35%)
    private Integer creditUtilizationScore;   // Max 270 (30%)
    private Integer creditHistoryLengthScore; // Max 135 (15%)
    private Integer creditMixScore;           // Max 90 (10%)
    private Integer recentInquiriesScore;     // Max 90 (10%)
    
    // Factor details
    private Double onTimePaymentPercentage;
    private Double creditUtilizationRatio;
    private Integer oldestAccountAgeMonths;
    private Integer numberOfActiveAccounts;
    private Integer hardInquiriesLast6Months;
    
    private Integer previousScore;
    private Integer scoreChange;
    
    @Column(length = 1000)
    private String improvementSuggestions;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        calculationDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public CreditScoreCategory getCategory() {
        return category;
    }

    public void setCategory(CreditScoreCategory category) {
        this.category = category;
    }

    public LocalDate getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(LocalDate calculationDate) {
        this.calculationDate = calculationDate;
    }

    public Integer getPaymentHistoryScore() {
        return paymentHistoryScore;
    }

    public void setPaymentHistoryScore(Integer paymentHistoryScore) {
        this.paymentHistoryScore = paymentHistoryScore;
    }

    public Integer getCreditUtilizationScore() {
        return creditUtilizationScore;
    }

    public void setCreditUtilizationScore(Integer creditUtilizationScore) {
        this.creditUtilizationScore = creditUtilizationScore;
    }

    public Integer getCreditHistoryLengthScore() {
        return creditHistoryLengthScore;
    }

    public void setCreditHistoryLengthScore(Integer creditHistoryLengthScore) {
        this.creditHistoryLengthScore = creditHistoryLengthScore;
    }

    public Integer getCreditMixScore() {
        return creditMixScore;
    }

    public void setCreditMixScore(Integer creditMixScore) {
        this.creditMixScore = creditMixScore;
    }

    public Integer getRecentInquiriesScore() {
        return recentInquiriesScore;
    }

    public void setRecentInquiriesScore(Integer recentInquiriesScore) {
        this.recentInquiriesScore = recentInquiriesScore;
    }

    public Double getOnTimePaymentPercentage() {
        return onTimePaymentPercentage;
    }

    public void setOnTimePaymentPercentage(Double onTimePaymentPercentage) {
        this.onTimePaymentPercentage = onTimePaymentPercentage;
    }

    public Double getCreditUtilizationRatio() {
        return creditUtilizationRatio;
    }

    public void setCreditUtilizationRatio(Double creditUtilizationRatio) {
        this.creditUtilizationRatio = creditUtilizationRatio;
    }

    public Integer getOldestAccountAgeMonths() {
        return oldestAccountAgeMonths;
    }

    public void setOldestAccountAgeMonths(Integer oldestAccountAgeMonths) {
        this.oldestAccountAgeMonths = oldestAccountAgeMonths;
    }

    public Integer getNumberOfActiveAccounts() {
        return numberOfActiveAccounts;
    }

    public void setNumberOfActiveAccounts(Integer numberOfActiveAccounts) {
        this.numberOfActiveAccounts = numberOfActiveAccounts;
    }

    public Integer getHardInquiriesLast6Months() {
        return hardInquiriesLast6Months;
    }

    public void setHardInquiriesLast6Months(Integer hardInquiriesLast6Months) {
        this.hardInquiriesLast6Months = hardInquiriesLast6Months;
    }

    public Integer getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(Integer previousScore) {
        this.previousScore = previousScore;
    }

    public Integer getScoreChange() {
        return scoreChange;
    }

    public void setScoreChange(Integer scoreChange) {
        this.scoreChange = scoreChange;
    }

    public String getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(String improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
