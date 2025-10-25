package com.bank.service;

import com.bank.dto.*;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.*;
import com.bank.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CreditScoreService {

    @Autowired
    private CreditScoreRepository creditScoreRepository;

    @Autowired
    private CreditInquiryRepository creditInquiryRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;
    @Autowired
    private CreditDisputeRepository creditDisputeRepository;

    @Autowired
    private LoanEligibilityRepository loanEligibilityRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WealthProfileRepository wealthProfileRepository;

    @Autowired
    private FixedDepositRepository fixedDepositRepository;

    @Autowired
    private RecurringDepositRepository recurringDepositRepository;

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    /**
     * Calculate credit score for a customer using 5-factor algorithm
     */
    public CreditScoreResponse calculateCreditScore(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Get previous score if exists
        Optional<CreditScore> previousScoreOpt = creditScoreRepository.findLatestByCustomerId(customerId);
        Integer previousScore = previousScoreOpt.map(CreditScore::getScore).orElse(null);

        // Calculate each factor
        int paymentHistoryScore = calculatePaymentHistoryScore(customerId);
        int creditUtilizationScore = calculateCreditUtilizationScore(customerId);
        int creditHistoryLengthScore = calculateCreditHistoryLengthScore(customerId);
        int creditMixScore = calculateCreditMixScore(customerId);
        int recentInquiriesScore = calculateRecentInquiriesScore(customerId);

        // Total score (300-900 range)
        int totalScore = paymentHistoryScore + creditUtilizationScore + 
                        creditHistoryLengthScore + creditMixScore + recentInquiriesScore;

        // Ensure score is within valid range
        totalScore = Math.max(300, Math.min(900, totalScore));

        // Determine category
        CreditScoreCategory category = determineCategory(totalScore);

        // Get metrics
        double onTimePaymentPercentage = calculateOnTimePaymentPercentage(customerId);
        double creditUtilizationRatio = calculateCreditUtilizationRatio(customerId);
        int oldestAccountAgeMonths = getOldestAccountAgeMonths(customerId);
        int numberOfActiveAccounts = countActiveAccounts(customerId);
        long hardInquiriesLast6Months = countHardInquiries(customerId, 6);

        // Create and save credit score
        CreditScore creditScore = new CreditScore();
        creditScore.setCustomer(customer);
        creditScore.setScore(totalScore);
        creditScore.setCategory(category);
        creditScore.setCalculationDate(LocalDate.now());
        
        creditScore.setPaymentHistoryScore(paymentHistoryScore);
        creditScore.setCreditUtilizationScore(creditUtilizationScore);
        creditScore.setCreditHistoryLengthScore(creditHistoryLengthScore);
        creditScore.setCreditMixScore(creditMixScore);
        creditScore.setRecentInquiriesScore(recentInquiriesScore);
        
        creditScore.setOnTimePaymentPercentage(onTimePaymentPercentage);
        creditScore.setCreditUtilizationRatio(creditUtilizationRatio);
        creditScore.setOldestAccountAgeMonths(oldestAccountAgeMonths);
        creditScore.setNumberOfActiveAccounts(numberOfActiveAccounts);
        creditScore.setHardInquiriesLast6Months((int) hardInquiriesLast6Months);
        
        if (previousScore != null) {
            creditScore.setPreviousScore(previousScore);
            creditScore.setScoreChange(totalScore - previousScore);
        }
        
        // Generate improvement suggestions
        List<String> suggestions = generateImprovementSuggestions(creditScore);
        creditScore.setImprovementSuggestions(String.join("; ", suggestions));

        creditScore = creditScoreRepository.save(creditScore);

        // Build response
        CreditScoreResponse response = buildCreditScoreResponse(creditScore);
        response.setImprovementSuggestions(suggestions);
        response.setFactorImpact(calculateFactorImpact());
        
        // Determine trend
        if (creditScore.getScoreChange() != null) {
            if (creditScore.getScoreChange() > 10) {
                response.setTrend("IMPROVING");
            } else if (creditScore.getScoreChange() < -10) {
                response.setTrend("DECLINING");
            } else {
                response.setTrend("STABLE");
            }
        } else {
            response.setTrend("NEW");
        }

        return response;
    }

    /**
     * Calculate payment history score (35% = 315 points max)
     */
    private int calculatePaymentHistoryScore(Long customerId) {
        LocalDate twoYearsAgo = LocalDate.now().minusMonths(24);
        List<PaymentHistory> payments = paymentHistoryRepository.findRecentPayments(customerId, twoYearsAgo);

        if (payments.isEmpty()) {
            return 250; // Default score if no payment history
        }

        long onTimePayments = payments.stream()
            .filter(p -> "ON_TIME".equals(p.getStatus()))
            .count();

        double onTimePercentage = (double) onTimePayments / payments.size() * 100;
        
        // Base score from on-time percentage
        int score = (int) (315 * (onTimePercentage / 100));

        // Deduct for late payments
        long latePayments = payments.stream()
            .filter(p -> "LATE".equals(p.getStatus()) && p.getDaysLate() != null)
            .count();

        long missedPayments = payments.stream()
            .filter(p -> "MISSED".equals(p.getStatus()))
            .count();

        // Penalties
        score -= (int) (latePayments * 15);  // -15 points per late payment
        score -= (int) (missedPayments * 30); // -30 points per missed payment

        return Math.max(0, Math.min(315, score));
    }

    /**
     * Calculate credit utilization score (30% = 270 points max)
     */
    private int calculateCreditUtilizationScore(Long customerId) {
        double utilizationRatio = calculateCreditUtilizationRatio(customerId);

        if (utilizationRatio < 0) {
            return 270; // No credit used
        }

        // Optimal ratio is < 30%
        // Score decreases as ratio increases
        int score;
        if (utilizationRatio <= 0.30) {
            score = 270; // Full score for < 30% utilization
        } else if (utilizationRatio <= 0.50) {
            score = (int) (270 * (1 - (utilizationRatio - 0.30) / 0.20 * 0.3));
        } else if (utilizationRatio <= 0.70) {
            score = (int) (270 * (1 - (utilizationRatio - 0.30) / 0.40 * 0.5));
        } else {
            score = (int) (270 * (1 - Math.min(utilizationRatio, 1.0)));
        }

        return Math.max(0, Math.min(270, score));
    }

    /**
     * Calculate credit history length score (15% = 135 points max)
     */
    private int calculateCreditHistoryLengthScore(Long customerId) {
        int ageInMonths = getOldestAccountAgeMonths(customerId);

        if (ageInMonths == 0) {
            return 50; // Minimal score for new customers
        }

        // Max score at 120 months (10 years)
        int score = (int) (135 * Math.min(ageInMonths / 120.0, 1.0));

        return Math.max(0, Math.min(135, score));
    }

    /**
     * Calculate credit mix score (10% = 90 points max)
     */
    private int calculateCreditMixScore(Long customerId) {
        int distinctTypes = countActiveAccounts(customerId);

        // 5+ types = max score
        int score = (int) (90 * Math.min(distinctTypes / 5.0, 1.0));

        return Math.max(0, Math.min(90, score));
    }

    /**
     * Calculate recent inquiries score (10% = 90 points max)
     */
    private int calculateRecentInquiriesScore(Long customerId) {
        long hardInquiries = countHardInquiries(customerId, 6);

        // 0 inquiries = 90 points, 5+ inquiries = 0 points
        int score = (int) (90 * Math.max(1 - (hardInquiries / 5.0), 0));

        return Math.max(0, Math.min(90, score));
    }

    /**
     * Calculate on-time payment percentage
     */
    private double calculateOnTimePaymentPercentage(Long customerId) {
        LocalDate twoYearsAgo = LocalDate.now().minusMonths(24);
        Long totalPayments = paymentHistoryRepository.countTotalPayments(customerId, twoYearsAgo);
        
        if (totalPayments == 0) {
            return 100.0;
        }

        Long onTimePayments = paymentHistoryRepository.countOnTimePayments(customerId, twoYearsAgo);
        return (double) onTimePayments / totalPayments * 100;
    }

    /**
     * Calculate credit utilization ratio
     */
    private double calculateCreditUtilizationRatio(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return -1;

        // Get total outstanding from loans
        List<Loan> loans = loanRepository.findByCustomer(customer);
        double totalOutstanding = loans.stream()
            .filter(l -> l.getOutstandingAmount() != null)
            .mapToDouble(Loan::getOutstandingAmount)
            .sum();

        // Get total principal (available credit)
        double totalCredit = loans.stream()
            .filter(l -> l.getPrincipalAmount() != null)
            .mapToDouble(Loan::getPrincipalAmount)
            .sum();

        if (totalCredit == 0) {
            return -1; // No credit history
        }

        return totalOutstanding / totalCredit;
    }

    /**
     * Get oldest account age in months
     */
    private int getOldestAccountAgeMonths(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0;

        LocalDate oldestDate = LocalDate.now();
        boolean hasAccounts = false;

        // Check loans
        List<Loan> loans = loanRepository.findByCustomer(customer);
        if (!loans.isEmpty()) {
            hasAccounts = true;
            for (Loan loan : loans) {
                if (loan.getApplicationDate() != null && loan.getApplicationDate().isBefore(oldestDate)) {
                    oldestDate = loan.getApplicationDate();
                }
            }
        }

        // Check accounts (savings)
        List<Account> accounts = accountRepository.findByCustomer(customer);
        if (!accounts.isEmpty()) {
            hasAccounts = true;
            // Assume first account is oldest (simplified)
            oldestDate = oldestDate.minusYears(2); // Simplified assumption
        }

        if (!hasAccounts) {
            return 0;
        }

        return (int) ChronoUnit.MONTHS.between(oldestDate, LocalDate.now());
    }

    /**
     * Count active accounts of different types
     */
    private int countActiveAccounts(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0;

        int types = 0;

        // Check accounts
        List<Account> accounts = accountRepository.findByCustomer(customer);
        if (!accounts.isEmpty()) types++;

        // Check loans
        if (!loanRepository.findByCustomer(customer).isEmpty()) types++;

        // Check fixed deposits
        if (!fixedDepositRepository.findByCustomer(customer).isEmpty()) types++;

        // Check recurring deposits
        if (!recurringDepositRepository.findByCustomer(customer).isEmpty()) types++;

        // Check cards (via accounts)
        for (Account account : accounts) {
            if (!cardRepository.findByAccount(account).isEmpty()) {
                types++;
                break;
            }
        }

        // Check insurance
        if (!insurancePolicyRepository.findByCustomerId(customerId).isEmpty()) types++;

        return types;
    }

    /**
     * Count hard inquiries in last X months
     */
    private long countHardInquiries(Long customerId, int months) {
        LocalDate sinceDate = LocalDate.now().minusMonths(months);
        return creditInquiryRepository.countHardInquiriesSince(customerId, sinceDate);
    }

    /**
     * Determine credit score category
     */
    private CreditScoreCategory determineCategory(int score) {
        if (score >= 800) return CreditScoreCategory.EXCELLENT;
        if (score >= 750) return CreditScoreCategory.VERY_GOOD;
        if (score >= 650) return CreditScoreCategory.GOOD;
        if (score >= 550) return CreditScoreCategory.FAIR;
        return CreditScoreCategory.POOR;
    }

    /**
     * Generate improvement suggestions
     */
    private List<String> generateImprovementSuggestions(CreditScore creditScore) {
        List<String> suggestions = new ArrayList<>();

        // Payment history suggestions
        if (creditScore.getOnTimePaymentPercentage() < 95) {
            suggestions.add("Pay all bills on time to improve payment history (35% impact)");
        }

        // Credit utilization suggestions
        if (creditScore.getCreditUtilizationRatio() > 0.30) {
            suggestions.add("Reduce credit utilization below 30% to boost score (30% impact)");
        }

        // Credit history length suggestions
        if (creditScore.getOldestAccountAgeMonths() < 60) {
            suggestions.add("Keep oldest accounts active to build credit history (15% impact)");
        }

        // Credit mix suggestions
        if (creditScore.getNumberOfActiveAccounts() < 3) {
            suggestions.add("Diversify credit types (accounts, loans, cards) for better credit mix (10% impact)");
        }

        // Recent inquiries suggestions
        if (creditScore.getHardInquiriesLast6Months() > 2) {
            suggestions.add("Limit new credit applications to reduce hard inquiries (10% impact)");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Your credit profile is strong! Maintain current practices.");
        }

        return suggestions;
    }

    /**
     * Calculate factor impact percentages
     */
    private Map<String, Double> calculateFactorImpact() {
        Map<String, Double> impact = new HashMap<>();
        impact.put("Payment History", 35.0);
        impact.put("Credit Utilization", 30.0);
        impact.put("Credit History Length", 15.0);
        impact.put("Credit Mix", 10.0);
        impact.put("Recent Inquiries", 10.0);
        return impact;
    }

    /**
     * Build credit score response DTO
     */
    private CreditScoreResponse buildCreditScoreResponse(CreditScore creditScore) {
        CreditScoreResponse response = new CreditScoreResponse();
        response.setId(creditScore.getId());
        response.setScore(creditScore.getScore());
        response.setCategory(creditScore.getCategory());
        response.setCalculationDate(creditScore.getCalculationDate());
        
        response.setPaymentHistoryScore(creditScore.getPaymentHistoryScore());
        response.setCreditUtilizationScore(creditScore.getCreditUtilizationScore());
        response.setCreditHistoryLengthScore(creditScore.getCreditHistoryLengthScore());
        response.setCreditMixScore(creditScore.getCreditMixScore());
        response.setRecentInquiriesScore(creditScore.getRecentInquiriesScore());
        
        response.setOnTimePaymentPercentage(creditScore.getOnTimePaymentPercentage());
        response.setCreditUtilizationRatio(creditScore.getCreditUtilizationRatio());
        response.setOldestAccountAgeMonths(creditScore.getOldestAccountAgeMonths());
        response.setNumberOfActiveAccounts(creditScore.getNumberOfActiveAccounts());
        response.setHardInquiriesLast6Months(creditScore.getHardInquiriesLast6Months());
        
        response.setPreviousScore(creditScore.getPreviousScore());
        response.setScoreChange(creditScore.getScoreChange());
        
        return response;
    }

    /**
     * Get credit score history
     */
    public List<CreditScoreResponse> getCreditScoreHistory(Long customerId, Integer months) {
        LocalDate sinceDate = LocalDate.now().minusMonths(months != null ? months : 6);
        List<CreditScore> scores = creditScoreRepository.getScoreHistory(customerId, sinceDate);
        
        return scores.stream()
            .map(this::buildCreditScoreResponse)
            .collect(Collectors.toList());
    }

    /**
     * Check loan eligibility
     */
    public LoanEligibilityResponse checkLoanEligibility(LoanEligibilityRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        // Get latest credit score
        CreditScore creditScore = creditScoreRepository.findLatestByCustomerId(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Credit score not found. Please calculate credit score first."));

        // Get wealth profile for income
        WealthProfile wealthProfile = wealthProfileRepository.findByCustomerId(request.getCustomerId())
            .orElse(null);

        Double monthlyIncome = request.getMonthlyIncome();
        if (monthlyIncome == null && wealthProfile != null) {
            monthlyIncome = wealthProfile.getMonthlyIncome();
        }

        if (monthlyIncome == null || monthlyIncome <= 0) {
            throw new IllegalArgumentException("Monthly income is required for eligibility check");
        }

        // Determine eligibility based on loan type and credit score
        LoanEligibilityResponse response = new LoanEligibilityResponse();
        response.setLoanType(request.getLoanType());
        response.setRequestedAmount(request.getRequestedAmount());
        response.setCreditScoreAtCheck(creditScore.getScore());
        response.setCheckDate(LocalDate.now());
        response.setExpiryDate(LocalDate.now().plusDays(30));

        // Calculate debt-to-income ratio
        List<Loan> existingLoans = loanRepository.findByCustomer(customer);
        double totalMonthlyEmi = existingLoans.stream()
            .filter(l -> l.getEmiAmount() != null)
            .mapToDouble(Loan::getEmiAmount)
            .sum();

        double debtToIncomeRatio = totalMonthlyEmi / monthlyIncome;
        response.setDebtToIncomeRatio(debtToIncomeRatio);

        // Minimum score requirements by loan type
        int minRequiredScore = getMinimumScoreRequirement(request.getLoanType());
        
        // Calculate max eligible amount based on loan type
        double maxAmount = calculateMaxLoanAmount(request.getLoanType(), monthlyIncome, debtToIncomeRatio);
        response.setMaxEligibleAmount(maxAmount);

        // Determine eligibility
        List<String> reasons = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        boolean eligible = true;
        EligibilityStatus status = EligibilityStatus.ELIGIBLE;

        // Check credit score requirement
        if (creditScore.getScore() < minRequiredScore) {
            eligible = false;
            status = EligibilityStatus.NOT_ELIGIBLE;
            reasons.add("Credit score (" + creditScore.getScore() + ") below minimum requirement (" + minRequiredScore + ")");
            suggestions.add("Improve credit score to at least " + minRequiredScore);
        } else {
            reasons.add("Credit score meets minimum requirement");
        }

        // Check debt-to-income ratio
        if (debtToIncomeRatio > 0.40) {
            if (eligible) {
                status = EligibilityStatus.CONDITIONALLY_ELIGIBLE;
                conditions.add("High debt-to-income ratio (" + String.format("%.1f%%", debtToIncomeRatio * 100) + "). May require co-borrower.");
            } else {
                reasons.add("Debt-to-income ratio too high (" + String.format("%.1f%%", debtToIncomeRatio * 100) + ")");
                suggestions.add("Reduce existing debt or increase income");
            }
        }

        // Check requested amount vs max eligible
        if (request.getRequestedAmount() > maxAmount) {
            if (eligible) {
                status = EligibilityStatus.CONDITIONALLY_ELIGIBLE;
                conditions.add("Requested amount exceeds maximum eligible amount");
                suggestions.add("Consider requesting up to ₹" + String.format("%.2f", maxAmount));
            } else {
                reasons.add("Requested amount (₹" + request.getRequestedAmount() + ") exceeds maximum (₹" + maxAmount + ")");
            }
        }

        response.setEligible(eligible && status != EligibilityStatus.NOT_ELIGIBLE);
        response.setEligibilityStatus(status);
        response.setEligibilityReasons(reasons);
        response.setConditions(conditions);
        response.setSuggestions(suggestions);

        // Calculate interest rate based on credit score
        double interestRate = calculateInterestRate(request.getLoanType(), creditScore.getScore());
        response.setInterestRate(interestRate);

        // Calculate tenure
        int maxTenure = getMaxTenure(request.getLoanType());
        response.setMaxTenureMonths(maxTenure);
        response.setRecommendedTenureMonths(request.getRequestedTenureMonths() != null ? 
            Math.min(request.getRequestedTenureMonths(), maxTenure) : maxTenure);

        // Calculate EMI
        int tenureMonths = response.getRecommendedTenureMonths();
        double emi = calculateEMI(request.getRequestedAmount(), interestRate, tenureMonths);
        response.setMonthlyEmiEstimate(emi);
        response.setTotalAmountPayable(emi * tenureMonths);
        response.setTotalInterestPayable(response.getTotalAmountPayable() - request.getRequestedAmount());

        // Save eligibility record
        LoanEligibility eligibility = new LoanEligibility();
        eligibility.setCustomer(customer);
        eligibility.setLoanType(request.getLoanType());
        eligibility.setEligibilityStatus(status);
        eligibility.setEligible(response.getEligible());
        eligibility.setMaxEligibleAmount(maxAmount);
        eligibility.setInterestRate(interestRate);
        eligibility.setMaxTenureMonths(maxTenure);
        eligibility.setCreditScoreAtCheck(creditScore.getScore());
        eligibility.setEligibilityReasons(String.join("; ", reasons));
        eligibility.setConditions(conditions.isEmpty() ? null : String.join("; ", conditions));

        loanEligibilityRepository.save(eligibility);

        return response;
    }

    /**
     * Get minimum credit score requirement by loan type
     */
    private int getMinimumScoreRequirement(LoanType loanType) {
        return switch (loanType) {
            case HOME -> 750;
            case PERSONAL, BUSINESS -> 700;
            case CAR -> 650;
            case EDUCATION -> 600;
            case GOLD, AGRICULTURE -> 550;
        };
    }

    /**
     * Calculate maximum loan amount based on income and loan type
     */
    private double calculateMaxLoanAmount(LoanType loanType, double monthlyIncome, double debtToIncomeRatio) {
        // Reduce available income by existing EMIs
        double availableIncomeRatio = Math.max(0.40 - debtToIncomeRatio, 0.10);
        double availableMonthlyEmi = monthlyIncome * availableIncomeRatio;

        return switch (loanType) {
            case HOME -> availableMonthlyEmi * 240; // 20 years
            case PERSONAL -> availableMonthlyEmi * 48; // 4 years
            case CAR -> availableMonthlyEmi * 72; // 6 years
            case EDUCATION -> availableMonthlyEmi * 120; // 10 years
            case BUSINESS -> availableMonthlyEmi * 84; // 7 years
            case GOLD -> monthlyIncome * 12; // 1 year income
            case AGRICULTURE -> monthlyIncome * 24; // 2 years income
        };
    }

    /**
     * Calculate interest rate based on credit score
     */
    private double calculateInterestRate(LoanType loanType, int creditScore) {
        double baseRate = switch (loanType) {
            case HOME -> 8.5;
            case CAR -> 9.0;
            case PERSONAL -> 11.0;
            case EDUCATION -> 10.0;
            case BUSINESS -> 12.0;
            case GOLD -> 7.5;
            case AGRICULTURE -> 7.0;
        };

        // Adjust based on credit score
        if (creditScore >= 800) return baseRate;
        if (creditScore >= 750) return baseRate + 0.5;
        if (creditScore >= 650) return baseRate + 1.5;
        if (creditScore >= 550) return baseRate + 3.0;
        return baseRate + 5.0;
    }

    /**
     * Get maximum tenure by loan type
     */
    private int getMaxTenure(LoanType loanType) {
        return switch (loanType) {
            case HOME -> 240; // 20 years
            case CAR -> 72; // 6 years
            case PERSONAL -> 60; // 5 years
            case EDUCATION -> 120; // 10 years
            case BUSINESS -> 84; // 7 years
            case GOLD -> 36; // 3 years
            case AGRICULTURE -> 60; // 5 years
        };
    }

    /**
     * Calculate EMI using formula: P × r × (1 + r)^n / ((1 + r)^n - 1)
     */
    private double calculateEMI(double principal, double annualRate, int tenureMonths) {
        double monthlyRate = annualRate / 12 / 100;
        double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths) / 
                    (Math.pow(1 + monthlyRate, tenureMonths) - 1);
        return Math.round(emi * 100.0) / 100.0;
    }

    /**
     * File credit dispute
     */
    public CreditDispute fileDispute(CreditDisputeRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        CreditDispute dispute = new CreditDispute();
        dispute.setCustomer(customer);
        dispute.setAccountReference(request.getAccountReference());
        dispute.setReason(request.getReason());
        dispute.setSupportingDocuments(request.getSupportingDocuments());
        dispute.setStatus(DisputeStatus.PENDING);

        return creditDisputeRepository.save(dispute);
    }

    /**
     * Get credit improvement plan
     */
    public CreditImprovementPlan getImprovementPlan(Long customerId, Integer targetScore) {
        CreditScore currentScore = creditScoreRepository.findLatestByCustomerId(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Credit score not found"));

        if (targetScore == null) {
            targetScore = Math.min(currentScore.getScore() + 100, 900);
        }

        CreditImprovementPlan plan = new CreditImprovementPlan(currentScore.getScore(), targetScore);
        plan.setEstimatedTimeframe("6-12 months");

        List<CreditImprovementPlan.Recommendation> recommendations = new ArrayList<>();

        // Payment history recommendations
        if (currentScore.getOnTimePaymentPercentage() < 95) {
            CreditImprovementPlan.Recommendation rec = new CreditImprovementPlan.Recommendation();
            rec.setTitle("Improve Payment History");
            rec.setDescription("Make all payments on time for the next 6 months");
            rec.setPriority(RecommendationPriority.CRITICAL);
            rec.setEstimatedImpact(50);
            rec.setActionSteps("Set up auto-pay for all loans and bills; Monitor due dates carefully");
            rec.setTimeframe("6 months");
            recommendations.add(rec);
        }

        // Credit utilization recommendations
        if (currentScore.getCreditUtilizationRatio() > 0.30) {
            CreditImprovementPlan.Recommendation rec = new CreditImprovementPlan.Recommendation();
            rec.setTitle("Reduce Credit Utilization");
            rec.setDescription("Bring credit utilization below 30%");
            rec.setPriority(RecommendationPriority.HIGH);
            rec.setEstimatedImpact(40);
            rec.setActionSteps("Pay down existing loans; Avoid taking new loans temporarily");
            rec.setTimeframe("3-6 months");
            recommendations.add(rec);
        }

        // Hard inquiries recommendations
        if (currentScore.getHardInquiriesLast6Months() > 2) {
            CreditImprovementPlan.Recommendation rec = new CreditImprovementPlan.Recommendation();
            rec.setTitle("Limit New Credit Applications");
            rec.setDescription("Avoid applying for new credit for 6 months");
            rec.setPriority(RecommendationPriority.MEDIUM);
            rec.setEstimatedImpact(20);
            rec.setActionSteps("Only apply for credit when absolutely necessary");
            rec.setTimeframe("6 months");
            recommendations.add(rec);
        }

        // Credit mix recommendations
        if (currentScore.getNumberOfActiveAccounts() < 3) {
            CreditImprovementPlan.Recommendation rec = new CreditImprovementPlan.Recommendation();
            rec.setTitle("Diversify Credit Portfolio");
            rec.setDescription("Consider opening different types of accounts");
            rec.setPriority(RecommendationPriority.LOW);
            rec.setEstimatedImpact(15);
            rec.setActionSteps("Open a savings account, FD, or RD to build credit mix");
            rec.setTimeframe("3-6 months");
            recommendations.add(rec);
        }

        plan.setRecommendations(recommendations);

        return plan;
    }
}
