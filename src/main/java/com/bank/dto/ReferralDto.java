package com.bank.dto;

import com.bank.model.ReferralStatus;
import java.time.LocalDate;

public class ReferralDto {
    
    private Long id;
    private String referralCode;
    private String referredEmail;
    private String referredPhone;
    private ReferralStatus status;
    private LocalDate referralDate;
    private LocalDate registrationDate;
    private LocalDate qualificationDate;
    private LocalDate bonusCreditDate;
    private Integer bonusPoints;
    private Boolean bonusCredited;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

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

    public ReferralStatus getStatus() {
        return status;
    }

    public void setStatus(ReferralStatus status) {
        this.status = status;
    }

    public LocalDate getReferralDate() {
        return referralDate;
    }

    public void setReferralDate(LocalDate referralDate) {
        this.referralDate = referralDate;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public LocalDate getQualificationDate() {
        return qualificationDate;
    }

    public void setQualificationDate(LocalDate qualificationDate) {
        this.qualificationDate = qualificationDate;
    }

    public LocalDate getBonusCreditDate() {
        return bonusCreditDate;
    }

    public void setBonusCreditDate(LocalDate bonusCreditDate) {
        this.bonusCreditDate = bonusCreditDate;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Boolean getBonusCredited() {
        return bonusCredited;
    }

    public void setBonusCredited(Boolean bonusCredited) {
        this.bonusCredited = bonusCredited;
    }
}
