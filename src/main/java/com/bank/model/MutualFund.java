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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mutual_funds")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MutualFund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fundCode;

    @Column(nullable = false)
    private String fundName;

    @Column(nullable = false)
    private String amc; // Asset Management Company

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal currentNav; // Net Asset Value

    @Column(precision = 10, scale = 4)
    private BigDecimal previousNav;

    @Column(precision = 15, scale = 2)
    private BigDecimal aum; // Assets Under Management

    @Column
    private Double expenseRatio;

    @Column
    private Double returns1Year;

    @Column
    private Double returns3Year;

    @Column
    private Double returns5Year;

    @Column(precision = 15, scale = 2)
    private BigDecimal minInvestment;

    @Column(precision = 15, scale = 2)
    private BigDecimal minSipAmount;

    @Column
    private Boolean sipAvailable = true;

    @Column
    private Boolean lumpsumAvailable = true;

    @Column
    private Integer exitLoadDays;

    @Column
    private Double exitLoadPercentage;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Boolean isActive = true;

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
