package com.bank.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;
    private BigDecimal totalTransfers;
    private BigDecimal averageTransactionAmount;
    private Integer transactionCount;
    private String topCategory;
    private BigDecimal topCategoryAmount;
    private Map<String, BigDecimal> categoryBreakdown;
    private List<DailyTransaction> dailyTransactions;
    
    // Inner class for daily transactions
    public static class DailyTransaction {
        private String date;
        private BigDecimal amount;
        private Integer count;
        
        public DailyTransaction() {}
        
        public DailyTransaction(String date, BigDecimal amount, Integer count) {
            this.date = date;
            this.amount = amount;
            this.count = count;
        }
        
        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
    
    // Constructors
    public AnalyticsResponse() {}
    
    // Getters and Setters
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
    
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    
    public BigDecimal getNetSavings() {
        return netSavings;
    }
    
    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }
    
    public BigDecimal getTotalTransfers() {
        return totalTransfers;
    }
    
    public void setTotalTransfers(BigDecimal totalTransfers) {
        this.totalTransfers = totalTransfers;
    }
    
    public BigDecimal getAverageTransactionAmount() {
        return averageTransactionAmount;
    }
    
    public void setAverageTransactionAmount(BigDecimal averageTransactionAmount) {
        this.averageTransactionAmount = averageTransactionAmount;
    }
    
    public Integer getTransactionCount() {
        return transactionCount;
    }
    
    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }
    
    public String getTopCategory() {
        return topCategory;
    }
    
    public void setTopCategory(String topCategory) {
        this.topCategory = topCategory;
    }
    
    public BigDecimal getTopCategoryAmount() {
        return topCategoryAmount;
    }
    
    public void setTopCategoryAmount(BigDecimal topCategoryAmount) {
        this.topCategoryAmount = topCategoryAmount;
    }
    
    public Map<String, BigDecimal> getCategoryBreakdown() {
        return categoryBreakdown;
    }
    
    public void setCategoryBreakdown(Map<String, BigDecimal> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
    
    public List<DailyTransaction> getDailyTransactions() {
        return dailyTransactions;
    }
    
    public void setDailyTransactions(List<DailyTransaction> dailyTransactions) {
        this.dailyTransactions = dailyTransactions;
    }
}
