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
@Table(name = "financial_health_scores")
public class FinancialHealthScore {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    private LocalDate scoreDate;
    
    // Overall score (0-100)
    private Double overallScore;
    
    @Enumerated(EnumType.STRING)
    private HealthScoreCategory category;
    
    // Component scores (0-100 each)
    private Double savingsScore;           // Savings rate
    private Double debtScore;              // Debt-to-income ratio
    private Double emergencyFundScore;     // Emergency fund adequacy
    private Double investmentScore;        // Investment diversity
    private Double insuranceScore;         // Insurance coverage
    private Double retirementScore;        // Retirement readiness
    
    // Metrics
    private Double savingsRate;            // Percentage of income saved
    private Double debtToIncomeRatio;      // Total debt / Annual income
    private Double emergencyFundMonths;    // Months of expenses covered
    private Double investmentDiversity;    // Portfolio diversification (0-100)
    private Double insuranceCoverage;      // Life insurance coverage adequacy
    private Double retirementReadiness;    // Retirement corpus adequacy (%)
    
    @Column(length = 2000)
    private String improvementRecommendations;
    
    private LocalDate previousScoreDate;
    private Double previousOverallScore;
    private Double scoreImprovement; // Change from previous score

    // Constructors
    public FinancialHealthScore() {
        this.scoreDate = LocalDate.now();
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

    public LocalDate getScoreDate() {
        return scoreDate;
    }

    public void setScoreDate(LocalDate scoreDate) {
        this.scoreDate = scoreDate;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public HealthScoreCategory getCategory() {
        return category;
    }

    public void setCategory(HealthScoreCategory category) {
        this.category = category;
    }

    public Double getSavingsScore() {
        return savingsScore;
    }

    public void setSavingsScore(Double savingsScore) {
        this.savingsScore = savingsScore;
    }

    public Double getDebtScore() {
        return debtScore;
    }

    public void setDebtScore(Double debtScore) {
        this.debtScore = debtScore;
    }

    public Double getEmergencyFundScore() {
        return emergencyFundScore;
    }

    public void setEmergencyFundScore(Double emergencyFundScore) {
        this.emergencyFundScore = emergencyFundScore;
    }

    public Double getInvestmentScore() {
        return investmentScore;
    }

    public void setInvestmentScore(Double investmentScore) {
        this.investmentScore = investmentScore;
    }

    public Double getInsuranceScore() {
        return insuranceScore;
    }

    public void setInsuranceScore(Double insuranceScore) {
        this.insuranceScore = insuranceScore;
    }

    public Double getRetirementScore() {
        return retirementScore;
    }

    public void setRetirementScore(Double retirementScore) {
        this.retirementScore = retirementScore;
    }

    public Double getSavingsRate() {
        return savingsRate;
    }

    public void setSavingsRate(Double savingsRate) {
        this.savingsRate = savingsRate;
    }

    public Double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(Double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public Double getEmergencyFundMonths() {
        return emergencyFundMonths;
    }

    public void setEmergencyFundMonths(Double emergencyFundMonths) {
        this.emergencyFundMonths = emergencyFundMonths;
    }

    public Double getInvestmentDiversity() {
        return investmentDiversity;
    }

    public void setInvestmentDiversity(Double investmentDiversity) {
        this.investmentDiversity = investmentDiversity;
    }

    public Double getInsuranceCoverage() {
        return insuranceCoverage;
    }

    public void setInsuranceCoverage(Double insuranceCoverage) {
        this.insuranceCoverage = insuranceCoverage;
    }

    public Double getRetirementReadiness() {
        return retirementReadiness;
    }

    public void setRetirementReadiness(Double retirementReadiness) {
        this.retirementReadiness = retirementReadiness;
    }

    public String getImprovementRecommendations() {
        return improvementRecommendations;
    }

    public void setImprovementRecommendations(String improvementRecommendations) {
        this.improvementRecommendations = improvementRecommendations;
    }

    public LocalDate getPreviousScoreDate() {
        return previousScoreDate;
    }

    public void setPreviousScoreDate(LocalDate previousScoreDate) {
        this.previousScoreDate = previousScoreDate;
    }

    public Double getPreviousOverallScore() {
        return previousOverallScore;
    }

    public void setPreviousOverallScore(Double previousOverallScore) {
        this.previousOverallScore = previousOverallScore;
    }

    public Double getScoreImprovement() {
        return scoreImprovement;
    }

    public void setScoreImprovement(Double scoreImprovement) {
        this.scoreImprovement = scoreImprovement;
    }
}
