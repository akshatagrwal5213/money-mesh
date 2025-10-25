package com.bank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ReferralRequest {
    
    @NotBlank(message = "Email or phone is required")
    @Email(message = "Invalid email format")
    private String referredEmail;
    
    private String referredPhone;
    
    // Getters and Setters
    public String getReferredEmail() {
        return referredEmail;
    }

    public void setReferredEmail(String referredEmail) {
        this.referredEmail = referredEmail;
    }

    public String getReferredPhone() {
        return referredPhone;
    }

    public void setReferredPhone(String referredPhone) {
        this.referredPhone = referredPhone;
    }
}
