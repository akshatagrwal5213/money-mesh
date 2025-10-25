package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_tier_info")
public class CustomerTierInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerTierLevel tierLevel;
    
    @Column(nullable = false)
    private Integer totalPoints = 0;
    
    @Column(nullable = false)
    private Integer activePoints = 0;  // Non-expired, non-redeemed points
    
    @Column(nullable = false)
    private LocalDate tierStartDate;
    
    private LocalDate tierUpgradeDate;
    
    private LocalDate lastReviewDate;
    
    @Column(nullable = false)
    private Integer nextTierThreshold;  // Points needed for next tier
    
    @Column(length = 1000)
    private String benefits;  // JSON or comma-separated list of benefits
    
    private Double interestRateDiscount;  // Percentage discount on loan rates
    
    private Boolean freeRestructuring = false;
    
    private Boolean prepaymentFeeWaiver = false;
    
    private Double cashbackMultiplier = 1.0;  // 1.0x, 1.5x, 2.0x, 3.0x
    
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

    public CustomerTierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(CustomerTierLevel tierLevel) {
        this.tierLevel = tierLevel;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getActivePoints() {
        return activePoints;
    }

    public void setActivePoints(Integer activePoints) {
        this.activePoints = activePoints;
    }

    public LocalDate getTierStartDate() {
        return tierStartDate;
    }

    public void setTierStartDate(LocalDate tierStartDate) {
        this.tierStartDate = tierStartDate;
    }

    public LocalDate getTierUpgradeDate() {
        return tierUpgradeDate;
    }

    public void setTierUpgradeDate(LocalDate tierUpgradeDate) {
        this.tierUpgradeDate = tierUpgradeDate;
    }

    public LocalDate getLastReviewDate() {
        return lastReviewDate;
    }

    public void setLastReviewDate(LocalDate lastReviewDate) {
        this.lastReviewDate = lastReviewDate;
    }

    public Integer getNextTierThreshold() {
        return nextTierThreshold;
    }

    public void setNextTierThreshold(Integer nextTierThreshold) {
        this.nextTierThreshold = nextTierThreshold;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public Double getInterestRateDiscount() {
        return interestRateDiscount;
    }

    public void setInterestRateDiscount(Double interestRateDiscount) {
        this.interestRateDiscount = interestRateDiscount;
    }

    public Boolean getFreeRestructuring() {
        return freeRestructuring;
    }

    public void setFreeRestructuring(Boolean freeRestructuring) {
        this.freeRestructuring = freeRestructuring;
    }

    public Boolean getPrepaymentFeeWaiver() {
        return prepaymentFeeWaiver;
    }

    public void setPrepaymentFeeWaiver(Boolean prepaymentFeeWaiver) {
        this.prepaymentFeeWaiver = prepaymentFeeWaiver;
    }

    public Double getCashbackMultiplier() {
        return cashbackMultiplier;
    }

    public void setCashbackMultiplier(Double cashbackMultiplier) {
        this.cashbackMultiplier = cashbackMultiplier;
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
