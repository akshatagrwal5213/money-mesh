package com.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetRequest {
    
    private String name;
    private String category;
    private BigDecimal budgetAmount;
    private String period; // WEEKLY, MONTHLY, QUARTERLY, YEARLY, CUSTOM
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer alertThreshold;
    
    // Constructors
    public BudgetRequest() {}
    
    public BudgetRequest(String name, String category, BigDecimal budgetAmount, String period) {
        this.name = name;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.period = period;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }
    
    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Integer getAlertThreshold() {
        return alertThreshold;
    }
    
    public void setAlertThreshold(Integer alertThreshold) {
        this.alertThreshold = alertThreshold;
    }
}
