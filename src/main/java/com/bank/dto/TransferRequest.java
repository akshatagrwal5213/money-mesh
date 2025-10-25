package com.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class TransferRequest {
    @NotBlank(message = "fromAccount is required")
    private @NotBlank(message = "fromAccount is required") String fromAccount;
    @NotBlank(message = "toAccount is required")
    private @NotBlank(message = "toAccount is required") String toAccount;
    @Positive(message = "amount must be greater than zero")
    private @Positive(message = "amount must be greater than zero") double amount;

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
