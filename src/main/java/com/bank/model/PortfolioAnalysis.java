package com.bank.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio_analyses")
public class PortfolioAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    private LocalDate analysisDate;
    
    // Current allocation
    private Double totalNetWorth;
    private Double equityValue;
    private Double debtValue;
    private Double goldValue;
    private Double cashValue;
    private Double realEstateValue;
    private Double alternativeValue;
    
    // Percentages
    private Double equityPercentage;
    private Double debtPercentage;
    private Double goldPercentage;
    private Double cashPercentage;
    private Double realEstatePercentage;
    private Double alternativePercentage;
    
    // Analysis metrics
    private Double diversificationScore; // 0-100
    private Double riskScore; // 0-100
    private Boolean needsRebalancing;
    private Double deviationFromTarget; // Percentage deviation
    
    @Column(length = 1000)
    private String rebalancingRecommendations;

    // Constructors
    public PortfolioAnalysis() {
        this.analysisDate = LocalDate.now();
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

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public Double getTotalNetWorth() {
        return totalNetWorth;
    }

    public void setTotalNetWorth(Double totalNetWorth) {
        this.totalNetWorth = totalNetWorth;
    }

    public Double getEquityValue() {
        return equityValue;
    }

    public void setEquityValue(Double equityValue) {
        this.equityValue = equityValue;
    }

    public Double getDebtValue() {
        return debtValue;
    }

    public void setDebtValue(Double debtValue) {
        this.debtValue = debtValue;
    }

    public Double getGoldValue() {
        return goldValue;
    }

    public void setGoldValue(Double goldValue) {
        this.goldValue = goldValue;
    }

    public Double getCashValue() {
        return cashValue;
    }

    public void setCashValue(Double cashValue) {
        this.cashValue = cashValue;
    }

    public Double getRealEstateValue() {
        return realEstateValue;
    }

    public void setRealEstateValue(Double realEstateValue) {
        this.realEstateValue = realEstateValue;
    }

    public Double getAlternativeValue() {
        return alternativeValue;
    }

    public void setAlternativeValue(Double alternativeValue) {
        this.alternativeValue = alternativeValue;
    }

    public Double getEquityPercentage() {
        return equityPercentage;
    }

    public void setEquityPercentage(Double equityPercentage) {
        this.equityPercentage = equityPercentage;
    }

    public Double getDebtPercentage() {
        return debtPercentage;
    }

    public void setDebtPercentage(Double debtPercentage) {
        this.debtPercentage = debtPercentage;
    }

    public Double getGoldPercentage() {
        return goldPercentage;
    }

    public void setGoldPercentage(Double goldPercentage) {
        this.goldPercentage = goldPercentage;
    }

    public Double getCashPercentage() {
        return cashPercentage;
    }

    public void setCashPercentage(Double cashPercentage) {
        this.cashPercentage = cashPercentage;
    }

    public Double getRealEstatePercentage() {
        return realEstatePercentage;
    }

    public void setRealEstatePercentage(Double realEstatePercentage) {
        this.realEstatePercentage = realEstatePercentage;
    }

    public Double getAlternativePercentage() {
        return alternativePercentage;
    }

    public void setAlternativePercentage(Double alternativePercentage) {
        this.alternativePercentage = alternativePercentage;
    }

    public Double getDiversificationScore() {
        return diversificationScore;
    }

    public void setDiversificationScore(Double diversificationScore) {
        this.diversificationScore = diversificationScore;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }

    public Boolean getNeedsRebalancing() {
        return needsRebalancing;
    }

    public void setNeedsRebalancing(Boolean needsRebalancing) {
        this.needsRebalancing = needsRebalancing;
    }

    public Double getDeviationFromTarget() {
        return deviationFromTarget;
    }

    public void setDeviationFromTarget(Double deviationFromTarget) {
        this.deviationFromTarget = deviationFromTarget;
    }

    public String getRebalancingRecommendations() {
        return rebalancingRecommendations;
    }

    public void setRebalancingRecommendations(String rebalancingRecommendations) {
        this.rebalancingRecommendations = rebalancingRecommendations;
    }
}
