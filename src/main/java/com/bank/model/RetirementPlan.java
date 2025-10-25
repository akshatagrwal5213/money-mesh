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
@Table(name = "retirement_plans")
public class RetirementPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    private Integer currentAge;
    private Integer retirementAge;
    private Double currentSavings;
    private Double monthlyInvestment;
    private Double expectedReturn; // Annual return percentage
    private Double inflationRate; // Annual inflation percentage
    
    private Double desiredMonthlyIncome; // In today's value
    private Integer lifeExpectancy;
    
    // Calculated values
    private Double projectedCorpus;
    private Double corpusRequired;
    private Boolean onTrack; // Is the plan on track?
    private Double shortfall; // Deficit if not on track
    private Double surplus; // Surplus if exceeding target
    
    private Double recommendedMonthlySip;
    private Double yearsToRetirement;
    
    private LocalDate planCreatedDate;
    private LocalDate lastUpdatedDate;
    
    @Column(length = 1000)
    private String recommendations;

    // Constructors
    public RetirementPlan() {
        this.planCreatedDate = LocalDate.now();
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

    public Integer getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(Integer currentAge) {
        this.currentAge = currentAge;
    }

    public Integer getRetirementAge() {
        return retirementAge;
    }

    public void setRetirementAge(Integer retirementAge) {
        this.retirementAge = retirementAge;
    }

    public Double getCurrentSavings() {
        return currentSavings;
    }

    public void setCurrentSavings(Double currentSavings) {
        this.currentSavings = currentSavings;
    }

    public Double getMonthlyInvestment() {
        return monthlyInvestment;
    }

    public void setMonthlyInvestment(Double monthlyInvestment) {
        this.monthlyInvestment = monthlyInvestment;
    }

    public Double getExpectedReturn() {
        return expectedReturn;
    }

    public void setExpectedReturn(Double expectedReturn) {
        this.expectedReturn = expectedReturn;
    }

    public Double getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(Double inflationRate) {
        this.inflationRate = inflationRate;
    }

    public Double getDesiredMonthlyIncome() {
        return desiredMonthlyIncome;
    }

    public void setDesiredMonthlyIncome(Double desiredMonthlyIncome) {
        this.desiredMonthlyIncome = desiredMonthlyIncome;
    }

    public Integer getLifeExpectancy() {
        return lifeExpectancy;
    }

    public void setLifeExpectancy(Integer lifeExpectancy) {
        this.lifeExpectancy = lifeExpectancy;
    }

    public Double getProjectedCorpus() {
        return projectedCorpus;
    }

    public void setProjectedCorpus(Double projectedCorpus) {
        this.projectedCorpus = projectedCorpus;
    }

    public Double getCorpusRequired() {
        return corpusRequired;
    }

    public void setCorpusRequired(Double corpusRequired) {
        this.corpusRequired = corpusRequired;
    }

    public Boolean getOnTrack() {
        return onTrack;
    }

    public void setOnTrack(Boolean onTrack) {
        this.onTrack = onTrack;
    }

    public Double getShortfall() {
        return shortfall;
    }

    public void setShortfall(Double shortfall) {
        this.shortfall = shortfall;
    }

    public Double getSurplus() {
        return surplus;
    }

    public void setSurplus(Double surplus) {
        this.surplus = surplus;
    }

    public Double getRecommendedMonthlySip() {
        return recommendedMonthlySip;
    }

    public void setRecommendedMonthlySip(Double recommendedMonthlySip) {
        this.recommendedMonthlySip = recommendedMonthlySip;
    }

    public Double getYearsToRetirement() {
        return yearsToRetirement;
    }

    public void setYearsToRetirement(Double yearsToRetirement) {
        this.yearsToRetirement = yearsToRetirement;
    }

    public LocalDate getPlanCreatedDate() {
        return planCreatedDate;
    }

    public void setPlanCreatedDate(LocalDate planCreatedDate) {
        this.planCreatedDate = planCreatedDate;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
}
