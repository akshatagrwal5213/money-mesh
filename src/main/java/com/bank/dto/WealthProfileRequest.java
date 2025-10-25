package com.bank.dto;

import com.bank.model.RebalanceStrategy;
import com.bank.model.RiskProfile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WealthProfileRequest {
    
    @NotNull
    private RiskProfile riskProfile;
    
    @NotNull
    @Min(18)
    @Max(100)
    private Integer age;
    
    @NotNull
    @Min(40)
    @Max(100)
    private Integer retirementAge;
    
    @NotNull
    @Positive
    private Double monthlyIncome;
    
    @NotNull
    @Positive
    private Double monthlyExpenses;
    
    @Positive
    private Double emergencyFundMonths;
    
    private RebalanceStrategy rebalanceStrategy;
    
    // Target allocations (should sum to 100)
    @Min(0) @Max(100)
    private Double targetEquityPercentage;
    
    @Min(0) @Max(100)
    private Double targetDebtPercentage;
    
    @Min(0) @Max(100)
    private Double targetGoldPercentage;
    
    @Min(0) @Max(100)
    private Double targetCashPercentage;
    
    @Min(0) @Max(100)
    private Double targetRealEstatePercentage;
    
    @Min(0) @Max(100)
    private Double targetAlternativePercentage;

    // Getters and Setters
    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(Integer retirementAge) {
        this.retirementAge = retirementAge;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(Double monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public Double getEmergencyFundMonths() {
        return emergencyFundMonths;
    }

    public void setEmergencyFundMonths(Double emergencyFundMonths) {
        this.emergencyFundMonths = emergencyFundMonths;
    }

    public RebalanceStrategy getRebalanceStrategy() {
        return rebalanceStrategy;
    }

    public void setRebalanceStrategy(RebalanceStrategy rebalanceStrategy) {
        this.rebalanceStrategy = rebalanceStrategy;
    }

    public Double getTargetEquityPercentage() {
        return targetEquityPercentage;
    }

    public void setTargetEquityPercentage(Double targetEquityPercentage) {
        this.targetEquityPercentage = targetEquityPercentage;
    }

    public Double getTargetDebtPercentage() {
        return targetDebtPercentage;
    }

    public void setTargetDebtPercentage(Double targetDebtPercentage) {
        this.targetDebtPercentage = targetDebtPercentage;
    }

    public Double getTargetGoldPercentage() {
        return targetGoldPercentage;
    }

    public void setTargetGoldPercentage(Double targetGoldPercentage) {
        this.targetGoldPercentage = targetGoldPercentage;
    }

    public Double getTargetCashPercentage() {
        return targetCashPercentage;
    }

    public void setTargetCashPercentage(Double targetCashPercentage) {
        this.targetCashPercentage = targetCashPercentage;
    }

    public Double getTargetRealEstatePercentage() {
        return targetRealEstatePercentage;
    }

    public void setTargetRealEstatePercentage(Double targetRealEstatePercentage) {
        this.targetRealEstatePercentage = targetRealEstatePercentage;
    }

    public Double getTargetAlternativePercentage() {
        return targetAlternativePercentage;
    }

    public void setTargetAlternativePercentage(Double targetAlternativePercentage) {
        this.targetAlternativePercentage = targetAlternativePercentage;
    }
}
