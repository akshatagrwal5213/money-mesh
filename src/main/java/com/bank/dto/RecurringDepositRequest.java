package com.bank.dto;

import com.bank.model.MaturityAction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecurringDepositRequest {
    private String accountNumber;
    private BigDecimal monthlyInstallment;
    private Integer tenureMonths;
    private MaturityAction maturityAction;
    private Boolean autoDebit;
}
