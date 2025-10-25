package com.bank.dto;

import java.time.LocalDate;
import java.util.List;

import com.bank.model.HealthScoreCategory;

public class FinancialHealthResponse {
    private LocalDate scoreDate;
    private Double overallScore;
    private HealthScoreCategory category;
    
    // Component scores
    private Double savingsScore;
    private Double debtScore;
    private Double emergencyFundScore;
    private Double investmentScore;
    private Double insuranceScore;
    private Double retirementScore;
    
    // Metrics
    private Double savingsRate;
    private Double debtToIncomeRatio;
    private Double emergencyFundMonths;
    private Double investmentDiversity;
    
    private List<String> improvementRecommendations;
    private Double previousOverallScore;
    private Double scoreImprovement;

    // Getters and Setters
    public LocalDate getScoreDate() { return scoreDate; }
    public void setScoreDate(LocalDate scoreDate) { this.scoreDate = scoreDate; }
    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
    public HealthScoreCategory getCategory() { return category; }
    public void setCategory(HealthScoreCategory category) { this.category = category; }
    public Double getSavingsScore() { return savingsScore; }
    public void setSavingsScore(Double savingsScore) { this.savingsScore = savingsScore; }
    public Double getDebtScore() { return debtScore; }
    public void setDebtScore(Double debtScore) { this.debtScore = debtScore; }
    public Double getEmergencyFundScore() { return emergencyFundScore; }
    public void setEmergencyFundScore(Double emergencyFundScore) { this.emergencyFundScore = emergencyFundScore; }
    public Double getInvestmentScore() { return investmentScore; }
    public void setInvestmentScore(Double investmentScore) { this.investmentScore = investmentScore; }
    public Double getInsuranceScore() { return insuranceScore; }
    public void setInsuranceScore(Double insuranceScore) { this.insuranceScore = insuranceScore; }
    public Double getRetirementScore() { return retirementScore; }
    public void setRetirementScore(Double retirementScore) { this.retirementScore = retirementScore; }
    public Double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(Double savingsRate) { this.savingsRate = savingsRate; }
    public Double getDebtToIncomeRatio() { return debtToIncomeRatio; }
    public void setDebtToIncomeRatio(Double debtToIncomeRatio) { this.debtToIncomeRatio = debtToIncomeRatio; }
    public Double getEmergencyFundMonths() { return emergencyFundMonths; }
    public void setEmergencyFundMonths(Double emergencyFundMonths) { this.emergencyFundMonths = emergencyFundMonths; }
    public Double getInvestmentDiversity() { return investmentDiversity; }
    public void setInvestmentDiversity(Double investmentDiversity) { this.investmentDiversity = investmentDiversity; }
    public List<String> getImprovementRecommendations() { return improvementRecommendations; }
    public void setImprovementRecommendations(List<String> improvementRecommendations) { this.improvementRecommendations = improvementRecommendations; }
    public Double getPreviousOverallScore() { return previousOverallScore; }
    public void setPreviousOverallScore(Double previousOverallScore) { this.previousOverallScore = previousOverallScore; }
    public Double getScoreImprovement() { return scoreImprovement; }
    public void setScoreImprovement(Double scoreImprovement) { this.scoreImprovement = scoreImprovement; }
}
