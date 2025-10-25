package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_bonuses")
public class ReferralBonus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "referrer_id", nullable = false)
    private Customer referrer;
    
    @ManyToOne
    @JoinColumn(name = "referred_id")
    private Customer referred;
    
    @Column(nullable = false, unique = true, length = 50)
    private String referralCode;
    
    @Column(length = 100)
    private String referredEmail;
    
    @Column(length = 20)
    private String referredPhone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReferralStatus status;
    
    @Column(nullable = false)
    private LocalDate referralDate;
    
    private LocalDate registrationDate;
    
    private LocalDate qualificationDate;
    
    private LocalDate bonusCreditDate;
    
    @Column(nullable = false)
    private Integer bonusPoints = 500;  // Default 500 points
    
    @Column(nullable = false)
    private Boolean bonusCredited = false;
    
    @Column(length = 1000)
    private String remarks;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getReferrer() {
        return referrer;
    }

    public void setReferrer(Customer referrer) {
        this.referrer = referrer;
    }

    public Customer getReferred() {
        return referred;
    }

    public void setReferred(Customer referred) {
        this.referred = referred;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
