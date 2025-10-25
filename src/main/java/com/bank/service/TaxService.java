package com.bank.service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.CapitalGainRequest;
import com.bank.dto.TaxCalculationRequest;
import com.bank.dto.TaxCalculationResponse;
import com.bank.dto.TaxDeductionRequest;
import com.bank.dto.TaxPaymentRequest;
import com.bank.dto.TaxSummaryResponse;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.AssetType;
import com.bank.model.CapitalGain;
import com.bank.model.CapitalGainType;
import com.bank.model.Customer;
import com.bank.model.DeductionType;
import com.bank.model.TaxDeduction;
import com.bank.model.TaxDocument;
import com.bank.model.TaxPayment;
import com.bank.model.TaxRegime;
import com.bank.repository.CapitalGainRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.TaxDeductionRepository;
import com.bank.repository.TaxDocumentRepository;
import com.bank.repository.TaxPaymentRepository;

@Service
public class TaxService {

    @Autowired
    private TaxDocumentRepository taxDocumentRepository;

    @Autowired
    private TaxDeductionRepository taxDeductionRepository;

    @Autowired
    private TaxPaymentRepository taxPaymentRepository;

    @Autowired
    private CapitalGainRepository capitalGainRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Calculate income tax based on regime and deductions
     */
    public TaxCalculationResponse calculateTax(Long customerId, TaxCalculationRequest request) {
        TaxCalculationResponse response = new TaxCalculationResponse();
        response.setFinancialYear(request.getFinancialYear());
        response.setGrossTotalIncome(request.getGrossTotalIncome());
        response.setTaxRegime(request.getTaxRegime());

        if (request.getTaxRegime() == TaxRegime.OLD_REGIME) {
            calculateOldRegimeTax(request, response);
        } else {
            calculateNewRegimeTax(request, response);
        }

        // Add regime comparison
        TaxCalculationResponse.TaxRegimeComparison comparison = new TaxCalculationResponse.TaxRegimeComparison();
        
        // Calculate other regime for comparison
        TaxCalculationRequest otherRegimeRequest = copyRequest(request);
        otherRegimeRequest.setTaxRegime(request.getTaxRegime() == TaxRegime.OLD_REGIME ? TaxRegime.NEW_REGIME : TaxRegime.OLD_REGIME);
        TaxCalculationResponse otherRegimeResponse = new TaxCalculationResponse();
        
        if (otherRegimeRequest.getTaxRegime() == TaxRegime.OLD_REGIME) {
            calculateOldRegimeTax(otherRegimeRequest, otherRegimeResponse);
        } else {
            calculateNewRegimeTax(otherRegimeRequest, otherRegimeResponse);
        }
        
        comparison.setOldRegimeTax(request.getTaxRegime() == TaxRegime.OLD_REGIME ? 
            response.getTotalTaxLiability() : otherRegimeResponse.getTotalTaxLiability());
        comparison.setNewRegimeTax(request.getTaxRegime() == TaxRegime.NEW_REGIME ? 
            response.getTotalTaxLiability() : otherRegimeResponse.getTotalTaxLiability());
        comparison.setDifference(comparison.getNewRegimeTax() - comparison.getOldRegimeTax());
        
        comparison.setRecommendation(comparison.getDifference() < 0 ? 
            "New regime is beneficial - Save ₹" + Math.abs(comparison.getDifference()) :
            "Old regime is beneficial - Save ₹" + comparison.getDifference());
        
        response.setRegimeComparison(comparison);

        return response;
    }

    private void calculateOldRegimeTax(TaxCalculationRequest request, TaxCalculationResponse response) {
        Double income = request.getGrossTotalIncome();
        
        // Calculate total deductions
        Map<String, Double> deductions = new HashMap<>();
        Double totalDeductions = 0.0;
        
        if (request.getStandardDeduction() != null && request.getStandardDeduction() > 0) {
            deductions.put("Standard Deduction", Math.min(request.getStandardDeduction(), 50000.0));
            totalDeductions += deductions.get("Standard Deduction");
        }
        
        if (request.getSection80CDeduction() != null && request.getSection80CDeduction() > 0) {
            deductions.put("Section 80C", Math.min(request.getSection80CDeduction(), 150000.0));
            totalDeductions += deductions.get("Section 80C");
        }
        
        if (request.getSection80DDeduction() != null && request.getSection80DDeduction() > 0) {
            double maxLimit = (request.getAge() != null && request.getAge() >= 60) ? 50000.0 : 25000.0;
            deductions.put("Section 80D", Math.min(request.getSection80DDeduction(), maxLimit));
            totalDeductions += deductions.get("Section 80D");
        }
        
        if (request.getSection80EDeduction() != null && request.getSection80EDeduction() > 0) {
            deductions.put("Section 80E", request.getSection80EDeduction()); // No limit
            totalDeductions += deductions.get("Section 80E");
        }
        
        if (request.getSection80GDeduction() != null && request.getSection80GDeduction() > 0) {
            deductions.put("Section 80G", request.getSection80GDeduction());
            totalDeductions += deductions.get("Section 80G");
        }
        
        if (request.getHraExemption() != null && request.getHraExemption() > 0) {
            deductions.put("HRA Exemption", request.getHraExemption());
            totalDeductions += deductions.get("HRA Exemption");
        }
        
        if (request.getHomeLoanInterest() != null && request.getHomeLoanInterest() > 0) {
            deductions.put("Home Loan Interest (24)", Math.min(request.getHomeLoanInterest(), 200000.0));
            totalDeductions += deductions.get("Home Loan Interest (24)");
        }
        
        if (request.getNpsDeduction() != null && request.getNpsDeduction() > 0) {
            deductions.put("NPS (80CCD(1B))", Math.min(request.getNpsDeduction(), 50000.0));
            totalDeductions += deductions.get("NPS (80CCD(1B))");
        }
        
        if (request.getOtherDeductions() != null && request.getOtherDeductions() > 0) {
            deductions.put("Other Deductions", request.getOtherDeductions());
            totalDeductions += deductions.get("Other Deductions");
        }
        
        response.setTotalDeductions(totalDeductions);
        response.setDeductionBreakdown(deductions);
        
        // Calculate taxable income
        Double taxableIncome = income - totalDeductions;
        response.setTaxableIncome(Math.max(taxableIncome, 0.0));
        
        // Calculate tax based on old regime slabs
        Map<String, Double> slabWiseTax = new HashMap<>();
        double tax = 0.0;
        
        if (taxableIncome <= 250000) {
            slabWiseTax.put("Up to ₹2.5L", 0.0);
        } else if (taxableIncome <= 500000) {
            tax = (taxableIncome - 250000) * 0.05;
            slabWiseTax.put("₹2.5L - ₹5L (5%)", tax);
        } else if (taxableIncome <= 1000000) {
            slabWiseTax.put("₹2.5L - ₹5L (5%)", 250000 * 0.05);
            slabWiseTax.put("₹5L - ₹10L (20%)", (taxableIncome - 500000) * 0.20);
            tax = slabWiseTax.get("₹2.5L - ₹5L (5%)") + slabWiseTax.get("₹5L - ₹10L (20%)");
        } else {
            slabWiseTax.put("₹2.5L - ₹5L (5%)", 250000 * 0.05);
            slabWiseTax.put("₹5L - ₹10L (20%)", 500000 * 0.20);
            slabWiseTax.put("Above ₹10L (30%)", (taxableIncome - 1000000) * 0.30);
            tax = slabWiseTax.get("₹2.5L - ₹5L (5%)") + slabWiseTax.get("₹5L - ₹10L (20%)") + slabWiseTax.get("Above ₹10L (30%)");
        }
        
        response.setSlabWiseTax(slabWiseTax);
        response.setTaxBeforeRebate(tax);
        
        // Apply rebate under 87A
        Double rebate = 0.0;
        if (taxableIncome <= 500000) {
            rebate = Math.min(tax, 12500.0);
        }
        response.setRebateUnder87A(rebate);
        
        Double taxAfterRebate = tax - rebate;
        response.setTaxAfterRebate(taxAfterRebate);
        
        // Add health and education cess (4%)
        Double cess = taxAfterRebate * 0.04;
        response.setHealthEducationCess(cess);
        
        Double totalTaxLiability = taxAfterRebate + cess;
        response.setTotalTaxLiability(Math.round(totalTaxLiability * 100.0) / 100.0);
        
        // Calculate tax payable/refund
        Double taxPaid = response.getTdsDeducted() + response.getAdvanceTaxPaid();
        if (taxPaid > totalTaxLiability) {
            response.setTaxRefund(taxPaid - totalTaxLiability);
            response.setTaxPayable(0.0);
        } else {
            response.setTaxPayable(totalTaxLiability - taxPaid);
            response.setTaxRefund(0.0);
        }
    }

    private void calculateNewRegimeTax(TaxCalculationRequest request, TaxCalculationResponse response) {
        Double income = request.getGrossTotalIncome();
        
        // New regime - no deductions except standard deduction
        Map<String, Double> deductions = new HashMap<>();
        Double totalDeductions = 0.0;
        
        if (request.getStandardDeduction() != null && request.getStandardDeduction() > 0) {
            deductions.put("Standard Deduction", Math.min(request.getStandardDeduction(), 50000.0));
            totalDeductions += deductions.get("Standard Deduction");
        }
        
        response.setTotalDeductions(totalDeductions);
        response.setDeductionBreakdown(deductions);
        
        Double taxableIncome = income - totalDeductions;
        response.setTaxableIncome(Math.max(taxableIncome, 0.0));
        
        // Calculate tax based on new regime slabs (FY 2024-25)
        Map<String, Double> slabWiseTax = new HashMap<>();
        double tax = 0.0;
        
        if (taxableIncome <= 300000) {
            slabWiseTax.put("Up to ₹3L", 0.0);
        } else if (taxableIncome <= 600000) {
            tax = (taxableIncome - 300000) * 0.05;
            slabWiseTax.put("₹3L - ₹6L (5%)", tax);
        } else if (taxableIncome <= 900000) {
            slabWiseTax.put("₹3L - ₹6L (5%)", 300000 * 0.05);
            slabWiseTax.put("₹6L - ₹9L (10%)", (taxableIncome - 600000) * 0.10);
            tax = slabWiseTax.get("₹3L - ₹6L (5%)") + slabWiseTax.get("₹6L - ₹9L (10%)");
        } else if (taxableIncome <= 1200000) {
            slabWiseTax.put("₹3L - ₹6L (5%)", 300000 * 0.05);
            slabWiseTax.put("₹6L - ₹9L (10%)", 300000 * 0.10);
            slabWiseTax.put("₹9L - ₹12L (15%)", (taxableIncome - 900000) * 0.15);
            tax = slabWiseTax.get("₹3L - ₹6L (5%)") + slabWiseTax.get("₹6L - ₹9L (10%)") + slabWiseTax.get("₹9L - ₹12L (15%)");
        } else if (taxableIncome <= 1500000) {
            slabWiseTax.put("₹3L - ₹6L (5%)", 300000 * 0.05);
            slabWiseTax.put("₹6L - ₹9L (10%)", 300000 * 0.10);
            slabWiseTax.put("₹9L - ₹12L (15%)", 300000 * 0.15);
            slabWiseTax.put("₹12L - ₹15L (20%)", (taxableIncome - 1200000) * 0.20);
            tax = slabWiseTax.values().stream().mapToDouble(Double::doubleValue).sum();
        } else {
            slabWiseTax.put("₹3L - ₹6L (5%)", 300000 * 0.05);
            slabWiseTax.put("₹6L - ₹9L (10%)", 300000 * 0.10);
            slabWiseTax.put("₹9L - ₹12L (15%)", 300000 * 0.15);
            slabWiseTax.put("₹12L - ₹15L (20%)", 300000 * 0.20);
            slabWiseTax.put("Above ₹15L (30%)", (taxableIncome - 1500000) * 0.30);
            tax = slabWiseTax.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        
        response.setSlabWiseTax(slabWiseTax);
        response.setTaxBeforeRebate(tax);
        
        // Apply rebate under 87A (for new regime up to 7 lakh)
        Double rebate = 0.0;
        if (taxableIncome <= 700000) {
            rebate = Math.min(tax, 25000.0);
        }
        response.setRebateUnder87A(rebate);
        
        Double taxAfterRebate = tax - rebate;
        response.setTaxAfterRebate(taxAfterRebate);
        
        // Add health and education cess (4%)
        Double cess = taxAfterRebate * 0.04;
        response.setHealthEducationCess(cess);
        
        Double totalTaxLiability = taxAfterRebate + cess;
        response.setTotalTaxLiability(Math.round(totalTaxLiability * 100.0) / 100.0);
        
        // Calculate tax payable/refund
        Double taxPaid = response.getTdsDeducted() + response.getAdvanceTaxPaid();
        if (taxPaid > totalTaxLiability) {
            response.setTaxRefund(taxPaid - totalTaxLiability);
            response.setTaxPayable(0.0);
        } else {
            response.setTaxPayable(totalTaxLiability - taxPaid);
            response.setTaxRefund(0.0);
        }
    }

    private TaxCalculationRequest copyRequest(TaxCalculationRequest source) {
        TaxCalculationRequest copy = new TaxCalculationRequest();
        copy.setFinancialYear(source.getFinancialYear());
        copy.setGrossTotalIncome(source.getGrossTotalIncome());
        copy.setStandardDeduction(source.getStandardDeduction());
        copy.setSection80CDeduction(source.getSection80CDeduction());
        copy.setSection80DDeduction(source.getSection80DDeduction());
        copy.setSection80EDeduction(source.getSection80EDeduction());
        copy.setSection80GDeduction(source.getSection80GDeduction());
        copy.setHraExemption(source.getHraExemption());
        copy.setHomeLoanInterest(source.getHomeLoanInterest());
        copy.setNpsDeduction(source.getNpsDeduction());
        copy.setOtherDeductions(source.getOtherDeductions());
        copy.setAge(source.getAge());
        return copy;
    }

    /**
     * Add tax deduction
     */
    @Transactional
    public TaxDeduction addDeduction(Long customerId, TaxDeductionRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        TaxDeduction deduction = new TaxDeduction();
        deduction.setCustomer(customer);
        deduction.setDeductionType(request.getDeductionType());
        deduction.setAmount(request.getAmount());
        deduction.setFinancialYear(request.getFinancialYear());
        deduction.setDescription(request.getDescription());
        deduction.setProofDocument(request.getProofDocument());
        deduction.setClaimedDate(LocalDateTime.now());
        
        return taxDeductionRepository.save(deduction);
    }

    /**
     * Get all deductions for a customer and year
     */
    public List<TaxDeduction> getDeductions(Long customerId, String financialYear) {
        return taxDeductionRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
    }

    /**
     * Record tax payment
     */
    @Transactional
    public TaxPayment recordPayment(Long customerId, TaxPaymentRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        TaxPayment payment = new TaxPayment();
        payment.setCustomer(customer);
        payment.setPaymentType(request.getPaymentType());
        payment.setAmount(request.getAmount());
        payment.setFinancialYear(request.getFinancialYear());
        payment.setAssessmentYear(request.getAssessmentYear());
        payment.setChallanNumber(request.getChallanNumber());
        payment.setAcknowledgementNumber(request.getAcknowledgementNumber());
        payment.setRemarks(request.getRemarks());
        payment.setPaymentDate(LocalDateTime.now());
        
        return taxPaymentRepository.save(payment);
    }

    /**
     * Get all tax payments for a customer
     */
    public List<TaxPayment> getPayments(Long customerId, String financialYear) {
        return taxPaymentRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
    }

    /**
     * Calculate capital gains
     */
    @Transactional
    public CapitalGain calculateCapitalGain(Long customerId, CapitalGainRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        CapitalGain capitalGain = new CapitalGain();
        capitalGain.setCustomer(customer);
        capitalGain.setAssetType(request.getAssetType());
        capitalGain.setAssetName(request.getAssetName());
        capitalGain.setPurchaseDate(request.getPurchaseDate());
        capitalGain.setSellDate(request.getSellDate());
        capitalGain.setPurchasePrice(request.getPurchasePrice());
        capitalGain.setSellPrice(request.getSellPrice());
        capitalGain.setFinancialYear(request.getFinancialYear());
        capitalGain.setRemarks(request.getRemarks());
        
        // Calculate holding period
        Period holdingPeriod = Period.between(request.getPurchaseDate(), request.getSellDate());
        int holdingMonths = holdingPeriod.getYears() * 12 + holdingPeriod.getMonths();
        
        // Determine if short-term or long-term based on asset type
        boolean isLongTerm;
        AssetType assetType = request.getAssetType();
        
        switch (assetType) {
            case EQUITY:
            case MUTUAL_FUND:
                isLongTerm = holdingMonths >= 12; // 1 year for equity/MF
                break;
            case PROPERTY:
                isLongTerm = holdingMonths >= 24; // 2 years for property
                break;
            default:
                isLongTerm = holdingMonths >= 36; // 3 years for other assets
                break;
        }
        
        capitalGain.setGainType(isLongTerm ? CapitalGainType.LONG_TERM : CapitalGainType.SHORT_TERM);
        
        // Calculate gain
        Double gain = request.getSellPrice() - request.getPurchasePrice();
        capitalGain.setGainAmount(gain);
        capitalGain.setTaxableGain(gain); // Simplified - no indexation
        
        // Calculate tax
        Double tax = 0.0;
        if (isLongTerm) {
            // LTCG: 10% above 1 lakh (for equity), 20% with indexation (for others)
            if (request.getAssetType() == AssetType.EQUITY || request.getAssetType() == AssetType.MUTUAL_FUND) {
                if (gain > 100000) {
                    tax = (gain - 100000) * 0.10;
                }
            } else {
                tax = gain * 0.20; // Simplified - should use indexed cost
            }
        } else {
            // STCG: 15% for equity, slab rate for others
            if (request.getAssetType() == AssetType.EQUITY || request.getAssetType() == AssetType.MUTUAL_FUND) {
                tax = gain * 0.15;
            } else {
                tax = gain * 0.30; // Simplified - should use slab rate
            }
        }
        
        capitalGain.setTaxAmount(tax);
        capitalGain.setRecordedDate(LocalDateTime.now());
        
        return capitalGainRepository.save(capitalGain);
    }

    /**
     * Get capital gains for a customer
     */
    public List<CapitalGain> getCapitalGains(Long customerId, String financialYear) {
        return capitalGainRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
    }

    /**
     * Get tax summary for a financial year
     */
    public TaxSummaryResponse getTaxSummary(Long customerId, String financialYear) {
        TaxSummaryResponse summary = new TaxSummaryResponse();
        summary.setFinancialYear(financialYear);
        
        // Get deductions
        Double total80C = taxDeductionRepository.getDeductionsByTypeAndYear(customerId, financialYear,
            Arrays.asList(DeductionType.SECTION_80C_PPF, DeductionType.SECTION_80C_EPF, 
                         DeductionType.SECTION_80C_ELSS, DeductionType.SECTION_80C_LIC));
        summary.setTotalSection80CDeductions(total80C != null ? total80C : 0.0);
        
        Double total80D = taxDeductionRepository.getDeductionsByTypeAndYear(customerId, financialYear,
            Arrays.asList(DeductionType.SECTION_80D_SELF, DeductionType.SECTION_80D_PARENTS));
        summary.setTotalSection80DDeductions(total80D != null ? total80D : 0.0);
        
        Double totalOther = taxDeductionRepository.getTotalDeductionsByCustomerAndYear(customerId, financialYear);
        if (totalOther != null) {
            summary.setTotalOtherDeductions(totalOther - (total80C != null ? total80C : 0.0) - (total80D != null ? total80D : 0.0));
        }
        
        // Get payments
        Double totalPayments = taxPaymentRepository.getTotalPaymentsByCustomerAndYear(customerId, financialYear);
        summary.setAdvanceTaxPaid(totalPayments != null ? totalPayments : 0.0);
        
        // Get capital gains
        List<CapitalGain> capitalGains = capitalGainRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
        Double stcg = capitalGains.stream()
            .filter(cg -> cg.getGainType() == CapitalGainType.SHORT_TERM)
            .mapToDouble(CapitalGain::getGainAmount).sum();
        Double ltcg = capitalGains.stream()
            .filter(cg -> cg.getGainType() == CapitalGainType.LONG_TERM)
            .mapToDouble(CapitalGain::getGainAmount).sum();
        Double totalCGTax = capitalGainRepository.getTotalCapitalGainsTaxByCustomerAndYear(customerId, financialYear);
        
        summary.setShortTermCapitalGains(stcg);
        summary.setLongTermCapitalGains(ltcg);
        summary.setTotalCapitalGainsTax(totalCGTax != null ? totalCGTax : 0.0);
        
        // Get documents
        List<TaxDocument> documents = taxDocumentRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
        List<TaxSummaryResponse.TaxDocumentSummary> docSummaries = documents.stream()
            .map(doc -> {
                TaxSummaryResponse.TaxDocumentSummary ds = new TaxSummaryResponse.TaxDocumentSummary();
                ds.setId(doc.getId());
                ds.setDocumentType(doc.getDocumentType().name());
                ds.setGeneratedDate(doc.getGeneratedDate().toString());
                return ds;
            }).collect(Collectors.toList());
        summary.setDocuments(docSummaries);
        
        // Get payments summary
        List<TaxPayment> payments = taxPaymentRepository.findByCustomerIdAndFinancialYear(customerId, financialYear);
        List<TaxSummaryResponse.TaxPaymentSummary> paymentSummaries = payments.stream()
            .map(p -> {
                TaxSummaryResponse.TaxPaymentSummary ps = new TaxSummaryResponse.TaxPaymentSummary();
                ps.setPaymentType(p.getPaymentType().name());
                ps.setAmount(p.getAmount());
                ps.setPaymentDate(p.getPaymentDate().toString());
                return ps;
            }).collect(Collectors.toList());
        summary.setPayments(paymentSummaries);
        
        // Tax saving recommendations
        List<String> recommendations = new ArrayList<>();
        if (total80C != null && total80C < 150000) {
            recommendations.add("You can save up to ₹" + (150000 - total80C) + " more under Section 80C");
        }
        if (total80D != null && total80D < 25000) {
            recommendations.add("Consider health insurance to claim up to ₹25,000 under Section 80D");
        }
        recommendations.add("Review NPS investment for additional ₹50,000 deduction under 80CCD(1B)");
        summary.setTaxSavingRecommendations(recommendations);
        
        return summary;
    }

    // --- Wrapper methods for controller (username-based) ---

    public TaxCalculationResponse calculateTaxByUsername(String username, TaxCalculationRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return calculateTax(customer.getId(), request);
    }

    public TaxDeduction addDeductionByUsername(String username, TaxDeductionRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return addDeduction(customer.getId(), request);
    }

    public List<TaxDeduction> getDeductionsByUsername(String username, String financialYear) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return getDeductions(customer.getId(), financialYear);
    }

    public TaxPayment recordPaymentByUsername(String username, TaxPaymentRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return recordPayment(customer.getId(), request);
    }

    public List<TaxPayment> getPaymentsByUsername(String username, String financialYear) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return getPayments(customer.getId(), financialYear);
    }

    public CapitalGain calculateCapitalGainByUsername(String username, CapitalGainRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return calculateCapitalGain(customer.getId(), request);
    }

    public List<CapitalGain> getCapitalGainsByUsername(String username, String financialYear) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return getCapitalGains(customer.getId(), financialYear);
    }

    public TaxSummaryResponse getTaxSummaryByUsername(String username, String financialYear) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return getTaxSummary(customer.getId(), financialYear);
    }
}
