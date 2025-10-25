package com.bank.dto;

import jakarta.validation.constraints.NotNull;

public class CardBlockRequest {
    
    @NotNull(message = "Card ID is required")
    private Long cardId;
    
    @NotNull(message = "Reason is required")
    private String reason;
    
    private Boolean reportLost = false;
    private Boolean reportStolen = false;

    // Getters and Setters
    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getReportLost() {
        return reportLost;
    }

    public void setReportLost(Boolean reportLost) {
        this.reportLost = reportLost;
    }

    public Boolean getReportStolen() {
        return reportStolen;
    }

    public void setReportStolen(Boolean reportStolen) {
        this.reportStolen = reportStolen;
    }
}
