package com.bank.dto;

import com.bank.model.EligibilityStatus;
import com.bank.model.LoanType;
import java.time.LocalDate;
import java.util.List;

public class LoanEligibilityResponse {
    
    private LoanType loanType;
    private EligibilityStatus eligibilityStatus;
    private Boolean eligible;
    
    private Double requestedAmount;
    private Double maxEligibleAmount;
    
    private Double interestRate;
    private Integer maxTenureMonths;
    private Integer recommendedTenureMonths;
    
    private Double monthlyEmiEstimate;
    private Double totalInterestPayable;
    private Double totalAmountPayable;
    
    private Integer creditScoreAtCheck;
    private Double debtToIncomeRatio;
    
    private List<String> eligibilityReasons;
    private List<String> conditions;
    private List<String> suggestions;
    
    private LocalDate checkDate;
    private LocalDate expiryDate;

    // Constructors
    public LoanEligibilityResponse() {}

    public LoanEligibilityResponse(LoanType loanType, EligibilityStatus eligibilityStatus, Boolean eligible) {
        this.loanType = loanType;
        this.eligibilityStatus = eligibilityStatus;
        this.eligible = eligible;
    }

    // Getters and Setters
    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public Boolean getEligible() {
        return eligible;
    }

    public void setEligible(Boolean eligible) {
        this.eligible = eligible;
    }

    public Double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(Double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public Double getMaxEligibleAmount() {
        return maxEligibleAmount;
    }

    public void setMaxEligibleAmount(Double maxEligibleAmount) {
        this.maxEligibleAmount = maxEligibleAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getMaxTenureMonths() {
        return maxTenureMonths;
    }

    public void setMaxTenureMonths(Integer maxTenureMonths) {
        this.maxTenureMonths = maxTenureMonths;
    }

    public Integer getRecommendedTenureMonths() {
        return recommendedTenureMonths;
    }

    public void setRecommendedTenureMonths(Integer recommendedTenureMonths) {
        this.recommendedTenureMonths = recommendedTenureMonths;
    }

    public Double getMonthlyEmiEstimate() {
        return monthlyEmiEstimate;
    }

    public void setMonthlyEmiEstimate(Double monthlyEmiEstimate) {
        this.monthlyEmiEstimate = monthlyEmiEstimate;
    }

    public Double getTotalInterestPayable() {
        return totalInterestPayable;
    }

    public void setTotalInterestPayable(Double totalInterestPayable) {
        this.totalInterestPayable = totalInterestPayable;
    }

    public Double getTotalAmountPayable() {
        return totalAmountPayable;
    }

    public void setTotalAmountPayable(Double totalAmountPayable) {
        this.totalAmountPayable = totalAmountPayable;
    }

    public Integer getCreditScoreAtCheck() {
        return creditScoreAtCheck;
    }

    public void setCreditScoreAtCheck(Integer creditScoreAtCheck) {
        this.creditScoreAtCheck = creditScoreAtCheck;
    }

    public Double getDebtToIncomeRatio() {
        return debtToIncomeRatio;
    }

    public void setDebtToIncomeRatio(Double debtToIncomeRatio) {
        this.debtToIncomeRatio = debtToIncomeRatio;
    }

    public List<String> getEligibilityReasons() {
        return eligibilityReasons;
    }

    public void setEligibilityReasons(List<String> eligibilityReasons) {
        this.eligibilityReasons = eligibilityReasons;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDate checkDate) {
        this.checkDate = checkDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}
