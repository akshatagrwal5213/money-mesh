package com.bank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MutualFundInvestmentRequest {
    private String fundCode;
    private String accountNumber;
    private BigDecimal amount;
    private String folioNumber; // Optional - for existing folio
}
