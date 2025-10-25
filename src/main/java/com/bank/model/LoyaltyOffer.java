package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_offers")
public class LoyaltyOffer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    private CustomerTierLevel tierLevel;  // Null means offer for all tiers
    
    @Column(nullable = false, length = 200)
    private String offerTitle;
    
    @Column(nullable = false, length = 1000)
    private String description;
    
    @Column(length = 50)
    private String offerType;  // CASHBACK, DISCOUNT, BONUS_POINTS, FEE_WAIVER
    
    private Double offerValue;  // Percentage or amount
    
    @Column(nullable = false)
    private LocalDate validFrom;
    
    @Column(nullable = false)
    private LocalDate validTill;
    
    @Column(length = 1000)
    private String termsConditions;
    
    @Column(length = 50)
    private String applicableOn;  // TRANSACTIONS, LOANS, DEPOSITS, ALL
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    private LocalDate usedDate;
    
    @Column(length = 100)
    private String usageReference;
    
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public CustomerTierLevel getTierLevel() {
        return tierLevel;
    }

    public void setTierLevel(CustomerTierLevel tierLevel) {
        this.tierLevel = tierLevel;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public Double getOfferValue() {
        return offerValue;
    }

    public void setOfferValue(Double offerValue) {
        this.offerValue = offerValue;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTill() {
        return validTill;
    }

    public void setValidTill(LocalDate validTill) {
        this.validTill = validTill;
    }

    public String getTermsConditions() {
        return termsConditions;
    }

    public void setTermsConditions(String termsConditions) {
        this.termsConditions = termsConditions;
    }

    public String getApplicableOn() {
        return applicableOn;
    }

    public void setApplicableOn(String applicableOn) {
        this.applicableOn = applicableOn;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDate getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(LocalDate usedDate) {
        this.usedDate = usedDate;
    }

    public String getUsageReference() {
        return usageReference;
    }

    public void setUsageReference(String usageReference) {
        this.usageReference = usageReference;
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
