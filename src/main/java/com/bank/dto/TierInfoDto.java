package com.bank.dto;

import com.bank.model.CustomerTierLevel;
import java.time.LocalDate;

public class TierInfoDto {
    
    private Long id;
    private CustomerTierLevel tierLevel;
    private Integer totalPoints;
    private Integer activePoints;
    private LocalDate tierStartDate;
    private LocalDate tierUpgradeDate;
    private Integer nextTierThreshold;
    private String benefits;
    private Double interestRateDiscount;
    private Boolean freeRestructuring;
    private Boolean prepaymentFeeWaiver;
    private Double cashbackMultiplier;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
