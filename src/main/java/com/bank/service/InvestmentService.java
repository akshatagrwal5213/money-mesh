package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.ResourceNotFoundException;
import com.bank.exception.InsufficientFundsException;
import com.bank.model.*;
import com.bank.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final FixedDepositRepository fixedDepositRepository;
    private final RecurringDepositRepository recurringDepositRepository;
    private final MutualFundRepository mutualFundRepository;
    private final MutualFundHoldingRepository mutualFundHoldingRepository;
    private final SipInvestmentRepository sipInvestmentRepository;
    private final InvestmentPortfolioRepository investmentPortfolioRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    // Fixed Deposit Operations
    
    @Transactional
    public FixedDeposit createFixedDeposit(FixedDepositRequest request, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        if (!account.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Account does not belong to customer");
        }
        
        double principalDouble = request.getPrincipalAmount().doubleValue();
        if (account.getBalance() < principalDouble) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        // Deduct from account
        account.setBalance(account.getBalance() - principalDouble);
        accountRepository.save(account);
        
        // Calculate interest and maturity
        Double interestRate = calculateFDInterestRate(request.getTenureMonths());
        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(request.getTenureMonths());
        BigDecimal maturityAmount = calculateMaturityAmount(request.getPrincipalAmount(), interestRate, request.getTenureMonths());
        BigDecimal interestEarned = maturityAmount.subtract(request.getPrincipalAmount());
        
        // Create FD
        FixedDeposit fd = new FixedDeposit();
        fd.setFdNumber("FD" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        fd.setAccount(account);
        fd.setCustomer(customer);
        fd.setPrincipalAmount(request.getPrincipalAmount());
        fd.setInterestRate(interestRate);
        fd.setTenureMonths(request.getTenureMonths());
        fd.setStartDate(startDate);
        fd.setMaturityDate(maturityDate);
        fd.setMaturityAmount(maturityAmount);
        fd.setInterestEarned(interestEarned);
        fd.setStatus(InvestmentStatus.ACTIVE);
        fd.setMaturityAction(request.getMaturityAction());
    Boolean ar = request.getAutoRenew();
    fd.setAutoRenew(ar != null ? ar : false);
        
        FixedDeposit savedFd = fixedDepositRepository.save(fd);
        updatePortfolio(customer);
        
        return savedFd;
    }
    
    @Transactional(readOnly = true)
    public List<FixedDeposit> getCustomerFixedDeposits(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return fixedDepositRepository.findByCustomer(customer);
    }
    
    @Transactional(readOnly = true)
    public FixedDeposit getFixedDepositDetails(String fdNumber, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        FixedDeposit fd = fixedDepositRepository.findByFdNumber(fdNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found"));
        
        if (!fd.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Fixed Deposit does not belong to customer");
        }
        
        return fd;
    }
    
    @Transactional
    public FixedDeposit closeFixedDeposit(String fdNumber, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        FixedDeposit fd = fixedDepositRepository.findByFdNumber(fdNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found"));
        
        if (!fd.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Fixed Deposit does not belong to customer");
        }
        
        if (fd.getStatus() != InvestmentStatus.ACTIVE) {
            throw new IllegalStateException("Fixed Deposit is not active");
        }
        
        // Credit maturity amount to account
        Account account = fd.getAccount();
        account.setBalance(account.getBalance() + fd.getMaturityAmount().doubleValue());
        accountRepository.save(account);
        
        // Update FD status
        fd.setStatus(InvestmentStatus.CLOSED);
        fd.setClosedAt(LocalDateTime.now());
        FixedDeposit closedFd = fixedDepositRepository.save(fd);
        
        updatePortfolio(customer);
        
        return closedFd;
    }
    
    // Recurring Deposit Operations
    
    @Transactional
    public RecurringDeposit createRecurringDeposit(RecurringDepositRequest request, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        if (!account.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Account does not belong to customer");
        }
        
        double installmentDouble = request.getMonthlyInstallment().doubleValue();
        if (account.getBalance() < installmentDouble) {
            throw new InsufficientFundsException("Insufficient balance for first installment");
        }
        
        // Deduct first installment
        account.setBalance(account.getBalance() - installmentDouble);
        accountRepository.save(account);
        
        // Calculate interest and maturity
        Double interestRate = calculateRDInterestRate(request.getTenureMonths());
        LocalDate startDate = LocalDate.now();
        LocalDate maturityDate = startDate.plusMonths(request.getTenureMonths());
        BigDecimal totalDeposit = request.getMonthlyInstallment().multiply(BigDecimal.valueOf(request.getTenureMonths()));
        BigDecimal maturityAmount = calculateRDMaturityAmount(request.getMonthlyInstallment(), interestRate, request.getTenureMonths());
        BigDecimal interestEarned = maturityAmount.subtract(totalDeposit);
        
        // Create RD
        RecurringDeposit rd = new RecurringDeposit();
        rd.setRdNumber("RD" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        rd.setAccount(account);
        rd.setCustomer(customer);
        rd.setMonthlyInstallment(request.getMonthlyInstallment());
        rd.setInterestRate(interestRate);
        rd.setTenureMonths(request.getTenureMonths());
        rd.setStartDate(startDate);
        rd.setMaturityDate(maturityDate);
        rd.setTotalDeposited(request.getMonthlyInstallment());
        rd.setMaturityAmount(maturityAmount);
        rd.setInterestEarned(interestEarned);
        rd.setInstallmentsPaid(1);
        rd.setLastInstallmentDate(startDate);
        rd.setNextInstallmentDate(startDate.plusMonths(1));
        rd.setStatus(InvestmentStatus.ACTIVE);
        rd.setMaturityAction(request.getMaturityAction());
    Boolean rdAuto = request.getAutoDebit();
    rd.setAutoDebit(rdAuto != null ? rdAuto : true);
        
        RecurringDeposit savedRd = recurringDepositRepository.save(rd);
        updatePortfolio(customer);
        
        return savedRd;
    }
    
    @Transactional(readOnly = true)
    public List<RecurringDeposit> getCustomerRecurringDeposits(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return recurringDepositRepository.findByCustomer(customer);
    }
    
    @Transactional
    public RecurringDeposit payRDInstallment(String rdNumber, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        RecurringDeposit rd = recurringDepositRepository.findByRdNumber(rdNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Recurring Deposit not found"));
        
        if (!rd.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("RD does not belong to customer");
        }
        
        if (rd.getStatus() != InvestmentStatus.ACTIVE) {
            throw new IllegalStateException("RD is not active");
        }
        
        Account account = rd.getAccount();
        double installmentDouble = rd.getMonthlyInstallment().doubleValue();
        if (account.getBalance() < installmentDouble) {
            throw new InsufficientFundsException("Insufficient balance for installment");
        }
        
        // Deduct installment
        account.setBalance(account.getBalance() - installmentDouble);
        accountRepository.save(account);
        
        // Update RD
        rd.setInstallmentsPaid(rd.getInstallmentsPaid() + 1);
        rd.setTotalDeposited(rd.getTotalDeposited().add(rd.getMonthlyInstallment()));
        rd.setLastInstallmentDate(LocalDate.now());
        rd.setNextInstallmentDate(rd.getNextInstallmentDate().plusMonths(1));
        
        RecurringDeposit updatedRd = recurringDepositRepository.save(rd);
        updatePortfolio(customer);
        
        return updatedRd;
    }
    
    // Mutual Fund Operations
    
    @Transactional(readOnly = true)
    public List<MutualFund> getAllMutualFunds() {
        return mutualFundRepository.findByIsActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public MutualFund getMutualFundDetails(String fundCode) {
        return mutualFundRepository.findByFundCode(fundCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mutual Fund not found"));
    }
    
    @Transactional
    public MutualFundHolding investInMutualFund(MutualFundInvestmentRequest request, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        MutualFund mutualFund = mutualFundRepository.findByFundCode(request.getFundCode())
                .orElseThrow(() -> new ResourceNotFoundException("Mutual Fund not found"));
        
        if (!account.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Account does not belong to customer");
        }
        
        double amountDouble = request.getAmount().doubleValue();
        if (account.getBalance() < amountDouble) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        // Deduct from account
        account.setBalance(account.getBalance() - amountDouble);
        accountRepository.save(account);
        
        // Calculate units
        BigDecimal units = request.getAmount().divide(mutualFund.getCurrentNav(), 4, RoundingMode.HALF_UP);
        
        // Create new holding
        MutualFundHolding holding = new MutualFundHolding();
        holding.setFolioNumber("MF" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        holding.setCustomer(customer);
        holding.setMutualFund(mutualFund);
        holding.setUnits(units);
        holding.setTotalInvested(request.getAmount());
        holding.setAverageNav(mutualFund.getCurrentNav());
        holding.setStatus(InvestmentStatus.ACTIVE);
        
        // Update current value and gains
        holding.setCurrentValue(holding.getUnits().multiply(mutualFund.getCurrentNav()));
        holding.setTotalGainLoss(holding.getCurrentValue().subtract(holding.getTotalInvested()));
        holding.setReturnPercentage(holding.getTotalGainLoss()
                .divide(holding.getTotalInvested(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue());
        
        MutualFundHolding savedHolding = mutualFundHoldingRepository.save(holding);
        updatePortfolio(customer);
        
        return savedHolding;
    }
    
    @Transactional(readOnly = true)
    public List<MutualFundHolding> getCustomerMutualFundHoldings(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return mutualFundHoldingRepository.findByCustomerAndStatus(customer, InvestmentStatus.ACTIVE);
    }
    
    // SIP Operations
    
    @Transactional
    public SipInvestment createSip(SipRequest request, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        MutualFund mutualFund = mutualFundRepository.findByFundCode(request.getFundCode())
                .orElseThrow(() -> new ResourceNotFoundException("Mutual Fund not found"));
        
        if (!mutualFund.getSipAvailable()) {
            throw new IllegalStateException("SIP not available for this fund");
        }
        
        if (!account.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("Account does not belong to customer");
        }
        
        // Create SIP
        SipInvestment sip = new SipInvestment();
        sip.setSipNumber("SIP" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        sip.setCustomer(customer);
        sip.setAccount(account);
        sip.setMutualFund(mutualFund);
        sip.setInstallmentAmount(request.getInstallmentAmount());
        sip.setFrequency(request.getFrequency());
        sip.setStartDate(request.getStartDate());
        sip.setEndDate(request.getEndDate());
        sip.setTotalInstallments(request.getTotalInstallments());
        sip.setNextInstallmentDate(request.getStartDate());
        sip.setStatus(InvestmentStatus.ACTIVE);
    Boolean sipAuto = request.getAutoDebit();
    sip.setAutoDebit(sipAuto != null ? sipAuto : true);
        
        SipInvestment savedSip = sipInvestmentRepository.save(sip);
        updatePortfolio(customer);
        
        return savedSip;
    }
    
    @Transactional(readOnly = true)
    public List<SipInvestment> getCustomerSips(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return sipInvestmentRepository.findByCustomer(customer);
    }
    
    @Transactional
    public SipInvestment cancelSip(String sipNumber, String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        SipInvestment sip = sipInvestmentRepository.findBySipNumber(sipNumber)
                .orElseThrow(() -> new ResourceNotFoundException("SIP not found"));
        
        if (!sip.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("SIP does not belong to customer");
        }
        
        sip.setStatus(InvestmentStatus.CANCELLED);
        sip.setCancelledAt(LocalDateTime.now());
        
        SipInvestment cancelledSip = sipInvestmentRepository.save(sip);
        updatePortfolio(customer);
        
        return cancelledSip;
    }
    
    // Portfolio Operations
    
    @Transactional(readOnly = true)
    public InvestmentPortfolio getCustomerPortfolio(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        return investmentPortfolioRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    InvestmentPortfolio portfolio = new InvestmentPortfolio();
                    portfolio.setCustomer(customer);
                    portfolio.setTotalInvestmentValue(BigDecimal.ZERO);
                    portfolio.setTotalInvested(BigDecimal.ZERO);
                    portfolio.setTotalGainLoss(BigDecimal.ZERO);
                    portfolio.setOverallReturnPercentage(0.0);
                    return investmentPortfolioRepository.save(portfolio);
                });
    }
    
    // Helper Methods
    
    private void updatePortfolio(Customer customer) {
        InvestmentPortfolio portfolio = investmentPortfolioRepository.findByCustomer(customer)
                .orElseGet(() -> {
                    InvestmentPortfolio newPortfolio = new InvestmentPortfolio();
                    newPortfolio.setCustomer(customer);
                    return newPortfolio;
                });
        
        // Calculate FD values
        List<FixedDeposit> fds = fixedDepositRepository.findByCustomerAndStatus(customer, InvestmentStatus.ACTIVE);
        BigDecimal fdValue = fds.stream()
                .map(FixedDeposit::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate RD values
        List<RecurringDeposit> rds = recurringDepositRepository.findByCustomerAndStatus(customer, InvestmentStatus.ACTIVE);
        BigDecimal rdValue = rds.stream()
                .map(RecurringDeposit::getTotalDeposited)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate Mutual Fund values
        List<MutualFundHolding> mfHoldings = mutualFundHoldingRepository.findByCustomerAndStatus(customer, InvestmentStatus.ACTIVE);
        BigDecimal mfInvested = mfHoldings.stream()
                .map(MutualFundHolding::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal mfValue = mfHoldings.stream()
                .map(holding -> holding.getUnits().multiply(holding.getMutualFund().getCurrentNav()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate SIP values
        List<SipInvestment> sips = sipInvestmentRepository.findByCustomerAndStatus(customer, InvestmentStatus.ACTIVE);
        BigDecimal sipValue = sips.stream()
                .map(SipInvestment::getTotalInvested)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Update portfolio
        portfolio.setFdValue(fdValue);
        portfolio.setRdValue(rdValue);
        portfolio.setMutualFundValue(mfValue);
        portfolio.setSipValue(sipValue);
        portfolio.setTotalFixedDeposits(fds.size());
        portfolio.setTotalRecurringDeposits(rds.size());
        portfolio.setTotalMutualFunds(mfHoldings.size());
        portfolio.setTotalActiveSips((int) sips.stream().filter(s -> s.getStatus() == InvestmentStatus.ACTIVE).count());
        
        BigDecimal totalInvested = fdValue.add(rdValue).add(mfInvested).add(sipValue);
        BigDecimal totalValue = fdValue.add(rdValue).add(mfValue).add(sipValue);
        BigDecimal totalGainLoss = totalValue.subtract(totalInvested);
        
        portfolio.setTotalInvested(totalInvested);
        portfolio.setTotalInvestmentValue(totalValue);
        portfolio.setTotalGainLoss(totalGainLoss);
        
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            portfolio.setOverallReturnPercentage(
                    totalGainLoss.divide(totalInvested, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)).doubleValue()
            );
        }
        
        investmentPortfolioRepository.save(portfolio);
    }
    
    private Double calculateFDInterestRate(Integer tenureMonths) {
        if (tenureMonths <= 6) return 5.5;
        if (tenureMonths <= 12) return 6.0;
        if (tenureMonths <= 24) return 6.5;
        if (tenureMonths <= 36) return 7.0;
        return 7.5;
    }
    
    private Double calculateRDInterestRate(Integer tenureMonths) {
        return calculateFDInterestRate(tenureMonths);
    }
    
    private BigDecimal calculateMaturityAmount(BigDecimal principal, Double rate, Integer months) {
        double r = rate / 100.0 / 4.0; // Quarterly compounding
        int n = (months / 3); // Number of quarters
        double amount = principal.doubleValue() * Math.pow(1 + r, n);
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateRDMaturityAmount(BigDecimal monthlyInstallment, Double rate, Integer months) {
        double r = rate / 100.0 / 12.0; // Monthly compounding
        double maturity = 0.0;
        
        for (int i = 1; i <= months; i++) {
            double monthsRemaining = months - i + 1;
            maturity += monthlyInstallment.doubleValue() * Math.pow(1 + r, monthsRemaining / 12.0);
        }
        
        return BigDecimal.valueOf(maturity).setScale(2, RoundingMode.HALF_UP);
    }
}
