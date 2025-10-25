package com.bank.dto;

import com.bank.model.RedemptionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RedemptionRequest {
    
    @NotNull(message = "Redemption type is required")
    private RedemptionType redemptionType;
    
    @NotNull(message = "Points to redeem is required")
    @Positive(message = "Points must be positive")
    private Integer pointsToRedeem;
    
    private String beneficiaryDetails;  // Account number, voucher preference, etc.
    
    // Getters and Setters
    public RedemptionType getRedemptionType() {
        return redemptionType;
    }

    public void setRedemptionType(RedemptionType redemptionType) {
        this.redemptionType = redemptionType;
    }

    public Integer getPointsToRedeem() {
        return pointsToRedeem;
    }

    public void setPointsToRedeem(Integer pointsToRedeem) {
        this.pointsToRedeem = pointsToRedeem;
    }

    public String getBeneficiaryDetails() {
        return beneficiaryDetails;
    }

    public void setBeneficiaryDetails(String beneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails;
    }
}
