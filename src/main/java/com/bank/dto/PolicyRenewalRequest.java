package com.bank.dto;

public class PolicyRenewalRequest {
    private Long policyId;
    private String remarks;
    
    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
