package com.bank.dto;

import java.time.LocalDate;
import java.util.List;

import com.bank.model.AssetClass;

public class PortfolioAnalysisResponse {
    private LocalDate analysisDate;
    private Double totalNetWorth;
    
    // Asset allocation
    private AssetAllocation currentAllocation;
    private AssetAllocation targetAllocation;
    
    // Scores
    private Double diversificationScore;
    private Double riskScore;
    private Boolean needsRebalancing;
    private Double deviationFromTarget;
    
    private List<RebalanceRecommendation> rebalancingRecommendations;
    
    // Inner classes
    public static class AssetAllocation {
        private Double equityValue;
        private Double debtValue;
        private Double goldValue;
        private Double cashValue;
        private Double realEstateValue;
        private Double alternativeValue;
        private Double equityPercentage;
        private Double debtPercentage;
        private Double goldPercentage;
        private Double cashPercentage;
        private Double realEstatePercentage;
        private Double alternativePercentage;

        // Getters and Setters
        public Double getEquityValue() { return equityValue; }
        public void setEquityValue(Double equityValue) { this.equityValue = equityValue; }
        public Double getDebtValue() { return debtValue; }
        public void setDebtValue(Double debtValue) { this.debtValue = debtValue; }
        public Double getGoldValue() { return goldValue; }
        public void setGoldValue(Double goldValue) { this.goldValue = goldValue; }
        public Double getCashValue() { return cashValue; }
        public void setCashValue(Double cashValue) { this.cashValue = cashValue; }
        public Double getRealEstateValue() { return realEstateValue; }
        public void setRealEstateValue(Double realEstateValue) { this.realEstateValue = realEstateValue; }
        public Double getAlternativeValue() { return alternativeValue; }
        public void setAlternativeValue(Double alternativeValue) { this.alternativeValue = alternativeValue; }
        public Double getEquityPercentage() { return equityPercentage; }
        public void setEquityPercentage(Double equityPercentage) { this.equityPercentage = equityPercentage; }
        public Double getDebtPercentage() { return debtPercentage; }
        public void setDebtPercentage(Double debtPercentage) { this.debtPercentage = debtPercentage; }
        public Double getGoldPercentage() { return goldPercentage; }
        public void setGoldPercentage(Double goldPercentage) { this.goldPercentage = goldPercentage; }
        public Double getCashPercentage() { return cashPercentage; }
        public void setCashPercentage(Double cashPercentage) { this.cashPercentage = cashPercentage; }
        public Double getRealEstatePercentage() { return realEstatePercentage; }
        public void setRealEstatePercentage(Double realEstatePercentage) { this.realEstatePercentage = realEstatePercentage; }
        public Double getAlternativePercentage() { return alternativePercentage; }
        public void setAlternativePercentage(Double alternativePercentage) { this.alternativePercentage = alternativePercentage; }
    }
    
    public static class RebalanceRecommendation {
        private AssetClass assetClass;
        private String action; // "INCREASE" or "DECREASE"
        private Double currentPercentage;
        private Double targetPercentage;
        private Double amountToAdjust;

        // Getters and Setters
        public AssetClass getAssetClass() { return assetClass; }
        public void setAssetClass(AssetClass assetClass) { this.assetClass = assetClass; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public Double getCurrentPercentage() { return currentPercentage; }
        public void setCurrentPercentage(Double currentPercentage) { this.currentPercentage = currentPercentage; }
        public Double getTargetPercentage() { return targetPercentage; }
        public void setTargetPercentage(Double targetPercentage) { this.targetPercentage = targetPercentage; }
        public Double getAmountToAdjust() { return amountToAdjust; }
        public void setAmountToAdjust(Double amountToAdjust) { this.amountToAdjust = amountToAdjust; }
    }

    // Getters and Setters
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
    public Double getTotalNetWorth() { return totalNetWorth; }
    public void setTotalNetWorth(Double totalNetWorth) { this.totalNetWorth = totalNetWorth; }
    public AssetAllocation getCurrentAllocation() { return currentAllocation; }
    public void setCurrentAllocation(AssetAllocation currentAllocation) { this.currentAllocation = currentAllocation; }
    public AssetAllocation getTargetAllocation() { return targetAllocation; }
    public void setTargetAllocation(AssetAllocation targetAllocation) { this.targetAllocation = targetAllocation; }
    public Double getDiversificationScore() { return diversificationScore; }
    public void setDiversificationScore(Double diversificationScore) { this.diversificationScore = diversificationScore; }
    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }
    public Boolean getNeedsRebalancing() { return needsRebalancing; }
    public void setNeedsRebalancing(Boolean needsRebalancing) { this.needsRebalancing = needsRebalancing; }
    public Double getDeviationFromTarget() { return deviationFromTarget; }
    public void setDeviationFromTarget(Double deviationFromTarget) { this.deviationFromTarget = deviationFromTarget; }
    public List<RebalanceRecommendation> getRebalancingRecommendations() { return rebalancingRecommendations; }
    public void setRebalancingRecommendations(List<RebalanceRecommendation> rebalancingRecommendations) { this.rebalancingRecommendations = rebalancingRecommendations; }
}
