package com.bank.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.bank.model.CreditScoreCategory;

public class CreditScoreResponse {
    
    private Long id;
    private Integer score;
    private CreditScoreCategory category;
    private LocalDate calculationDate;
    
    // Factor breakdown
    private Integer paymentHistoryScore;
    private Integer creditUtilizationScore;
    private Integer creditHistoryLengthScore;
    private Integer creditMixScore;
    private Integer recentInquiriesScore;
    
    // Metrics
    private Double onTimePaymentPercentage;
    private Double creditUtilizationRatio;
    private Integer oldestAccountAgeMonths;
    private Integer numberOfActiveAccounts;
    private Integer hardInquiriesLast6Months;
    
    // Progress tracking
    private Integer previousScore;
    private Integer scoreChange;
    private String trend;  // "IMPROVING", "DECLINING", "STABLE"
    
    // Improvement suggestions
    private List<String> improvementSuggestions;
    
    // Factor impact percentages
    private Map<String, Double> factorImpact;

    // Constructors
    public CreditScoreResponse() {}

    public CreditScoreResponse(Long id, Integer score, CreditScoreCategory category, LocalDate calculationDate) {
        this.id = id;
        this.score = score;
        this.category = category;
        this.calculationDate = calculationDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public List<String> getImprovementSuggestions() {
        return improvementSuggestions;
    }

    public void setImprovementSuggestions(List<String> improvementSuggestions) {
        this.improvementSuggestions = improvementSuggestions;
    }

    public Map<String, Double> getFactorImpact() {
        return factorImpact;
    }

    public void setFactorImpact(Map<String, Double> factorImpact) {
        this.factorImpact = factorImpact;
    }
}
