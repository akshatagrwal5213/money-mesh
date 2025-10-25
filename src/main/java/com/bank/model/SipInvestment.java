package com.bank.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sip_investments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SipInvestment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sipNumber;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "mutual_fund_id", nullable = false)
    private MutualFund mutualFund;

    @ManyToOne
    @JoinColumn(name = "folio_id")
    private MutualFundHolding folio;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal installmentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SipFrequency frequency;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Integer totalInstallments;

    @Column(nullable = false)
    private Integer installmentsExecuted = 0;

    @Column
    private LocalDate nextInstallmentDate;

    @Column
    private LocalDate lastInstallmentDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalInvested;

    @Column(precision = 15, scale = 4)
    private BigDecimal totalUnits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvestmentStatus status;

    @Column
    private Boolean autoDebit = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime cancelledAt;

    @Column
    private String remarks;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = InvestmentStatus.ACTIVE;
        }
        if (totalInvested == null) {
            totalInvested = BigDecimal.ZERO;
        }
        if (totalUnits == null) {
            totalUnits = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
