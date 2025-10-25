package com.bank.dto;

import jakarta.validation.constraints.NotBlank;

public class PendingTransferConfirmRequest {
    @NotBlank
    private Long pendingId;
    @NotBlank
    private String otp;

    public Long getPendingId() {
        return pendingId;
    }

    public void setPendingId(Long pendingId) {
        this.pendingId = pendingId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
