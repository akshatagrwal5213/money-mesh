package com.bank.dto;

import java.math.BigDecimal;

import com.bank.model.InsuranceType;
import com.bank.model.PremiumFrequency;

public class PolicyApplicationRequest {
    private InsuranceType insuranceType;
    private String policyName;
    private BigDecimal coverageAmount;
    private Integer termYears;
    private PremiumFrequency premiumFrequency;
    private String nominee;
    private String nomineeRelation;
    private BigDecimal nomineePercentage;
    private String remarks;
    
    // Getters and Setters
    public InsuranceType getInsuranceType() {
        return insuranceType;
    }
    
    public void setInsuranceType(InsuranceType insuranceType) {
        this.insuranceType = insuranceType;
    }
    
    public String getPolicyName() {
        return policyName;
    }
    
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }
    
    public BigDecimal getCoverageAmount() {
        return coverageAmount;
    }
    
    public void setCoverageAmount(BigDecimal coverageAmount) {
        this.coverageAmount = coverageAmount;
    }
    
    public Integer getTermYears() {
        return termYears;
    }
    
    public void setTermYears(Integer termYears) {
        this.termYears = termYears;
    }
    
    public PremiumFrequency getPremiumFrequency() {
        return premiumFrequency;
    }
    
    public void setPremiumFrequency(PremiumFrequency premiumFrequency) {
        this.premiumFrequency = premiumFrequency;
    }
    
    public String getNominee() {
        return nominee;
    }
    
    public void setNominee(String nominee) {
        this.nominee = nominee;
    }
    
    public String getNomineeRelation() {
        return nomineeRelation;
    }
    
    public void setNomineeRelation(String nomineeRelation) {
        this.nomineeRelation = nomineeRelation;
    }
    
    public BigDecimal getNomineePercentage() {
        return nomineePercentage;
    }
    
    public void setNomineePercentage(BigDecimal nomineePercentage) {
        this.nomineePercentage = nomineePercentage;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
