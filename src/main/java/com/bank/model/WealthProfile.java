package com.bank.model;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "wealth_profiles")
public class WealthProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskProfile riskProfile;
    
    private Integer age;
    private Integer retirementAge;
    private Double monthlyIncome;
    private Double monthlyExpenses;
    private Double emergencyFundMonths; // Number of months of expenses in emergency fund
    
    @Enumerated(EnumType.STRING)
    private RebalanceStrategy rebalanceStrategy;
    
    private Double targetEquityPercentage;
    private Double targetDebtPercentage;
    private Double targetGoldPercentage;
    private Double targetCashPercentage;
    private Double targetRealEstatePercentage;
    private Double targetAlternativePercentage;
    
    private LocalDate profileCreatedDate;
    private LocalDate lastUpdatedDate;

    // Constructors
    public WealthProfile() {
        this.profileCreatedDate = LocalDate.now();
        this.lastUpdatedDate = LocalDate.now();
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

    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfile riskProfile) {
        this.riskProfile = riskProfile;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(Integer retirementAge) {
        this.retirementAge = retirementAge;
    }

    public Double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(Double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public Double getMonthlyExpenses() {
        return monthlyExpenses;
    }

    public void setMonthlyExpenses(Double monthlyExpenses) {
        this.monthlyExpenses = monthlyExpenses;
    }

    public Double getEmergencyFundMonths() {
        return emergencyFundMonths;
    }

    public void setEmergencyFundMonths(Double emergencyFundMonths) {
        this.emergencyFundMonths = emergencyFundMonths;
    }

    public RebalanceStrategy getRebalanceStrategy() {
        return rebalanceStrategy;
    }

    public void setRebalanceStrategy(RebalanceStrategy rebalanceStrategy) {
        this.rebalanceStrategy = rebalanceStrategy;
    }

    public Double getTargetEquityPercentage() {
        return targetEquityPercentage;
    }

    public void setTargetEquityPercentage(Double targetEquityPercentage) {
        this.targetEquityPercentage = targetEquityPercentage;
    }

    public Double getTargetDebtPercentage() {
        return targetDebtPercentage;
    }

    public void setTargetDebtPercentage(Double targetDebtPercentage) {
        this.targetDebtPercentage = targetDebtPercentage;
    }

    public Double getTargetGoldPercentage() {
        return targetGoldPercentage;
    }

    public void setTargetGoldPercentage(Double targetGoldPercentage) {
        this.targetGoldPercentage = targetGoldPercentage;
    }

    public Double getTargetCashPercentage() {
        return targetCashPercentage;
    }

    public void setTargetCashPercentage(Double targetCashPercentage) {
        this.targetCashPercentage = targetCashPercentage;
    }

    public Double getTargetRealEstatePercentage() {
        return targetRealEstatePercentage;
    }

    public void setTargetRealEstatePercentage(Double targetRealEstatePercentage) {
        this.targetRealEstatePercentage = targetRealEstatePercentage;
    }

    public Double getTargetAlternativePercentage() {
        return targetAlternativePercentage;
    }

    public void setTargetAlternativePercentage(Double targetAlternativePercentage) {
        this.targetAlternativePercentage = targetAlternativePercentage;
    }

    public LocalDate getProfileCreatedDate() {
        return profileCreatedDate;
    }

    public void setProfileCreatedDate(LocalDate profileCreatedDate) {
        this.profileCreatedDate = profileCreatedDate;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
