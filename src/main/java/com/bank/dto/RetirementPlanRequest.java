package com.bank.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class RetirementPlanRequest {
    @NotNull @Min(18) @Max(100)
    private Integer currentAge;
    
    @NotNull @Min(40) @Max(100)
    private Integer retirementAge;
    
    @NotNull @PositiveOrZero
    private Double currentSavings;
    
    @NotNull @Positive
    private Double monthlyInvestment;
    
    @NotNull @Min(1) @Max(30)
    private Double expectedReturn; // Annual %
    
    @NotNull @Min(0) @Max(15)
    private Double inflationRate; // Annual %
    
    @NotNull @Positive
    private Double desiredMonthlyIncome; // In today's value
    
    @NotNull @Min(60) @Max(120)
    private Integer lifeExpectancy;

    // Getters and Setters
    public Integer getCurrentAge() { return currentAge; }
    public void setCurrentAge(Integer currentAge) { this.currentAge = currentAge; }
    public Integer getRetirementAge() { return retirementAge; }
    public void setRetirementAge(Integer retirementAge) { this.retirementAge = retirementAge; }
    public Double getCurrentSavings() { return currentSavings; }
    public void setCurrentSavings(Double currentSavings) { this.currentSavings = currentSavings; }
    public Double getMonthlyInvestment() { return monthlyInvestment; }
    public void setMonthlyInvestment(Double monthlyInvestment) { this.monthlyInvestment = monthlyInvestment; }
    public Double getExpectedReturn() { return expectedReturn; }
    public void setExpectedReturn(Double expectedReturn) { this.expectedReturn = expectedReturn; }
    public Double getInflationRate() { return inflationRate; }
    public void setInflationRate(Double inflationRate) { this.inflationRate = inflationRate; }
    public Double getDesiredMonthlyIncome() { return desiredMonthlyIncome; }
    public void setDesiredMonthlyIncome(Double desiredMonthlyIncome) { this.desiredMonthlyIncome = desiredMonthlyIncome; }
    public Integer getLifeExpectancy() { return lifeExpectancy; }
    public void setLifeExpectancy(Integer lifeExpectancy) { this.lifeExpectancy = lifeExpectancy; }
}
