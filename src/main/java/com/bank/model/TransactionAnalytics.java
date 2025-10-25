package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_analytics")
public class TransactionAnalytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    
    @Column(name = "total_income", nullable = false)
    private BigDecimal totalIncome = BigDecimal.ZERO;
    
    @Column(name = "total_expenses", nullable = false)
    private BigDecimal totalExpenses = BigDecimal.ZERO;
    
    @Column(name = "total_transfers", nullable = false)
    private BigDecimal totalTransfers = BigDecimal.ZERO;
    
    @Column(name = "average_transaction_amount", nullable = false)
    private BigDecimal averageTransactionAmount = BigDecimal.ZERO;
    
    @Column(name = "transaction_count", nullable = false)
    private Integer transactionCount = 0;
    
    @Column(name = "top_category")
    private String topCategory;
    
    @Column(name = "top_category_amount")
    private BigDecimal topCategoryAmount = BigDecimal.ZERO;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }
    
    // Constructors
    public TransactionAnalytics() {}
    
    public TransactionAnalytics(Customer customer, LocalDate periodStart, LocalDate periodEnd) {
        this.customer = customer;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
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
    
    public LocalDate getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }
    
    public LocalDate getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }
    
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
    
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
