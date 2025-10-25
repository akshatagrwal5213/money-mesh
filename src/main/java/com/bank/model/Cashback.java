package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cashbacks")
public class Cashback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(length = 100)
    private String transactionReference;
    
    @Column(nullable = false)
    private Double transactionAmount;
    
    @Column(nullable = false)
    private Double cashbackAmount;
    
    @Column(nullable = false)
    private Double cashbackPercentage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardCategory category;
    
    @Column(length = 50)
    private String transactionType;  // UPI, CARD, PREPAYMENT, etc.
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(nullable = false)
    private LocalDate creditDate;
    
    @Column(nullable = false)
    private Boolean isCredited = false;
    
    private Double tierMultiplier;  // Applied multiplier based on tier
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Double getCashbackAmount() {
        return cashbackAmount;
    }

    public void setCashbackAmount(Double cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }

    public Double getCashbackPercentage() {
        return cashbackPercentage;
    }

    public void setCashbackPercentage(Double cashbackPercentage) {
        this.cashbackPercentage = cashbackPercentage;
    }

    public RewardCategory getCategory() {
        return category;
    }

    public void setCategory(RewardCategory category) {
        this.category = category;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDate getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(LocalDate creditDate) {
        this.creditDate = creditDate;
    }

    public Boolean getIsCredited() {
        return isCredited;
    }

    public void setIsCredited(Boolean isCredited) {
        this.isCredited = isCredited;
    }

    public Double getTierMultiplier() {
        return tierMultiplier;
    }

    public void setTierMultiplier(Double tierMultiplier) {
        this.tierMultiplier = tierMultiplier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
