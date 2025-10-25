package com.bank.dto;

import com.bank.model.SipFrequency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SipRequest {
    private String fundCode;
    private String accountNumber;
    private BigDecimal installmentAmount;
    private SipFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate; // Optional
    private Integer totalInstallments; // Optional
    private String folioNumber; // Optional - for existing folio
    private Boolean autoDebit;
}
