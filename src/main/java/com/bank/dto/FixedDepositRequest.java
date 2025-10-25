package com.bank.dto;

import com.bank.model.MaturityAction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FixedDepositRequest {
    private String accountNumber;
    private BigDecimal principalAmount;
    private Integer tenureMonths;
    private MaturityAction maturityAction;
    private Boolean autoRenew;
}
