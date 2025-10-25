package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {
    @NotBlank(message = "type is required")
    private @NotBlank(message = "type is required") String type;
    @Positive(message = "amount must be greater than zero")
    private @Positive(message = "amount must be greater than zero") double amount;
    @NotNull(message = "accountId is required")
    private @NotNull(message = "accountId is required") Long accountId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
