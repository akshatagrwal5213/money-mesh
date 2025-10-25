package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "investment_portfolio")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentPortfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalInvestmentValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalInvested;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalGainLoss;

    @Column
    private Double overallReturnPercentage;

    @Column(precision = 15, scale = 2)
    private BigDecimal fdValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal rdValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal mutualFundValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal sipValue;

    @Column
    private Integer totalFixedDeposits = 0;

    @Column
    private Integer totalRecurringDeposits = 0;

    @Column
    private Integer totalMutualFunds = 0;

    @Column
    private Integer totalActiveSips = 0;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskProfile;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
