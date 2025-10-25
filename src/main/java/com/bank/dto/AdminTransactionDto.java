package com.bank.dto;

import java.time.LocalDateTime;

public class AdminTransactionDto {
    private Long id;
    private double amount;
    private String type;
    private LocalDateTime date;
    private AccountInfo account;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public AccountInfo getAccount() {
        return account;
    }

    public void setAccount(AccountInfo account) {
        this.account = account;
    }

    public static class AccountInfo {
        private String accountNumber;
        private CustomerInfo customer;

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public CustomerInfo getCustomer() {
            return customer;
        }

        public void setCustomer(CustomerInfo customer) {
            this.customer = customer;
        }
    }

    public static class CustomerInfo {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
