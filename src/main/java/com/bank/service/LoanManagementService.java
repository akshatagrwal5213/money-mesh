package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanManagementService {
    
    @Autowired
    private EmiScheduleRepository emiScheduleRepository;
    
    @Autowired
    private LoanPrepaymentRepository loanPrepaymentRepository;
    
    @Autowired
    private LoanRestructureRepository loanRestructureRepository;
    
    @Autowired
    private LoanForeclosureRepository loanForeclosureRepository;
    
    @Autowired
    private OverdueTrackingRepository overdueTrackingRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private RewardsService rewardsService;
    
    // Generate EMI schedule for a loan
    public List<EmiScheduleDto> generateEmiSchedule(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        // Check if schedule already exists
        List<EmiSchedule> existingSchedule = emiScheduleRepository.findByLoanId(loanId);
        if (!existingSchedule.isEmpty()) {
            return existingSchedule.stream()
                    .map(this::convertToEmiDto)
                    .collect(Collectors.toList());
        }
        
        // Generate new schedule
        Double loanAmount = loan.getPrincipalAmount();
        Double annualRate = loan.getInterestRate();
        Integer tenureMonths = loan.getTenureMonths();
        
        Double monthlyRate = annualRate / 12 / 100;
        
        // EMI calculation using reducing balance method
        Double emi = calculateEMI(loanAmount, monthlyRate, tenureMonths);
        
        Double outstandingPrincipal = loanAmount;
        LocalDate startDate = LocalDate.now();
        
        List<EmiSchedule> schedules = new ArrayList<>();
        
        for (int i = 1; i <= tenureMonths; i++) {
            EmiSchedule schedule = new EmiSchedule();
            schedule.setLoan(loan);
            schedule.setEmiNumber(i);
            schedule.setDueDate(startDate.plusMonths(i));
            schedule.setEmiAmount(emi);
            
            // Calculate principal and interest components
            Double interestComponent = outstandingPrincipal * monthlyRate;
            Double principalComponent = emi - interestComponent;
            
            schedule.setInterestComponent(interestComponent);
            schedule.setPrincipalComponent(principalComponent);
            
            outstandingPrincipal -= principalComponent;
            schedule.setOutstandingPrincipal(Math.max(0, outstandingPrincipal));
            
            schedule.setIsPaid(false);
            schedule.setDaysOverdue(0);
            schedule.setPenaltyAmount(0.0);
            
            schedules.add(schedule);
        }
        
        emiScheduleRepository.saveAll(schedules);
        
        return schedules.stream()
                .map(this::convertToEmiDto)
                .collect(Collectors.toList());
    }
    
    // Calculate EMI using formula: P * r * (1+r)^n / ((1+r)^n - 1)
    private Double calculateEMI(Double principal, Double monthlyRate, Integer months) {
        if (monthlyRate == 0) {
            return principal / months;
        }
        
        Double numerator = principal * monthlyRate * Math.pow(1 + monthlyRate, months);
        Double denominator = Math.pow(1 + monthlyRate, months) - 1;
        
        return numerator / denominator;
    }
    
    // Record EMI payment
    public EmiScheduleDto recordEmiPayment(Long emiId, Double amount, String reference) {
        EmiSchedule emi = emiScheduleRepository.findById(emiId)
                .orElseThrow(() -> new ResourceNotFoundException("EMI schedule not found"));
        
        if (emi.getIsPaid()) {
            throw new IllegalStateException("EMI already paid");
        }
        
        emi.setIsPaid(true);
        emi.setPaidDate(LocalDate.now());
        emi.setPaidAmount(amount);
        
        // Check if payment is on time
        long daysOverdue = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());
        if (daysOverdue > 0) {
            emi.setDaysOverdue((int) daysOverdue);
            Double penalty = calculatePenalty(emi.getEmiAmount(), (int) daysOverdue);
            emi.setPenaltyAmount(penalty);
        }
        
        emiScheduleRepository.save(emi);
        
        // Award points for timely payment (integration with rewards)
        if (daysOverdue <= 0) {
            Long customerId = emi.getLoan().getCustomer().getId();
            rewardsService.awardPoints(customerId, 500, RewardCategory.EMI_PAYMENT, 
                    "Timely EMI payment - EMI #" + emi.getEmiNumber());
        }
        
        return convertToEmiDto(emi);
    }
    
    // Process prepayment
    public PrepaymentResponse processPrepayment(PrepaymentRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        Customer customer = loan.getCustomer();
        
        // Get outstanding principal from EMI schedule
        List<EmiSchedule> unpaidEmis = emiScheduleRepository.findByLoanIdAndIsPaidFalse(request.getLoanId());
        Double outstandingPrincipal = unpaidEmis.stream()
                .mapToDouble(EmiSchedule::getPrincipalComponent)
                .sum();
        
        if (request.getPrepaymentAmount() > outstandingPrincipal) {
            throw new IllegalArgumentException("Prepayment amount cannot exceed outstanding principal");
        }
        
        // Check for prepayment charges (waived for Platinum/Diamond tiers)
        Double prepaymentCharges = 0.0;
        CustomerTierInfo tierInfo = rewardsService.getTierInfo(customer.getId());
        
        if (tierInfo == null || 
            (tierInfo.getTierLevel() != CustomerTierLevel.PLATINUM && 
             tierInfo.getTierLevel() != CustomerTierLevel.DIAMOND)) {
            prepaymentCharges = request.getPrepaymentAmount() * 0.02; // 2% charges
        }
        
        // Calculate interest saved
        Double totalInterestRemaining = unpaidEmis.stream()
                .mapToDouble(EmiSchedule::getInterestComponent)
                .sum();
        
    Double interestSaved;
    Integer tenureReduced;
        
        LoanPrepayment prepayment = new LoanPrepayment();
        prepayment.setLoan(loan);
        prepayment.setCustomer(customer);
        prepayment.setPrepaymentType(request.getPrepaymentType());
        prepayment.setPrepaymentDate(LocalDate.now());
        prepayment.setPrepaymentAmount(request.getPrepaymentAmount());
        prepayment.setPrepaymentCharges(prepaymentCharges);
        prepayment.setOutstandingBeforePrepayment(outstandingPrincipal);
        
        if (request.getPrepaymentType() == PrepaymentType.FULL) {
            // Full prepayment - close the loan
            interestSaved = totalInterestRemaining;
            tenureReduced = unpaidEmis.size();
            
            prepayment.setOutstandingAfterPrepayment(0.0);
            prepayment.setInterestSaved(interestSaved);
            prepayment.setTenureReduced(tenureReduced);
            
            // Mark all unpaid EMIs as paid
            for (EmiSchedule emi : unpaidEmis) {
                emi.setIsPaid(true);
                emi.setPaidDate(LocalDate.now());
                emi.setPaidAmount(0.0);
            }
            emiScheduleRepository.saveAll(unpaidEmis);
            
            // Award milestone bonus for loan closure
            rewardsService.awardPoints(customer.getId(), 1000, RewardCategory.LOAN_CLOSURE, 
                    "Loan fully prepaid - Loan ID: " + request.getLoanId());
            
        } else {
            // Partial prepayment - reduce tenure (keep EMI same)
            Double remainingPrincipal = outstandingPrincipal - request.getPrepaymentAmount();
            prepayment.setOutstandingAfterPrepayment(remainingPrincipal);
            
            // Recalculate EMI schedule
            Double currentEmi = unpaidEmis.get(0).getEmiAmount();
            Double monthlyRate = 10.5 / 12 / 100;
            
            // Calculate new tenure
            Integer newTenure = calculateTenure(remainingPrincipal, monthlyRate, currentEmi);
            tenureReduced = unpaidEmis.size() - newTenure;
            
            // Calculate interest saved
            interestSaved = totalInterestRemaining - (currentEmi * newTenure - remainingPrincipal);
            
            prepayment.setTenureReduced(tenureReduced);
            prepayment.setInterestSaved(interestSaved);
            prepayment.setNewEmiAmount(currentEmi);
            
            // Remove excess EMIs
            for (int i = unpaidEmis.size() - 1; i >= newTenure; i--) {
                emiScheduleRepository.delete(unpaidEmis.get(i));
            }
        }
        
        prepayment.setTransactionReference(request.getPaymentReference());
        loanPrepaymentRepository.save(prepayment);
        
        // Award 2% cashback on prepayment amount
        rewardsService.awardCashback(customer.getId(), request.getPrepaymentAmount(), 
                2.0, "Loan prepayment cashback");
        
        return convertToPrepaymentResponse(prepayment);
    }
    
    // Calculate tenure given principal, rate and EMI
    private Integer calculateTenure(Double principal, Double monthlyRate, Double emi) {
        if (monthlyRate == 0) {
            return (int) Math.ceil(principal / emi);
        }
        
        Double numerator = Math.log(emi / (emi - principal * monthlyRate));
        Double denominator = Math.log(1 + monthlyRate);
        
        return (int) Math.ceil(numerator / denominator);
    }
    
    // Request loan restructure
    public RestructureResponse requestRestructure(RestructureRequest request) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        Customer customer = loan.getCustomer();
        
        // Get current loan details
        List<EmiSchedule> unpaidEmis = emiScheduleRepository.findByLoanIdAndIsPaidFalse(request.getLoanId());
        Double outstandingPrincipal = unpaidEmis.stream()
                .mapToDouble(EmiSchedule::getPrincipalComponent)
                .sum();
        
    // Safely resolve current EMI (may be null) and tenure
    Double currentEmiObj = unpaidEmis.isEmpty() ? null : unpaidEmis.get(0).getEmiAmount();
    double currentEmi = currentEmiObj != null ? currentEmiObj : 0.0;
    Integer currentTenure = unpaidEmis.size();
    Double currentRate = 10.5; // Should come from loan details
        
        LoanRestructure restructure = new LoanRestructure();
        restructure.setLoan(loan);
        restructure.setCustomer(customer);
        restructure.setReason(request.getReason());
        restructure.setRequestDate(LocalDate.now());
        restructure.setCustomerJustification(request.getCustomerJustification());
        
        // Original terms
        restructure.setOriginalEmiAmount(currentEmi);
        restructure.setOriginalTenureMonths(currentTenure);
        restructure.setOriginalInterestRate(currentRate);
        restructure.setOriginalOutstanding(outstandingPrincipal);
        
        // Calculate new terms
        Integer newTenure = request.getNewTenureMonths();
        if (newTenure == null) {
            newTenure = currentTenure + 12; // Default: extend by 1 year (autoboxed)
        }
    Double newRate = request.getNewInterestRate() != null ? request.getNewInterestRate() : currentRate;
        
        Double monthlyRate = newRate / 12 / 100;
        Double newEmi = calculateEMI(outstandingPrincipal, monthlyRate, newTenure);
        
        restructure.setNewEmiAmount(newEmi);
        restructure.setNewTenureMonths(newTenure);
        restructure.setNewInterestRate(newRate);
        
        // Check for restructuring charges (free for Platinum/Diamond)
        Double charges = 0.0;
        CustomerTierInfo tierInfo = rewardsService.getTierInfo(customer.getId());
        
        if (tierInfo == null || 
            (tierInfo.getTierLevel() != CustomerTierLevel.PLATINUM && 
             tierInfo.getTierLevel() != CustomerTierLevel.DIAMOND)) {
            charges = 5000.0; // ₹5000 restructuring charges
        }
        
        restructure.setRestructuringCharges(charges);
        restructure.setAdditionalInterest((newEmi * newTenure) - outstandingPrincipal - 
                (currentEmi * currentTenure - outstandingPrincipal));
        
        restructure.setIsApproved(false);
        restructure.setIsImplemented(false);
        
        loanRestructureRepository.save(restructure);
        
        return convertToRestructureResponse(restructure);
    }
    
    // Approve restructure request
    public RestructureResponse approveRestructure(Long restructureId, String bankRemarks) {
        LoanRestructure restructure = loanRestructureRepository.findById(restructureId)
                .orElseThrow(() -> new ResourceNotFoundException("Restructure request not found"));
        
        restructure.setIsApproved(true);
        restructure.setApprovalDate(LocalDate.now());
        restructure.setBankRemarks(bankRemarks);
        
        loanRestructureRepository.save(restructure);
        
        return convertToRestructureResponse(restructure);
    }
    
    // Implement approved restructure
    public RestructureResponse implementRestructure(Long restructureId) {
        LoanRestructure restructure = loanRestructureRepository.findById(restructureId)
                .orElseThrow(() -> new ResourceNotFoundException("Restructure request not found"));
        
        if (!restructure.getIsApproved()) {
            throw new IllegalStateException("Restructure request not approved");
        }
        
        if (restructure.getIsImplemented()) {
            throw new IllegalStateException("Restructure already implemented");
        }
        
        // Delete existing unpaid EMIs
        List<EmiSchedule> unpaidEmis = emiScheduleRepository.findByLoanIdAndIsPaidFalse(restructure.getLoan().getId());
        emiScheduleRepository.deleteAll(unpaidEmis);
        
        // Generate new EMI schedule
        Double outstandingPrincipal = restructure.getOriginalOutstanding();
        Double monthlyRate = restructure.getNewInterestRate() / 12 / 100;
        Integer tenureMonths = restructure.getNewTenureMonths();
        Double emi = restructure.getNewEmiAmount();
        
        LocalDate startDate = LocalDate.now();
        List<EmiSchedule> newSchedules = new ArrayList<>();
        
        for (int i = 1; i <= tenureMonths; i++) {
            EmiSchedule schedule = new EmiSchedule();
            schedule.setLoan(restructure.getLoan());
            schedule.setEmiNumber(i);
            schedule.setDueDate(startDate.plusMonths(i));
            schedule.setEmiAmount(emi);
            
            Double interestComponent = outstandingPrincipal * monthlyRate;
            Double principalComponent = emi - interestComponent;
            
            schedule.setInterestComponent(interestComponent);
            schedule.setPrincipalComponent(principalComponent);
            
            outstandingPrincipal -= principalComponent;
            schedule.setOutstandingPrincipal(Math.max(0, outstandingPrincipal));
            
            schedule.setIsPaid(false);
            schedule.setDaysOverdue(0);
            schedule.setPenaltyAmount(0.0);
            
            newSchedules.add(schedule);
        }
        
        emiScheduleRepository.saveAll(newSchedules);
        
        restructure.setIsImplemented(true);
        restructure.setEffectiveDate(LocalDate.now());
        loanRestructureRepository.save(restructure);
        
        return convertToRestructureResponse(restructure);
    }
    
    // Calculate foreclosure amount
    public ForeclosureResponse calculateForeclosureAmount(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        Customer customer = loan.getCustomer();
        
        List<EmiSchedule> unpaidEmis = emiScheduleRepository.findByLoanIdAndIsPaidFalse(loanId);
        
        Double outstandingPrincipal = unpaidEmis.stream()
                .mapToDouble(EmiSchedule::getPrincipalComponent)
                .sum();
        
        Double pendingInterest = unpaidEmis.stream()
                .mapToDouble(EmiSchedule::getInterestComponent)
                .sum();
        
        // Check for foreclosure charges
        Double foreclosureCharges = 0.0;
        CustomerTierInfo tierInfo = rewardsService.getTierInfo(customer.getId());
        
        if (tierInfo == null || 
            (tierInfo.getTierLevel() != CustomerTierLevel.PLATINUM && 
             tierInfo.getTierLevel() != CustomerTierLevel.DIAMOND)) {
            foreclosureCharges = outstandingPrincipal * 0.04; // 4% of outstanding
        }
        
        Double totalAmount = outstandingPrincipal + pendingInterest + foreclosureCharges;
        
        ForeclosureResponse response = new ForeclosureResponse();
        response.setLoanId(loanId);
        response.setStatus(ForeclosureStatus.REQUESTED);
        response.setRequestDate(LocalDate.now());
        response.setOutstandingPrincipal(outstandingPrincipal);
        response.setPendingInterest(pendingInterest);
        response.setForeclosureCharges(foreclosureCharges);
        response.setPrepaymentPenalty(0.0);
        response.setTotalAmountDue(totalAmount);
        response.setRemainingEmis(unpaidEmis.size());
        response.setInterestSaved(pendingInterest);
        
        return response;
    }
    
    // Process foreclosure
    public ForeclosureResponse processForeclosure(ForeclosureRequest request, Double paymentAmount, String reference) {
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        
        Customer customer = loan.getCustomer();
        
        ForeclosureResponse calculation = calculateForeclosureAmount(request.getLoanId());
        
        if (paymentAmount < calculation.getTotalAmountDue()) {
            throw new InsufficientFundsException("Payment amount insufficient for foreclosure");
        }
        
        LoanForeclosure foreclosure = new LoanForeclosure();
        foreclosure.setLoan(loan);
        foreclosure.setCustomer(customer);
        foreclosure.setStatus(ForeclosureStatus.COMPLETED);
        foreclosure.setRequestDate(LocalDate.now());
        foreclosure.setApprovalDate(LocalDate.now());
        foreclosure.setForeclosureDate(LocalDate.now());
        foreclosure.setOutstandingPrincipal(calculation.getOutstandingPrincipal());
        foreclosure.setPendingInterest(calculation.getPendingInterest());
        foreclosure.setForeclosureCharges(calculation.getForeclosureCharges());
        foreclosure.setPrepaymentPenalty(0.0);
        foreclosure.setTotalAmountDue(calculation.getTotalAmountDue());
        foreclosure.setAmountPaid(paymentAmount);
        foreclosure.setPaymentDate(LocalDate.now());
        foreclosure.setPaymentReference(reference);
        foreclosure.setRemainingEmis(calculation.getRemainingEmis());
        foreclosure.setInterestSaved(calculation.getInterestSaved());
        foreclosure.setReason(request.getReason());
        
        loanForeclosureRepository.save(foreclosure);
        
        // Mark all unpaid EMIs as paid
        List<EmiSchedule> unpaidEmis = emiScheduleRepository.findByLoanIdAndIsPaidFalse(request.getLoanId());
        for (EmiSchedule emi : unpaidEmis) {
            emi.setIsPaid(true);
            emi.setPaidDate(LocalDate.now());
            emi.setPaidAmount(0.0);
        }
        emiScheduleRepository.saveAll(unpaidEmis);
        
        // Award milestone bonus for loan closure
        rewardsService.awardPoints(customer.getId(), 1000, RewardCategory.LOAN_CLOSURE, 
                "Loan foreclosed - Loan ID: " + request.getLoanId());
        
        return convertToForeclosureResponse(foreclosure);
    }
    
    // Check and update overdue EMIs (scheduled job)
    public void checkOverdueEmis() {
        LocalDate today = LocalDate.now();
        List<EmiSchedule> overdueEmis = emiScheduleRepository.findAllOverdueEmis(today);
        
        for (EmiSchedule emi : overdueEmis) {
            long daysOverdue = ChronoUnit.DAYS.between(emi.getDueDate(), today);
            emi.setDaysOverdue((int) daysOverdue);
            
            Double penalty = calculatePenalty(emi.getEmiAmount(), (int) daysOverdue);
            emi.setPenaltyAmount(penalty);
            
            // Create or update overdue tracking
            OverdueStatus status = determineOverdueStatus((int) daysOverdue);
            
            List<OverdueTracking> existingTracking = overdueTrackingRepository
                    .findActiveOverduesByLoanId(emi.getLoan().getId());
            
            OverdueTracking tracking = existingTracking.stream()
                    .filter(t -> t.getEmiSchedule().getId().equals(emi.getId()))
                    .findFirst()
                    .orElse(new OverdueTracking());
            
            if (tracking.getId() == null) {
                tracking.setLoan(emi.getLoan());
                tracking.setCustomer(emi.getLoan().getCustomer());
                tracking.setEmiSchedule(emi);
                tracking.setEmiDueDate(emi.getDueDate());
                tracking.setEmiAmount(emi.getEmiAmount());
            }
            
            tracking.setOverdueStatus(status);
            tracking.setDaysOverdue((int) daysOverdue);
            tracking.setTotalOverdueAmount(emi.getEmiAmount() + penalty);
            tracking.setPenaltyAmount(penalty);
            tracking.setIsResolved(false);
            
            overdueTrackingRepository.save(tracking);
        }
        
        emiScheduleRepository.saveAll(overdueEmis);
    }
    
    // Calculate penalty based on days overdue
    private Double calculatePenalty(Double emiAmount, Integer daysOverdue) {
        if (daysOverdue <= 0) return 0.0;
        
        Double dailyPenaltyRate = 0.001; // 0.1% per day
        return emiAmount * dailyPenaltyRate * daysOverdue;
    }
    
    // Determine overdue status based on days
    private OverdueStatus determineOverdueStatus(Integer daysOverdue) {
        if (daysOverdue <= 0) return OverdueStatus.CURRENT;
        if (daysOverdue <= 30) return OverdueStatus.OVERDUE_1_30;
        if (daysOverdue <= 60) return OverdueStatus.OVERDUE_31_60;
        if (daysOverdue <= 90) return OverdueStatus.OVERDUE_61_90;
        return OverdueStatus.OVERDUE_90_PLUS;
    }
    
    // Conversion methods
    private EmiScheduleDto convertToEmiDto(EmiSchedule emi) {
        EmiScheduleDto dto = new EmiScheduleDto();
        dto.setId(emi.getId());
        dto.setEmiNumber(emi.getEmiNumber());
        dto.setDueDate(emi.getDueDate());
        dto.setEmiAmount(emi.getEmiAmount());
        dto.setPrincipalComponent(emi.getPrincipalComponent());
        dto.setInterestComponent(emi.getInterestComponent());
        dto.setOutstandingPrincipal(emi.getOutstandingPrincipal());
        dto.setIsPaid(emi.getIsPaid());
        dto.setPaidDate(emi.getPaidDate());
        dto.setPaidAmount(emi.getPaidAmount());
        dto.setDaysOverdue(emi.getDaysOverdue());
        dto.setPenaltyAmount(emi.getPenaltyAmount());
        return dto;
    }
    
    private PrepaymentResponse convertToPrepaymentResponse(LoanPrepayment prepayment) {
        PrepaymentResponse response = new PrepaymentResponse();
        response.setId(prepayment.getId());
        response.setLoanId(prepayment.getLoan().getId());
        response.setPrepaymentType(prepayment.getPrepaymentType());
        response.setPrepaymentAmount(prepayment.getPrepaymentAmount());
        response.setPrepaymentCharges(prepayment.getPrepaymentCharges());
        response.setOutstandingBeforePrepayment(prepayment.getOutstandingBeforePrepayment());
        response.setOutstandingAfterPrepayment(prepayment.getOutstandingAfterPrepayment());
        response.setInterestSaved(prepayment.getInterestSaved());
        response.setTenureReduced(prepayment.getTenureReduced());
        response.setNewEmiAmount(prepayment.getNewEmiAmount());
        response.setPrepaymentDate(prepayment.getPrepaymentDate());
        response.setTransactionReference(prepayment.getTransactionReference());
        return response;
    }
    
    private RestructureResponse convertToRestructureResponse(LoanRestructure restructure) {
        RestructureResponse response = new RestructureResponse();
        response.setId(restructure.getId());
        response.setLoanId(restructure.getLoan().getId());
        response.setReason(restructure.getReason());
        response.setRequestDate(restructure.getRequestDate());
        response.setApprovalDate(restructure.getApprovalDate());
        response.setEffectiveDate(restructure.getEffectiveDate());
        response.setOriginalEmiAmount(restructure.getOriginalEmiAmount());
        response.setOriginalTenureMonths(restructure.getOriginalTenureMonths());
        response.setOriginalInterestRate(restructure.getOriginalInterestRate());
        response.setOriginalOutstanding(restructure.getOriginalOutstanding());
        response.setNewEmiAmount(restructure.getNewEmiAmount());
        response.setNewTenureMonths(restructure.getNewTenureMonths());
        response.setNewInterestRate(restructure.getNewInterestRate());
        response.setRestructuringCharges(restructure.getRestructuringCharges());
        response.setAdditionalInterest(restructure.getAdditionalInterest());
        response.setIsApproved(restructure.getIsApproved());
        response.setIsImplemented(restructure.getIsImplemented());
        response.setCustomerJustification(restructure.getCustomerJustification());
        response.setBankRemarks(restructure.getBankRemarks());
        return response;
    }
    
    private ForeclosureResponse convertToForeclosureResponse(LoanForeclosure foreclosure) {
        ForeclosureResponse response = new ForeclosureResponse();
        response.setId(foreclosure.getId());
        response.setLoanId(foreclosure.getLoan().getId());
        response.setStatus(foreclosure.getStatus());
        response.setRequestDate(foreclosure.getRequestDate());
        response.setApprovalDate(foreclosure.getApprovalDate());
        response.setForeclosureDate(foreclosure.getForeclosureDate());
        response.setOutstandingPrincipal(foreclosure.getOutstandingPrincipal());
        response.setPendingInterest(foreclosure.getPendingInterest());
        response.setForeclosureCharges(foreclosure.getForeclosureCharges());
        response.setPrepaymentPenalty(foreclosure.getPrepaymentPenalty());
        response.setTotalAmountDue(foreclosure.getTotalAmountDue());
        response.setAmountPaid(foreclosure.getAmountPaid());
        response.setPaymentDate(foreclosure.getPaymentDate());
        response.setPaymentReference(foreclosure.getPaymentReference());
        response.setRemainingEmis(foreclosure.getRemainingEmis());
        response.setInterestSaved(foreclosure.getInterestSaved());
        response.setReason(foreclosure.getReason());
        response.setBankRemarks(foreclosure.getBankRemarks());
        return response;
    }
}
