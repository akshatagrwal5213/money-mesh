package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category; // FOOD, SHOPPING, ENTERTAINMENT, BILLS, etc.
    
    @Column(name = "budget_amount", nullable = false)
    private BigDecimal budgetAmount;
    
    @Column(name = "spent_amount", nullable = false)
    private BigDecimal spentAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod period = BudgetPeriod.MONTHLY;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "alert_threshold")
    private Integer alertThreshold = 80; // Alert at 80% spent
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "alert_sent", nullable = false)
    private Boolean alertSent = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    // Constructors
    public Budget() {}
    
    public Budget(Customer customer, String name, String category, BigDecimal budgetAmount, 
                  BudgetPeriod period, LocalDate startDate, LocalDate endDate) {
        this.customer = customer;
        this.name = name;
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Utility methods
    public BigDecimal getRemainingAmount() {
        return budgetAmount.subtract(spentAmount);
    }
    
    public double getPercentageSpent() {
        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return spentAmount.divide(budgetAmount, 4, java.math.RoundingMode.HALF_UP)
                         .multiply(BigDecimal.valueOf(100))
                         .doubleValue();
    }
    
    public boolean isOverBudget() {
        return spentAmount.compareTo(budgetAmount) > 0;
    }
    
    public boolean shouldSendAlert() {
        return !alertSent && getPercentageSpent() >= alertThreshold;
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
    
    public BigDecimal getSpentAmount() {
        return spentAmount;
    }
    
    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }
    
    public BudgetPeriod getPeriod() {
        return period;
    }
    
    public void setPeriod(BudgetPeriod period) {
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getAlertSent() {
        return alertSent;
    }
    
    public void setAlertSent(Boolean alertSent) {
        this.alertSent = alertSent;
    }
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
