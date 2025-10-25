package com.bank.dto;

import com.bank.model.InvestmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponse {
    private Long id;
    private String investmentNumber;
    private String investmentType;
    private BigDecimal amount;
    private InvestmentStatus status;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private BigDecimal currentValue;
    private BigDecimal gainLoss;
    private Double returnPercentage;
    private LocalDateTime createdAt;
}
