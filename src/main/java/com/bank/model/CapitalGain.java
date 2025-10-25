package com.bank.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "capital_gains")
public class CapitalGain {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(nullable = false)
    private String financialYear;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;
    
    @Column(length = 200, nullable = false)
    private String assetName;
    
    @Column(nullable = false)
    private LocalDate purchaseDate;
    
    @Column(nullable = false)
    private LocalDate sellDate;
    
    @Column(nullable = false)
    private Double purchasePrice;
    
    @Column(nullable = false)
    private Double sellPrice;
    
    @Column
    private Double indexedCost; // For LTCG calculation
    
    @Column(nullable = false)
    private Double gainAmount; // Calculated gain
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CapitalGainType gainType; // SHORT_TERM or LONG_TERM
    
    @Column
    private Double taxableGain; // After exemptions
    
    @Column
    private Double taxAmount;
    
    @Column(length = 500)
    private String remarks;
    
    @Column(nullable = false)
    private LocalDateTime recordedDate;
    
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

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getSellDate() {
        return sellDate;
    }

    public void setSellDate(LocalDate sellDate) {
        this.sellDate = sellDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getIndexedCost() {
        return indexedCost;
    }

    public void setIndexedCost(Double indexedCost) {
        this.indexedCost = indexedCost;
    }

    public Double getGainAmount() {
        return gainAmount;
    }

    public void setGainAmount(Double gainAmount) {
        this.gainAmount = gainAmount;
    }

    public CapitalGainType getGainType() {
        return gainType;
    }

    public void setGainType(CapitalGainType gainType) {
        this.gainType = gainType;
    }

    public Double getTaxableGain() {
        return taxableGain;
    }

    public void setTaxableGain(Double taxableGain) {
        this.taxableGain = taxableGain;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(LocalDateTime recordedDate) {
        this.recordedDate = recordedDate;
    }
}
