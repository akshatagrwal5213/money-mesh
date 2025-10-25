package com.bank.dto;

import java.util.List;

public class RetirementPlanResponse {
    private Integer currentAge;
    private Integer retirementAge;
    private Double yearsToRetirement;
    private Double projectedCorpus;
    private Double corpusRequired;
    private Boolean onTrack;
    private Double shortfall;
    private Double surplus;
    private Double recommendedMonthlySip;
    private List<String> recommendations;

    // Getters and Setters
    public Integer getCurrentAge() { return currentAge; }
    public void setCurrentAge(Integer currentAge) { this.currentAge = currentAge; }
    public Integer getRetirementAge() { return retirementAge; }
    public void setRetirementAge(Integer retirementAge) { this.retirementAge = retirementAge; }
    public Double getYearsToRetirement() { return yearsToRetirement; }
    public void setYearsToRetirement(Double yearsToRetirement) { this.yearsToRetirement = yearsToRetirement; }
    public Double getProjectedCorpus() { return projectedCorpus; }
    public void setProjectedCorpus(Double projectedCorpus) { this.projectedCorpus = projectedCorpus; }
    public Double getCorpusRequired() { return corpusRequired; }
    public void setCorpusRequired(Double corpusRequired) { this.corpusRequired = corpusRequired; }
    public Boolean getOnTrack() { return onTrack; }
    public void setOnTrack(Boolean onTrack) { this.onTrack = onTrack; }
    public Double getShortfall() { return shortfall; }
    public void setShortfall(Double shortfall) { this.shortfall = shortfall; }
    public Double getSurplus() { return surplus; }
    public void setSurplus(Double surplus) { this.surplus = surplus; }
    public Double getRecommendedMonthlySip() { return recommendedMonthlySip; }
    public void setRecommendedMonthlySip(Double recommendedMonthlySip) { this.recommendedMonthlySip = recommendedMonthlySip; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
