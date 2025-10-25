package com.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalRequest {
    
    private String name;
    private String description;
    private String type; // EMERGENCY_FUND, RETIREMENT, etc.
    private BigDecimal targetAmount;
    private LocalDate targetDate;
    private BigDecimal monthlyContribution;
    private Boolean isAutomated;
    private Long linkedAccountId;
    
    // Constructors
    public GoalRequest() {}
    
    public GoalRequest(String name, String type, BigDecimal targetAmount, LocalDate targetDate) {
        this.name = name;
        this.type = type;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public LocalDate getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    
    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }
    
    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }
    
    public Boolean getIsAutomated() {
        return isAutomated;
    }
    
    public void setIsAutomated(Boolean isAutomated) {
        this.isAutomated = isAutomated;
    }
    
    public Long getLinkedAccountId() {
        return linkedAccountId;
    }
    
    public void setLinkedAccountId(Long linkedAccountId) {
        this.linkedAccountId = linkedAccountId;
    }
}
