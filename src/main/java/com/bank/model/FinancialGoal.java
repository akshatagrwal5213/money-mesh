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
@Table(name = "financial_goals")
public class FinancialGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalType type;
    
    @Column(name = "target_amount", nullable = false)
    private BigDecimal targetAmount;
    
    @Column(name = "current_amount", nullable = false)
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;
    
    @Column(name = "monthly_contribution")
    private BigDecimal monthlyContribution;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalStatus status = GoalStatus.IN_PROGRESS;
    
    @Column(name = "is_automated", nullable = false)
    private Boolean isAutomated = false;
    
    @ManyToOne
    @JoinColumn(name = "linked_account_id")
    private Account linkedAccount; // For automated contributions
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @Column(name = "completed_at")
    private LocalDate completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
    // Utility methods
    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }
    
    public double getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        return currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                           .multiply(BigDecimal.valueOf(100))
                           .doubleValue();
    }
    
    public boolean isCompleted() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }
    
    public long getDaysRemaining() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
    
    // Constructors
    public FinancialGoal() {}
    
    public FinancialGoal(Customer customer, String name, GoalType type, 
                        BigDecimal targetAmount, LocalDate targetDate) {
        this.customer = customer;
        this.name = name;
        this.type = type;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public GoalType getType() {
        return type;
    }
    
    public void setType(GoalType type) {
        this.type = type;
    }
    
    public BigDecimal getTargetAmount() {
        return targetAmount;
    }
    
    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }
    
    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }
    
    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
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
    
    public GoalStatus getStatus() {
        return status;
    }
    
    public void setStatus(GoalStatus status) {
        this.status = status;
        if (status == GoalStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDate.now();
        }
    }
    
    public Boolean getIsAutomated() {
        return isAutomated;
    }
    
    public void setIsAutomated(Boolean isAutomated) {
        this.isAutomated = isAutomated;
    }
    
    public Account getLinkedAccount() {
        return linkedAccount;
    }
    
    public void setLinkedAccount(Account linkedAccount) {
        this.linkedAccount = linkedAccount;
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
    
    public LocalDate getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }
}
