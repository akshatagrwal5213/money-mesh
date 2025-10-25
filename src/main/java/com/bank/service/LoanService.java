package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanRepaymentRepository loanRepaymentRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private AuditService auditService;

    private static final SecureRandom random = new SecureRandom();

    /**
     * Apply for a new loan
     */
    @Transactional
    public LoanDetailsResponse applyForLoan(LoanApplicationRequest request, String username) {
        // Get account and verify ownership
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        verifyAccountOwnership(account, username);

        // Create loan
        Loan loan = new Loan();
        loan.setLoanNumber(generateLoanNumber());
        loan.setCustomer(account.getCustomer());
        loan.setAccount(account);
        loan.setLoanType(request.getLoanType());
        loan.setStatus(LoanStatus.PENDING);
        loan.setPrincipalAmount(request.getPrincipalAmount());
        loan.setTenureMonths(request.getTenureMonths());
        loan.setRepaymentFrequency(request.getRepaymentFrequency());
        loan.setPurpose(request.getPurpose());
        loan.setCollateral(request.getCollateral());
        
        // Calculate interest rate based on loan type
        Double interestRate = getInterestRateForLoanType(request.getLoanType());
        loan.setInterestRate(interestRate);
        
        // Calculate EMI
        Double emiAmount = calculateEMI(
            request.getPrincipalAmount(),
            interestRate,
            request.getTenureMonths(),
            request.getRepaymentFrequency()
        );
        loan.setEmiAmount(emiAmount);
        
        // Set outstanding amount to principal initially
        loan.setOutstandingAmount(request.getPrincipalAmount());
        loan.setTotalAmountPaid(0.0);

        loan = loanRepository.save(loan);

        // Audit log
        auditService.logAction(username, "LOAN_APPLICATION", 
            "Applied for " + request.getLoanType() + " loan of ₹" + request.getPrincipalAmount());

        return mapToDetailsResponse(loan);
    }

    /**
     * Approve a loan (Admin function)
     */
    @Transactional
    public LoanDetailsResponse approveLoan(Long loanId, String remarks, String username) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING && loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Loan is not in a state that can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDate.now());
        loan.setRemarks(remarks);

        loanRepository.save(loan);

        // Audit log
        auditService.logAction(username, "LOAN_APPROVED", 
            "Approved loan " + loan.getLoanNumber());

        return mapToDetailsResponse(loan);
    }

    /**
     * Reject a loan (Admin function)
     */
    @Transactional
    public LoanDetailsResponse rejectLoan(Long loanId, String reason, String username) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING && loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Loan is not in a state that can be rejected");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionReason(reason);

        loanRepository.save(loan);

        // Audit log
        auditService.logAction(username, "LOAN_REJECTED", 
            "Rejected loan " + loan.getLoanNumber() + ". Reason: " + reason);

        return mapToDetailsResponse(loan);
    }

    /**
     * Disburse loan amount (Admin function)
     */
    @Transactional
    public LoanDetailsResponse disburseLoan(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException("Loan must be approved before disbursement");
        }

        // Credit loan amount to account
        Account account = loan.getAccount();
        account.setBalance(account.getBalance() + loan.getPrincipalAmount());
        accountRepository.save(account);

        // Update loan status
        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        
        // Set next payment due date based on repayment frequency
        loan.setNextPaymentDue(calculateNextPaymentDate(LocalDate.now(), loan.getRepaymentFrequency()));
        
        // Calculate maturity date
        loan.setMaturityDate(LocalDate.now().plusMonths(loan.getTenureMonths()));

        loanRepository.save(loan);

        // Audit log
        auditService.logAction(username, "LOAN_DISBURSED", 
            "Disbursed loan " + loan.getLoanNumber() + " amount ₹" + loan.getPrincipalAmount());

        return mapToDetailsResponse(loan);
    }

    /**
     * Make loan repayment
     */
    @Transactional
    public LoanDetailsResponse makeRepayment(LoanRepaymentRequest request, String username) {
        Loan loan = loanRepository.findById(request.getLoanId())
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        verifyLoanOwnership(loan, username);

        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not active for repayment");
        }

        // Calculate principal and interest portions
        Double monthlyInterestRate = loan.getInterestRate() / 12 / 100;
        Double interestPortion = loan.getOutstandingAmount() * monthlyInterestRate;
        Double principalPortion = request.getAmount() - interestPortion;

        // Create repayment record
        LoanRepayment repayment = new LoanRepayment();
        repayment.setLoan(loan);
        repayment.setAmount(request.getAmount());
        repayment.setPrincipalPaid(principalPortion);
        repayment.setInterestPaid(interestPortion);
        repayment.setPaymentDate(LocalDate.now());
        repayment.setDueDate(loan.getNextPaymentDue());
        repayment.setPaymentMethod(request.getPaymentMethod() != null ? 
            request.getPaymentMethod() : "ONLINE");
        repayment.setTransactionId("TXN" + System.currentTimeMillis());
        repayment.setReceiptNumber("RCP" + System.currentTimeMillis());
        
        // Check if payment is late
        if (loan.getNextPaymentDue() != null && LocalDate.now().isAfter(loan.getNextPaymentDue())) {
            repayment.setIsLate(true);
            repayment.setLateFee(100.0);  // Fixed late fee
        } else {
            repayment.setIsLate(false);
            repayment.setLateFee(0.0);
        }

        loanRepaymentRepository.save(repayment);

        // Update loan
        loan.setOutstandingAmount(loan.getOutstandingAmount() - principalPortion);
    Double paidSoFar = loan.getTotalAmountPaid();
    double paid = paidSoFar != null ? paidSoFar : 0.0;
    loan.setTotalAmountPaid(paid + request.getAmount());
        
        // Update status
        if (loan.getStatus() == LoanStatus.DISBURSED) {
            loan.setStatus(LoanStatus.ACTIVE);
        }

        // Calculate next payment due date
        loan.setNextPaymentDue(calculateNextPaymentDate(LocalDate.now(), loan.getRepaymentFrequency()));

        // Check if loan is fully paid
        if (loan.getOutstandingAmount() <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
            loan.setNextPaymentDue(null);
        }

        loanRepository.save(loan);

        // Audit log
        auditService.logAction(username, "LOAN_REPAYMENT", 
            "Paid ₹" + request.getAmount() + " for loan " + loan.getLoanNumber());

        return mapToDetailsResponse(loan);
    }

    /**
     * Calculate EMI
     */
    public LoanEmiCalculationResponse calculateEmiDetails(LoanEmiCalculationRequest request) {
        Double interestRate = getInterestRateForLoanType(request.getLoanType());
        
        Double emiAmount = calculateEMI(
            request.getPrincipalAmount(),
            interestRate,
            request.getTenureMonths(),
            request.getRepaymentFrequency()
        );

        int numberOfPayments = calculateNumberOfPayments(
            request.getTenureMonths(), 
            request.getRepaymentFrequency()
        );

        Double totalAmount = emiAmount * numberOfPayments;
        Double totalInterest = totalAmount - request.getPrincipalAmount();

        LoanEmiCalculationResponse response = new LoanEmiCalculationResponse();
        response.setEmiAmount(emiAmount);
        response.setTotalAmount(totalAmount);
        response.setTotalInterest(totalInterest);
        response.setInterestRate(interestRate);
        response.setNumberOfPayments(numberOfPayments);

        return response;
    }

    /**
     * Get loan details
     */
    public LoanDetailsResponse getLoanDetails(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        verifyLoanOwnership(loan, username);

        return mapToDetailsResponse(loan);
    }

    /**
     * Get all loans for user
     */
    public List<LoanDetailsResponse> getUserLoans(String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = user.getCustomer();
        if (customer == null) {
            return List.of();
        }

        List<Loan> loans = loanRepository.findByCustomer(customer);
        return loans.stream()
            .map(this::mapToDetailsResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get loan repayment history
     */
    public List<LoanRepayment> getLoanRepaymentHistory(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        verifyLoanOwnership(loan, username);

        return loanRepaymentRepository.findByLoanOrderByPaymentDateDesc(loan);
    }

    /**
     * Calculate EMI amount
     */
    private Double calculateEMI(Double principal, Double annualRate, Integer tenureMonths, 
                                RepaymentFrequency frequency) {
        int paymentsPerYear = getPaymentsPerYear(frequency);
        int numberOfPayments = calculateNumberOfPayments(tenureMonths, frequency);
        
        Double ratePerPeriod = (annualRate / 100) / paymentsPerYear;
        
        if (ratePerPeriod == 0) {
            return principal / numberOfPayments;
        }
        
        // EMI = P * r * (1+r)^n / ((1+r)^n - 1)
        Double emi = principal * ratePerPeriod * Math.pow(1 + ratePerPeriod, numberOfPayments) / 
                    (Math.pow(1 + ratePerPeriod, numberOfPayments) - 1);
        
        return Math.round(emi * 100.0) / 100.0;
    }

    /**
     * Calculate number of payments based on frequency
     */
    private int calculateNumberOfPayments(Integer tenureMonths, RepaymentFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> tenureMonths;
            case QUARTERLY -> (int) Math.ceil(tenureMonths / 3.0);
            case HALF_YEARLY -> (int) Math.ceil(tenureMonths / 6.0);
            case YEARLY -> (int) Math.ceil(tenureMonths / 12.0);
            default -> tenureMonths;
        };
    }

    /**
     * Get payments per year for frequency
     */
    private int getPaymentsPerYear(RepaymentFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> 12;
            case QUARTERLY -> 4;
            case HALF_YEARLY -> 2;
            case YEARLY -> 1;
            default -> 12;
        };
    }

    /**
     * Calculate next payment date
     */
    private LocalDate calculateNextPaymentDate(LocalDate fromDate, RepaymentFrequency frequency) {
        return switch (frequency) {
            case MONTHLY -> fromDate.plusMonths(1);
            case QUARTERLY -> fromDate.plusMonths(3);
            case HALF_YEARLY -> fromDate.plusMonths(6);
            case YEARLY -> fromDate.plusYears(1);
            default -> fromDate.plusMonths(1);
        };
    }

    /**
     * Get interest rate based on loan type
     */
    private Double getInterestRateForLoanType(LoanType loanType) {
        return switch (loanType) {
            case PERSONAL -> 10.5;
            case HOME -> 7.5;
            case CAR -> 9.0;
            case EDUCATION -> 8.5;
            case BUSINESS -> 11.0;
            case GOLD -> 8.0;
            case AGRICULTURE -> 7.0;
            default -> 10.0;
        };
    }

    /**
     * Generate unique loan number
     */
    private String generateLoanNumber() {
        String prefix = "LN";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = String.format("%04d", random.nextInt(10000));
        return prefix + timestamp.substring(timestamp.length() - 8) + randomPart;
    }

    /**
     * Verify loan ownership
     */
    private void verifyLoanOwnership(Loan loan, String username) {
        verifyAccountOwnership(loan.getAccount(), username);
    }

    /**
     * Verify account ownership
     */
    private void verifyAccountOwnership(Account account, String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (account.getCustomer() == null || 
            !account.getCustomer().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to account");
        }
    }

    /**
     * Map Loan to LoanDetailsResponse
     */
    private LoanDetailsResponse mapToDetailsResponse(Loan loan) {
        LoanDetailsResponse response = new LoanDetailsResponse();
        response.setId(loan.getId());
        response.setLoanNumber(loan.getLoanNumber());
        response.setLoanType(loan.getLoanType());
        response.setStatus(loan.getStatus());
        response.setPrincipalAmount(loan.getPrincipalAmount());
        response.setInterestRate(loan.getInterestRate());
        response.setTenureMonths(loan.getTenureMonths());
        response.setRepaymentFrequency(loan.getRepaymentFrequency());
        response.setEmiAmount(loan.getEmiAmount());
        response.setOutstandingAmount(loan.getOutstandingAmount());
        response.setTotalAmountPaid(loan.getTotalAmountPaid());
        
        // Calculate total interest
        Double totalInterest = (loan.getEmiAmount() * 
            calculateNumberOfPayments(loan.getTenureMonths(), loan.getRepaymentFrequency())) - 
            loan.getPrincipalAmount();
        response.setTotalInterest(totalInterest);
        
        response.setApplicationDate(loan.getApplicationDate());
        response.setApprovalDate(loan.getApprovalDate());
        response.setDisbursementDate(loan.getDisbursementDate());
        response.setNextPaymentDue(loan.getNextPaymentDue());
        response.setMaturityDate(loan.getMaturityDate());
        response.setPurpose(loan.getPurpose());
        response.setCollateral(loan.getCollateral());
        response.setRemarks(loan.getRemarks());
        response.setRejectionReason(loan.getRejectionReason());

        // Customer info
        if (loan.getCustomer() != null) {
            LoanDetailsResponse.CustomerInfo customerInfo = new LoanDetailsResponse.CustomerInfo();
            customerInfo.setId(loan.getCustomer().getId());
            customerInfo.setName(loan.getCustomer().getName());
            customerInfo.setEmail(loan.getCustomer().getEmail());
            customerInfo.setPhone(loan.getCustomer().getPhone());
            response.setCustomer(customerInfo);
        }

        // Account info
        if (loan.getAccount() != null) {
            response.setAccountId(loan.getAccount().getId());
            response.setAccountNumber(loan.getAccount().getAccountNumber());
        }

        return response;
    }
}
