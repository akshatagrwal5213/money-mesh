package com.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ClaimRequest {
    private Long policyId;
    private BigDecimal claimAmount;
    private LocalDate incidentDate;
    private String description;
    private String hospitalName;
    private String doctorName;
    private String documentsSubmitted;
    
    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public BigDecimal getClaimAmount() {
        return claimAmount;
    }
    
    public void setClaimAmount(BigDecimal claimAmount) {
        this.claimAmount = claimAmount;
    }
    
    public LocalDate getIncidentDate() {
        return incidentDate;
    }
    
    public void setIncidentDate(LocalDate incidentDate) {
        this.incidentDate = incidentDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getDocumentsSubmitted() {
        return documentsSubmitted;
    }
    
    public void setDocumentsSubmitted(String documentsSubmitted) {
        this.documentsSubmitted = documentsSubmitted;
    }
}
