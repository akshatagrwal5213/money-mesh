package com.bank.dto;

import java.math.BigDecimal;

public class GoalContributionRequest {
    
    private Long goalId;
    private BigDecimal amount;
    private Long accountId;
    
    // Constructors
    public GoalContributionRequest() {}
    
    public GoalContributionRequest(Long goalId, BigDecimal amount, Long accountId) {
        this.goalId = goalId;
        this.amount = amount;
        this.accountId = accountId;
    }
    
    // Getters and Setters
    public Long getGoalId() {
        return goalId;
    }
    
    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public Long getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
