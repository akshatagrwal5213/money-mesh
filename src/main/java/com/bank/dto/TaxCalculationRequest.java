package com.bank.dto;

import com.bank.model.TaxRegime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class TaxCalculationRequest {
    
    @NotNull(message = "Financial year is required")
    private String financialYear;
    
    @NotNull(message = "Gross total income is required")
    @PositiveOrZero(message = "Income must be positive")
    private Double grossTotalIncome;
    
    @NotNull(message = "Tax regime is required")
    private TaxRegime taxRegime;
    
    @PositiveOrZero
    private Double standardDeduction = 50000.0;
    
    @PositiveOrZero
    private Double section80CDeduction = 0.0;
    
    @PositiveOrZero
    private Double section80DDeduction = 0.0;
    
    @PositiveOrZero
    private Double section80EDeduction = 0.0;
    
    @PositiveOrZero
    private Double section80GDeduction = 0.0;
    
    @PositiveOrZero
    private Double hraExemption = 0.0;
    
    @PositiveOrZero
    private Double homeLoanInterest = 0.0;
    
    @PositiveOrZero
    private Double npsDeduction = 0.0;
    
    @PositiveOrZero
    private Double otherDeductions = 0.0;
    
    private Integer age; // For senior citizen benefits
    
    // Getters and Setters
    
    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public Double getGrossTotalIncome() {
        return grossTotalIncome;
    }

    public void setGrossTotalIncome(Double grossTotalIncome) {
        this.grossTotalIncome = grossTotalIncome;
    }

    public TaxRegime getTaxRegime() {
        return taxRegime;
    }

    public void setTaxRegime(TaxRegime taxRegime) {
        this.taxRegime = taxRegime;
    }

    public Double getStandardDeduction() {
        return standardDeduction;
    }

    public void setStandardDeduction(Double standardDeduction) {
        this.standardDeduction = standardDeduction;
    }

    public Double getSection80CDeduction() {
        return section80CDeduction;
    }

    public void setSection80CDeduction(Double section80CDeduction) {
        this.section80CDeduction = section80CDeduction;
    }

    public Double getSection80DDeduction() {
        return section80DDeduction;
    }

    public void setSection80DDeduction(Double section80DDeduction) {
        this.section80DDeduction = section80DDeduction;
    }

    public Double getSection80EDeduction() {
        return section80EDeduction;
    }

    public void setSection80EDeduction(Double section80EDeduction) {
        this.section80EDeduction = section80EDeduction;
    }

    public Double getSection80GDeduction() {
        return section80GDeduction;
    }

    public void setSection80GDeduction(Double section80GDeduction) {
        this.section80GDeduction = section80GDeduction;
    }

    public Double getHraExemption() {
        return hraExemption;
    }

    public void setHraExemption(Double hraExemption) {
        this.hraExemption = hraExemption;
    }

    public Double getHomeLoanInterest() {
        return homeLoanInterest;
    }

    public void setHomeLoanInterest(Double homeLoanInterest) {
        this.homeLoanInterest = homeLoanInterest;
    }

    public Double getNpsDeduction() {
        return npsDeduction;
    }

    public void setNpsDeduction(Double npsDeduction) {
        this.npsDeduction = npsDeduction;
    }

    public Double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(Double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
