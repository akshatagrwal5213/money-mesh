package com.bank.dto;

import com.bank.model.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
    private BigDecimal totalInvestmentValue;
    private BigDecimal totalInvested;
    private BigDecimal totalGainLoss;
    private Double overallReturnPercentage;
    private BigDecimal fdValue;
    private BigDecimal rdValue;
    private BigDecimal mutualFundValue;
    private BigDecimal sipValue;
    private Integer totalFixedDeposits;
    private Integer totalRecurringDeposits;
    private Integer totalMutualFunds;
    private Integer totalActiveSips;
    private RiskLevel riskProfile;
}
