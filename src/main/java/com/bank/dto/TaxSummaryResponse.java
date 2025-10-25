package com.bank.dto;

import java.util.List;

public class TaxSummaryResponse {
    
    private String financialYear;
    private Double grossTotalIncome;
    private Double totalTaxLiability;
    private Double tdsDeducted;
    private Double advanceTaxPaid;
    private Double taxPayable;
    private Double taxRefund;
    
    // Deductions summary
    private Double totalSection80CDeductions;
    private Double totalSection80DDeductions;
    private Double totalOtherDeductions;
    
    // Capital gains summary
    private Double shortTermCapitalGains;
    private Double longTermCapitalGains;
    private Double totalCapitalGainsTax;
    
    // Documents
    private List<TaxDocumentSummary> documents;
    
    // Payment history
    private List<TaxPaymentSummary> payments;
    
    // Recommendations
    private List<String> taxSavingRecommendations;
    
    // Getters and Setters
    
    public String getFinancialYear() {
        return financialYear;
    }

    public void setFinancialYear(String financialYear) {
        this.financialYear = financialYear;
    }

    public Double getGrossTotalIncome() {
        return grossTotalIncome;
    }

    public void setGrossTotalIncome(Double grossTotalIncome) {
        this.grossTotalIncome = grossTotalIncome;
    }

    public Double getTotalTaxLiability() {
        return totalTaxLiability;
    }

    public void setTotalTaxLiability(Double totalTaxLiability) {
        this.totalTaxLiability = totalTaxLiability;
    }

    public Double getTdsDeducted() {
        return tdsDeducted;
    }

    public void setTdsDeducted(Double tdsDeducted) {
        this.tdsDeducted = tdsDeducted;
    }

    public Double getAdvanceTaxPaid() {
        return advanceTaxPaid;
    }

    public void setAdvanceTaxPaid(Double advanceTaxPaid) {
        this.advanceTaxPaid = advanceTaxPaid;
    }

    public Double getTaxPayable() {
        return taxPayable;
    }

    public void setTaxPayable(Double taxPayable) {
        this.taxPayable = taxPayable;
    }

    public Double getTaxRefund() {
        return taxRefund;
    }

    public void setTaxRefund(Double taxRefund) {
        this.taxRefund = taxRefund;
    }

    public Double getTotalSection80CDeductions() {
        return totalSection80CDeductions;
    }

    public void setTotalSection80CDeductions(Double totalSection80CDeductions) {
        this.totalSection80CDeductions = totalSection80CDeductions;
    }

    public Double getTotalSection80DDeductions() {
        return totalSection80DDeductions;
    }

    public void setTotalSection80DDeductions(Double totalSection80DDeductions) {
        this.totalSection80DDeductions = totalSection80DDeductions;
    }

    public Double getTotalOtherDeductions() {
        return totalOtherDeductions;
    }

    public void setTotalOtherDeductions(Double totalOtherDeductions) {
        this.totalOtherDeductions = totalOtherDeductions;
    }

    public Double getShortTermCapitalGains() {
        return shortTermCapitalGains;
    }

    public void setShortTermCapitalGains(Double shortTermCapitalGains) {
        this.shortTermCapitalGains = shortTermCapitalGains;
    }

    public Double getLongTermCapitalGains() {
        return longTermCapitalGains;
    }

    public void setLongTermCapitalGains(Double longTermCapitalGains) {
        this.longTermCapitalGains = longTermCapitalGains;
    }

    public Double getTotalCapitalGainsTax() {
        return totalCapitalGainsTax;
    }

    public void setTotalCapitalGainsTax(Double totalCapitalGainsTax) {
        this.totalCapitalGainsTax = totalCapitalGainsTax;
    }

    public List<TaxDocumentSummary> getDocuments() {
        return documents;
    }

    public void setDocuments(List<TaxDocumentSummary> documents) {
        this.documents = documents;
    }

    public List<TaxPaymentSummary> getPayments() {
        return payments;
    }

    public void setPayments(List<TaxPaymentSummary> payments) {
        this.payments = payments;
    }

    public List<String> getTaxSavingRecommendations() {
        return taxSavingRecommendations;
    }

    public void setTaxSavingRecommendations(List<String> taxSavingRecommendations) {
        this.taxSavingRecommendations = taxSavingRecommendations;
    }
    
    // Inner classes
    public static class TaxDocumentSummary {
        private Long id;
        private String documentType;
        private String generatedDate;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getDocumentType() {
            return documentType;
        }
        
        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }
        
        public String getGeneratedDate() {
            return generatedDate;
        }
        
        public void setGeneratedDate(String generatedDate) {
            this.generatedDate = generatedDate;
        }
    }
    
    public static class TaxPaymentSummary {
        private String paymentType;
        private Double amount;
        private String paymentDate;
        
        public String getPaymentType() {
            return paymentType;
        }
        
        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }
        
        public Double getAmount() {
            return amount;
        }
        
        public void setAmount(Double amount) {
            this.amount = amount;
        }
        
        public String getPaymentDate() {
            return paymentDate;
        }
        
        public void setPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
        }
    }
}
