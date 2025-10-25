package com.bank.dto;

import java.util.Map;

import com.bank.model.TaxRegime;

public class TaxCalculationResponse {
    
    private String financialYear;
    private Double grossTotalIncome;
    private TaxRegime taxRegime;
    private Double totalDeductions;
    private Double taxableIncome;
    private Double taxBeforeRebate;
    private Double rebateUnder87A; // Up to 12,500 for income up to 5L (old regime) or 7L (new regime)
    private Double taxAfterRebate;
    private Double healthEducationCess; // 4% on tax
    private Double totalTaxLiability;
    private Double tdsDeducted = 0.0;
    private Double advanceTaxPaid = 0.0;
    private Double taxPayable; // Remaining tax to pay
    private Double taxRefund; // If TDS > tax liability
    
    // Breakdown by slab
    private Map<String, Double> slabWiseTax;
    
    // Deduction breakdown
    private Map<String, Double> deductionBreakdown;
    
    // Comparison with other regime (if applicable)
    private TaxRegimeComparison regimeComparison;
    
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

    public Double getTotalDeductions() {
        return totalDeductions;
    }

    public void setTotalDeductions(Double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }

    public Double getTaxableIncome() {
        return taxableIncome;
    }

    public void setTaxableIncome(Double taxableIncome) {
        this.taxableIncome = taxableIncome;
    }

    public Double getTaxBeforeRebate() {
        return taxBeforeRebate;
    }

    public void setTaxBeforeRebate(Double taxBeforeRebate) {
        this.taxBeforeRebate = taxBeforeRebate;
    }

    public Double getRebateUnder87A() {
        return rebateUnder87A;
    }

    public void setRebateUnder87A(Double rebateUnder87A) {
        this.rebateUnder87A = rebateUnder87A;
    }

    public Double getTaxAfterRebate() {
        return taxAfterRebate;
    }

    public void setTaxAfterRebate(Double taxAfterRebate) {
        this.taxAfterRebate = taxAfterRebate;
    }

    public Double getHealthEducationCess() {
        return healthEducationCess;
    }

    public void setHealthEducationCess(Double healthEducationCess) {
        this.healthEducationCess = healthEducationCess;
    }

    public Double getTotalTaxLiability() {
        return totalTaxLiability;
    }

    public void setTotalTaxLiability(Double totalTaxLiability) {
        this.totalTaxLiability = totalTaxLiability;
    }

    public Double getTdsDeducted() {
        return tdsDeducted;
    }

    public void setTdsDeducted(Double tdsDeducted) {
        this.tdsDeducted = tdsDeducted;
    }

    public Double getAdvanceTaxPaid() {
        return advanceTaxPaid;
    }

    public void setAdvanceTaxPaid(Double advanceTaxPaid) {
        this.advanceTaxPaid = advanceTaxPaid;
    }

    public Double getTaxPayable() {
        return taxPayable;
    }

    public void setTaxPayable(Double taxPayable) {
        this.taxPayable = taxPayable;
    }

    public Double getTaxRefund() {
        return taxRefund;
    }

    public void setTaxRefund(Double taxRefund) {
        this.taxRefund = taxRefund;
    }

    public Map<String, Double> getSlabWiseTax() {
        return slabWiseTax;
    }

    public void setSlabWiseTax(Map<String, Double> slabWiseTax) {
        this.slabWiseTax = slabWiseTax;
    }

    public Map<String, Double> getDeductionBreakdown() {
        return deductionBreakdown;
    }

    public void setDeductionBreakdown(Map<String, Double> deductionBreakdown) {
        this.deductionBreakdown = deductionBreakdown;
    }

    public TaxRegimeComparison getRegimeComparison() {
        return regimeComparison;
    }

    public void setRegimeComparison(TaxRegimeComparison regimeComparison) {
        this.regimeComparison = regimeComparison;
    }
    
    // Inner class for regime comparison
    public static class TaxRegimeComparison {
        private Double oldRegimeTax;
        private Double newRegimeTax;
        private Double difference;
        private String recommendation;
        
        public Double getOldRegimeTax() {
            return oldRegimeTax;
        }
        
        public void setOldRegimeTax(Double oldRegimeTax) {
            this.oldRegimeTax = oldRegimeTax;
        }
        
        public Double getNewRegimeTax() {
            return newRegimeTax;
        }
        
        public void setNewRegimeTax(Double newRegimeTax) {
            this.newRegimeTax = newRegimeTax;
        }
        
        public Double getDifference() {
            return difference;
        }
        
        public void setDifference(Double difference) {
            this.difference = difference;
        }
        
        public String getRecommendation() {
            return recommendation;
        }
        
        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}
