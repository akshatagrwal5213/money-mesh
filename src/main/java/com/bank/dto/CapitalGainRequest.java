package com.bank.dto;

import java.time.LocalDate;

import com.bank.model.AssetType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CapitalGainRequest {
    
    @NotNull(message = "Asset type is required")
    private AssetType assetType;
    
    @NotNull(message = "Asset name is required")
    private String assetName;
    
    @NotNull(message = "Purchase date is required")
    private LocalDate purchaseDate;
    
    @NotNull(message = "Sell date is required")
    private LocalDate sellDate;
    
    @NotNull(message = "Purchase price is required")
    @Positive(message = "Purchase price must be positive")
    private Double purchasePrice;
    
    @NotNull(message = "Sell price is required")
    @Positive(message = "Sell price must be positive")
    private Double sellPrice;
    
    @NotNull(message = "Financial year is required")
    private String financialYear;
    
    private String remarks;
    
    // Getters and Setters
    
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

    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
