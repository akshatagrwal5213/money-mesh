package com.bank.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.FinancialHealthResponse;
import com.bank.dto.PortfolioAnalysisResponse;
import com.bank.dto.RetirementPlanRequest;
import com.bank.dto.RetirementPlanResponse;
import com.bank.dto.WealthProfileRequest;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.AssetClass;
import com.bank.model.Customer;
import com.bank.model.FinancialHealthScore;
import com.bank.model.FixedDeposit;
import com.bank.model.HealthScoreCategory;
import com.bank.model.InsurancePolicy;
import com.bank.model.InsurancePolicyStatus;
import com.bank.model.InvestmentRecommendation;
import com.bank.model.InvestmentStatus;
import com.bank.model.Loan;
import com.bank.model.LoanStatus;
import com.bank.model.MutualFundHolding;
import com.bank.model.PortfolioAnalysis;
import com.bank.model.RetirementPlan;
import com.bank.model.WealthProfile;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.FinancialHealthScoreRepository;
import com.bank.repository.FixedDepositRepository;
import com.bank.repository.InsurancePolicyRepository;
import com.bank.repository.InvestmentRecommendationRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.MutualFundHoldingRepository;
import com.bank.repository.PortfolioAnalysisRepository;
// RecurringDepositRepository was previously injected but not used. Removed to clean up warnings.
import com.bank.repository.RetirementPlanRepository;
import com.bank.repository.WealthProfileRepository;

@Service
@Transactional
public class WealthService {

    @Autowired
    private WealthProfileRepository wealthProfileRepository;

    @Autowired
    private PortfolioAnalysisRepository portfolioAnalysisRepository;

    @Autowired
    private RetirementPlanRepository retirementPlanRepository;

    @Autowired
    private FinancialHealthScoreRepository financialHealthScoreRepository;

    @Autowired
    private InvestmentRecommendationRepository investmentRecommendationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private FixedDepositRepository fixedDepositRepository;

    // recurring deposits are not currently used in calculations; field removed to avoid unused-field warnings

    @Autowired
    private MutualFundHoldingRepository mutualFundHoldingRepository;

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;

    // ==================== Wealth Profile Management ====================

    public WealthProfile createOrUpdateWealthProfile(Long customerId, WealthProfileRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        WealthProfile profile = wealthProfileRepository.findByCustomerId(customerId)
            .orElse(new WealthProfile());

        profile.setCustomer(customer);
        profile.setRiskProfile(request.getRiskProfile());
        profile.setAge(request.getAge());
        profile.setRetirementAge(request.getRetirementAge());
        profile.setMonthlyIncome(request.getMonthlyIncome());
        profile.setMonthlyExpenses(request.getMonthlyExpenses());
        profile.setEmergencyFundMonths(request.getEmergencyFundMonths());
        profile.setRebalanceStrategy(request.getRebalanceStrategy());
        
        // Set target allocations
        profile.setTargetEquityPercentage(request.getTargetEquityPercentage());
        profile.setTargetDebtPercentage(request.getTargetDebtPercentage());
        profile.setTargetGoldPercentage(request.getTargetGoldPercentage());
        profile.setTargetCashPercentage(request.getTargetCashPercentage());
        profile.setTargetRealEstatePercentage(request.getTargetRealEstatePercentage());
        profile.setTargetAlternativePercentage(request.getTargetAlternativePercentage());
        
        profile.setLastUpdatedDate(LocalDate.now());

        return wealthProfileRepository.save(profile);
    }

    public WealthProfile getWealthProfile(Long customerId) {
        return wealthProfileRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Wealth profile not found"));
    }

    // ==================== Portfolio Analysis ====================

    public PortfolioAnalysisResponse analyzePortfolio(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        WealthProfile profile = wealthProfileRepository.findByCustomerId(customerId)
            .orElse(null);

        // Calculate current asset values
        Double cashValue = calculateCashValue(customerId);
        Double equityValue = calculateEquityValue(customerId);
        Double debtValue = calculateDebtValue(customerId);
        Double goldValue = calculateGoldValue(customerId);
        Double realEstateValue = calculateRealEstateValue(customerId);
        Double alternativeValue = 0.0; // Placeholder

        Double totalNetWorth = cashValue + equityValue + debtValue + goldValue + realEstateValue + alternativeValue;

        // Calculate percentages
        Double equityPct = totalNetWorth > 0 ? (equityValue / totalNetWorth) * 100 : 0.0;
        Double debtPct = totalNetWorth > 0 ? (debtValue / totalNetWorth) * 100 : 0.0;
        Double goldPct = totalNetWorth > 0 ? (goldValue / totalNetWorth) * 100 : 0.0;
        Double cashPct = totalNetWorth > 0 ? (cashValue / totalNetWorth) * 100 : 0.0;
        Double realEstatePct = totalNetWorth > 0 ? (realEstateValue / totalNetWorth) * 100 : 0.0;
        Double alternativePct = totalNetWorth > 0 ? (alternativeValue / totalNetWorth) * 100 : 0.0;

        // Calculate diversification score
        Double diversificationScore = calculateDiversificationScore(
            equityPct, debtPct, goldPct, cashPct, realEstatePct, alternativePct
        );

        // Calculate risk score
        Double riskScore = calculateRiskScore(equityPct, debtPct, goldPct, cashPct, realEstatePct);

        // Check if rebalancing is needed
        Boolean needsRebalancing = false;
        Double deviationFromTarget = 0.0;
        List<PortfolioAnalysisResponse.RebalanceRecommendation> rebalanceRecommendations = new ArrayList<>();

        if (profile != null) {
            deviationFromTarget = calculateDeviationFromTarget(
                equityPct, debtPct, goldPct, cashPct, realEstatePct, alternativePct, profile
            );
            needsRebalancing = deviationFromTarget > 10.0; // Threshold: 10%

            if (needsRebalancing) {
                rebalanceRecommendations = generateRebalanceRecommendations(
                    totalNetWorth, equityPct, debtPct, goldPct, cashPct, realEstatePct, alternativePct, profile
                );
            }
        }

        // Save analysis
        PortfolioAnalysis analysis = new PortfolioAnalysis();
        analysis.setCustomer(customer);
        analysis.setTotalNetWorth(totalNetWorth);
        analysis.setEquityValue(equityValue);
        analysis.setDebtValue(debtValue);
        analysis.setGoldValue(goldValue);
        analysis.setCashValue(cashValue);
        analysis.setRealEstateValue(realEstateValue);
        analysis.setAlternativeValue(alternativeValue);
        analysis.setEquityPercentage(equityPct);
        analysis.setDebtPercentage(debtPct);
        analysis.setGoldPercentage(goldPct);
        analysis.setCashPercentage(cashPct);
        analysis.setRealEstatePercentage(realEstatePct);
        analysis.setAlternativePercentage(alternativePct);
        analysis.setDiversificationScore(diversificationScore);
        analysis.setRiskScore(riskScore);
        analysis.setNeedsRebalancing(needsRebalancing);
        analysis.setDeviationFromTarget(deviationFromTarget);
        portfolioAnalysisRepository.save(analysis);

        // Build response
        PortfolioAnalysisResponse response = new PortfolioAnalysisResponse();
        response.setAnalysisDate(LocalDate.now());
        response.setTotalNetWorth(totalNetWorth);
        response.setDiversificationScore(diversificationScore);
        response.setRiskScore(riskScore);
        response.setNeedsRebalancing(needsRebalancing);
        response.setDeviationFromTarget(deviationFromTarget);
        response.setRebalancingRecommendations(rebalanceRecommendations);

        // Current allocation
        PortfolioAnalysisResponse.AssetAllocation current = new PortfolioAnalysisResponse.AssetAllocation();
        current.setEquityValue(equityValue);
        current.setDebtValue(debtValue);
        current.setGoldValue(goldValue);
        current.setCashValue(cashValue);
        current.setRealEstateValue(realEstateValue);
        current.setAlternativeValue(alternativeValue);
        current.setEquityPercentage(equityPct);
        current.setDebtPercentage(debtPct);
        current.setGoldPercentage(goldPct);
        current.setCashPercentage(cashPct);
        current.setRealEstatePercentage(realEstatePct);
        current.setAlternativePercentage(alternativePct);
        response.setCurrentAllocation(current);

        // Target allocation (if profile exists)
        if (profile != null) {
            PortfolioAnalysisResponse.AssetAllocation target = new PortfolioAnalysisResponse.AssetAllocation();
            target.setEquityPercentage(profile.getTargetEquityPercentage());
            target.setDebtPercentage(profile.getTargetDebtPercentage());
            target.setGoldPercentage(profile.getTargetGoldPercentage());
            target.setCashPercentage(profile.getTargetCashPercentage());
            target.setRealEstatePercentage(profile.getTargetRealEstatePercentage());
            target.setAlternativePercentage(profile.getTargetAlternativePercentage());
            response.setTargetAllocation(target);
        }

        return response;
    }

    private Double calculateCashValue(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0.0;
        
        List<Account> accounts = accountRepository.findByCustomer(customer);
        return accounts.stream()
            .mapToDouble(a -> a.getBalance())
            .sum();
    }

    private Double calculateEquityValue(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0.0;
        
        List<MutualFundHolding> holdings = mutualFundHoldingRepository.findByCustomer(customer);
        return holdings.stream()
            .filter(h -> h.getMutualFund() != null && 
                        h.getMutualFund().getCategory() != null && 
                        (h.getMutualFund().getCategory().toString().contains("EQUITY") ||
                         h.getMutualFund().getCategory().toString().contains("HYBRID")))
            .mapToDouble(h -> h.getCurrentValue() != null ? h.getCurrentValue().doubleValue() : 0.0)
            .sum();
    }

    private Double calculateDebtValue(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0.0;
        
        // Fixed deposits + debt mutual funds
        List<FixedDeposit> fds = fixedDepositRepository.findByCustomer(customer);
        Double fdValue = fds.stream()
            .filter(fd -> fd.getStatus() == InvestmentStatus.ACTIVE)
            .mapToDouble(fd -> fd.getPrincipalAmount() != null ? fd.getPrincipalAmount().doubleValue() : 0.0)
            .sum();

        List<MutualFundHolding> debtFunds = mutualFundHoldingRepository.findByCustomer(customer);
        Double debtMfValue = debtFunds.stream()
            .filter(h -> h.getMutualFund() != null && 
                        h.getMutualFund().getCategory() != null && 
                        h.getMutualFund().getCategory().toString().contains("DEBT"))
            .mapToDouble(h -> h.getCurrentValue() != null ? h.getCurrentValue().doubleValue() : 0.0)
            .sum();

        return fdValue + debtMfValue;
    }

    private Double calculateGoldValue(Long customerId) {
        // Placeholder - read customerId to avoid unused-variable warning
        if (customerId == null) return 0.0;
        // TODO: integrate with gold investment tracking if available
        return 0.0;
    }

    private Double calculateRealEstateValue(Long customerId) {
        // Placeholder - read customerId to avoid unused-variable warning
        if (customerId == null) return 0.0;
        // TODO: integrate with property investment tracking if available
        return 0.0;
    }

    private Double calculateDiversificationScore(Double eq, Double debt, Double gold, Double cash, Double re, Double alt) {
        // Using Herfindahl-Hirschman Index (HHI) approach
        // Lower HHI = better diversification
        Double hhi = Math.pow(eq, 2) + Math.pow(debt, 2) + Math.pow(gold, 2) + 
                     Math.pow(cash, 2) + Math.pow(re, 2) + Math.pow(alt, 2);
        
        // Convert to 0-100 score (10000 HHI = 0 score, 0 HHI = 100 score)
        Double score = 100.0 - (hhi / 100.0);
        return Math.max(0.0, Math.min(100.0, score));
    }

    private Double calculateRiskScore(Double eq, Double debt, Double gold, Double cash, Double re) {
        // Risk weights: Equity(1.0), RealEstate(0.8), Gold(0.5), Debt(0.3), Cash(0.1)
        Double riskScore = (eq * 1.0) + (re * 0.8) + (gold * 0.5) + (debt * 0.3) + (cash * 0.1);
        return Math.max(0.0, Math.min(100.0, riskScore));
    }

    private Double calculateDeviationFromTarget(Double eq, Double debt, Double gold, Double cash, 
                                                 Double re, Double alt, WealthProfile profile) {
        if (profile == null) return 0.0;
        // Use primitive doubles to avoid accidental unboxing of null values
        double eqVal = eq != null ? eq : 0.0;
        double debtVal = debt != null ? debt : 0.0;
        double goldVal = gold != null ? gold : 0.0;
        double cashVal = cash != null ? cash : 0.0;
        double reVal = re != null ? re : 0.0;
        double altVal = alt != null ? alt : 0.0;

    Double tEq = profile.getTargetEquityPercentage();
    Double tDebt = profile.getTargetDebtPercentage();
    Double tGold = profile.getTargetGoldPercentage();
    Double tCash = profile.getTargetCashPercentage();
    Double tRe = profile.getTargetRealEstatePercentage();
    Double tAlt = profile.getTargetAlternativePercentage();

    double targetEq = tEq != null ? tEq : 0.0;
    double targetDebt = tDebt != null ? tDebt : 0.0;
    double targetGold = tGold != null ? tGold : 0.0;
    double targetCash = tCash != null ? tCash : 0.0;
    double targetRe = tRe != null ? tRe : 0.0;
    double targetAlt = tAlt != null ? tAlt : 0.0;

        double eqDev = Math.abs(eqVal - targetEq);
        double debtDev = Math.abs(debtVal - targetDebt);
        double goldDev = Math.abs(goldVal - targetGold);
        double cashDev = Math.abs(cashVal - targetCash);
        double reDev = Math.abs(reVal - targetRe);
        double altDev = Math.abs(altVal - targetAlt);

        return (eqDev + debtDev + goldDev + cashDev + reDev + altDev) / 6.0;
    }

    private List<PortfolioAnalysisResponse.RebalanceRecommendation> generateRebalanceRecommendations(
            Double totalNetWorth, Double eq, Double debt, Double gold, Double cash, Double re, Double alt, WealthProfile profile) {
        
        List<PortfolioAnalysisResponse.RebalanceRecommendation> recommendations = new ArrayList<>();

        addRebalanceRecommendation(recommendations, AssetClass.EQUITY, eq, 
            profile.getTargetEquityPercentage(), totalNetWorth);
        addRebalanceRecommendation(recommendations, AssetClass.DEBT, debt, 
            profile.getTargetDebtPercentage(), totalNetWorth);
        addRebalanceRecommendation(recommendations, AssetClass.GOLD, gold, 
            profile.getTargetGoldPercentage(), totalNetWorth);
        addRebalanceRecommendation(recommendations, AssetClass.CASH, cash, 
            profile.getTargetCashPercentage(), totalNetWorth);
        addRebalanceRecommendation(recommendations, AssetClass.REAL_ESTATE, re, 
            profile.getTargetRealEstatePercentage(), totalNetWorth);
        addRebalanceRecommendation(recommendations, AssetClass.ALTERNATIVE, alt, 
            profile.getTargetAlternativePercentage(), totalNetWorth);

        return recommendations;
    }

    private void addRebalanceRecommendation(List<PortfolioAnalysisResponse.RebalanceRecommendation> recommendations,
                                           AssetClass assetClass, Double currentPct, Double targetPct, Double totalNetWorth) {
        if (targetPct == null || Math.abs(currentPct - targetPct) < 5.0) return; // Skip small deviations
        
        PortfolioAnalysisResponse.RebalanceRecommendation rec = new PortfolioAnalysisResponse.RebalanceRecommendation();
        rec.setAssetClass(assetClass);
        rec.setCurrentPercentage(currentPct);
        rec.setTargetPercentage(targetPct);
        
        if (currentPct < targetPct) {
            rec.setAction("INCREASE");
            rec.setAmountToAdjust((targetPct - currentPct) * totalNetWorth / 100.0);
        } else {
            rec.setAction("DECREASE");
            rec.setAmountToAdjust((currentPct - targetPct) * totalNetWorth / 100.0);
        }
        
        recommendations.add(rec);
    }

    // ==================== Retirement Planning ====================

    public RetirementPlanResponse createRetirementPlan(Long customerId, RetirementPlanRequest request) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Double yearsToRetirement = Double.valueOf(request.getRetirementAge() - request.getCurrentAge());
        Double retirementYears = Double.valueOf(request.getLifeExpectancy() - request.getRetirementAge());

        // Calculate future value of current savings
        Double fvCurrentSavings = calculateFutureValue(
            request.getCurrentSavings(), 
            request.getExpectedReturn() / 100.0, 
            yearsToRetirement.intValue()
        );

        // Calculate future value of monthly SIP
        Double fvMonthlySip = calculateFutureValueSip(
            request.getMonthlyInvestment(),
            request.getExpectedReturn() / 100.0,
            yearsToRetirement.intValue()
        );

        Double projectedCorpus = fvCurrentSavings + fvMonthlySip;

        // Calculate required corpus for retirement
        // Adjust desired income for inflation at retirement
        Double futureMonthlyIncome = request.getDesiredMonthlyIncome() * 
            Math.pow(1 + (request.getInflationRate() / 100.0), yearsToRetirement);

        // Required corpus = Monthly income * 12 * retirement years / (expected return - inflation)
        Double realReturn = request.getExpectedReturn() - request.getInflationRate();
        Double corpusRequired = (futureMonthlyIncome * 12 * retirementYears) / (realReturn / 100.0);

        Boolean onTrack = projectedCorpus >= corpusRequired;
        Double shortfall = onTrack ? 0.0 : corpusRequired - projectedCorpus;
        Double surplus = onTrack ? projectedCorpus - corpusRequired : 0.0;

        // Calculate recommended SIP if not on track
        Double recommendedSip = request.getMonthlyInvestment();
        if (!onTrack) {
            // Calculate required SIP to meet corpus
            Double requiredFV = corpusRequired - fvCurrentSavings;
            recommendedSip = calculateRequiredSip(requiredFV, request.getExpectedReturn() / 100.0, yearsToRetirement.intValue());
        }

        // Save retirement plan
        RetirementPlan plan = new RetirementPlan();
        plan.setCustomer(customer);
        plan.setCurrentAge(request.getCurrentAge());
        plan.setRetirementAge(request.getRetirementAge());
        plan.setCurrentSavings(request.getCurrentSavings());
        plan.setMonthlyInvestment(request.getMonthlyInvestment());
        plan.setExpectedReturn(request.getExpectedReturn());
        plan.setInflationRate(request.getInflationRate());
        plan.setDesiredMonthlyIncome(request.getDesiredMonthlyIncome());
        plan.setLifeExpectancy(request.getLifeExpectancy());
        plan.setProjectedCorpus(projectedCorpus);
        plan.setCorpusRequired(corpusRequired);
        plan.setOnTrack(onTrack);
        plan.setShortfall(shortfall);
        plan.setSurplus(surplus);
        plan.setRecommendedMonthlySip(recommendedSip);
        plan.setYearsToRetirement(yearsToRetirement);

        // Generate recommendations
        List<String> recommendations = new ArrayList<>();
        if (!onTrack) {
            recommendations.add("Increase monthly SIP to ₹" + String.format("%.0f", recommendedSip) + " to meet retirement goal");
            recommendations.add("Consider diversifying into equity for higher returns over the long term");
        } else {
            recommendations.add("You're on track! Continue with current investment strategy");
            recommendations.add("Consider increasing SIP annually to beat inflation");
        }
        if (yearsToRetirement > 15) {
            recommendations.add("With " + String.format("%.0f", yearsToRetirement) + " years to retirement, you can take higher risk in equity");
        } else if (yearsToRetirement < 5) {
            recommendations.add("Close to retirement - start shifting to safer debt instruments");
        }
        plan.setRecommendations(String.join("; ", recommendations));

        retirementPlanRepository.save(plan);

        // Build response
        RetirementPlanResponse response = new RetirementPlanResponse();
        response.setCurrentAge(request.getCurrentAge());
        response.setRetirementAge(request.getRetirementAge());
        response.setYearsToRetirement(yearsToRetirement);
        response.setProjectedCorpus(projectedCorpus);
        response.setCorpusRequired(corpusRequired);
        response.setOnTrack(onTrack);
        response.setShortfall(shortfall);
        response.setSurplus(surplus);
        response.setRecommendedMonthlySip(recommendedSip);
        response.setRecommendations(recommendations);

        return response;
    }

    private Double calculateFutureValue(Double pv, Double rate, Integer years) {
        return pv * Math.pow(1 + rate, years);
    }

    private Double calculateFutureValueSip(Double monthlyAmount, Double annualRate, Integer years) {
        Double monthlyRate = annualRate / 12.0;
        Integer months = years * 12;
        return monthlyAmount * ((Math.pow(1 + monthlyRate, months) - 1) / monthlyRate) * (1 + monthlyRate);
    }

    private Double calculateRequiredSip(Double fv, Double annualRate, Integer years) {
        Double monthlyRate = annualRate / 12.0;
        Integer months = years * 12;
        return fv / (((Math.pow(1 + monthlyRate, months) - 1) / monthlyRate) * (1 + monthlyRate));
    }

    // ==================== Financial Health Score ====================

    public FinancialHealthResponse calculateFinancialHealth(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        WealthProfile profile = wealthProfileRepository.findByCustomerId(customerId).orElse(null);
        
        if (profile == null) {
            throw new ResourceNotFoundException("Wealth profile required for financial health calculation");
        }

        // Get previous score for comparison
        FinancialHealthScore previousScore = financialHealthScoreRepository
            .findLatestByCustomerId(customerId).orElse(null);

        // Calculate component scores
        Double savingsScore = calculateSavingsScore(profile);
        Double debtScore = calculateDebtScore(customerId, profile);
        Double emergencyFundScore = calculateEmergencyFundScore(profile);
        Double investmentScore = calculateInvestmentScore(customerId);
        Double insuranceScore = calculateInsuranceScore(customerId, profile);
        Double retirementScore = calculateRetirementScore(customerId);

        // Calculate overall score (weighted average)
        Double overallScore = (savingsScore * 0.2) + (debtScore * 0.2) + (emergencyFundScore * 0.15) +
                             (investmentScore * 0.15) + (insuranceScore * 0.15) + (retirementScore * 0.15);

        HealthScoreCategory category = categorizeScore(overallScore);

        // Generate improvement recommendations
        List<String> recommendations = new ArrayList<>();
        if (savingsScore < 60) recommendations.add("Increase your savings rate to at least 20% of income");
        if (debtScore < 60) recommendations.add("Focus on reducing debt - aim for debt-to-income ratio below 30%");
        if (emergencyFundScore < 60) recommendations.add("Build emergency fund to cover 6 months of expenses");
        if (investmentScore < 60) recommendations.add("Diversify your investment portfolio across asset classes");
        if (insuranceScore < 60) recommendations.add("Review and increase life insurance coverage");
        if (retirementScore < 60) recommendations.add("Increase retirement savings to meet your goals");

        // Save score
        FinancialHealthScore score = new FinancialHealthScore();
        score.setCustomer(customer);
        score.setOverallScore(overallScore);
        score.setCategory(category);
        score.setSavingsScore(savingsScore);
        score.setDebtScore(debtScore);
        score.setEmergencyFundScore(emergencyFundScore);
        score.setInvestmentScore(investmentScore);
        score.setInsuranceScore(insuranceScore);
        score.setRetirementScore(retirementScore);
        
        // Set metrics
        Double savingsRate = ((profile.getMonthlyIncome() - profile.getMonthlyExpenses()) / profile.getMonthlyIncome()) * 100;
        score.setSavingsRate(savingsRate);
        
        Double debtToIncomeRatio = calculateDebtToIncomeRatio(customerId, profile);
        score.setDebtToIncomeRatio(debtToIncomeRatio);
        score.setEmergencyFundMonths(profile.getEmergencyFundMonths());
        
        Double investmentDiversity = calculateDiversificationScore(
            calculateEquityValue(customerId), calculateDebtValue(customerId), 
            calculateGoldValue(customerId), calculateCashValue(customerId), 
            calculateRealEstateValue(customerId), 0.0
        );
        score.setInvestmentDiversity(investmentDiversity);
        
        score.setImprovementRecommendations(String.join("; ", recommendations));

        if (previousScore != null) {
            score.setPreviousScoreDate(previousScore.getScoreDate());
            score.setPreviousOverallScore(previousScore.getOverallScore());
            score.setScoreImprovement(overallScore - previousScore.getOverallScore());
        }

        financialHealthScoreRepository.save(score);

        // Build response
        FinancialHealthResponse response = new FinancialHealthResponse();
        response.setScoreDate(LocalDate.now());
        response.setOverallScore(overallScore);
        response.setCategory(category);
        response.setSavingsScore(savingsScore);
        response.setDebtScore(debtScore);
        response.setEmergencyFundScore(emergencyFundScore);
        response.setInvestmentScore(investmentScore);
        response.setInsuranceScore(insuranceScore);
        response.setRetirementScore(retirementScore);
        response.setSavingsRate(savingsRate);
        response.setDebtToIncomeRatio(debtToIncomeRatio);
        response.setEmergencyFundMonths(profile.getEmergencyFundMonths());
        response.setInvestmentDiversity(investmentDiversity);
        response.setImprovementRecommendations(recommendations);
        response.setPreviousOverallScore(previousScore != null ? previousScore.getOverallScore() : null);
        response.setScoreImprovement(previousScore != null ? overallScore - previousScore.getOverallScore() : null);

        return response;
    }

    private Double calculateSavingsScore(WealthProfile profile) {
        Double savingsRate = ((profile.getMonthlyIncome() - profile.getMonthlyExpenses()) / profile.getMonthlyIncome()) * 100;
        if (savingsRate >= 30) return 100.0;
        if (savingsRate >= 20) return 80.0;
        if (savingsRate >= 10) return 60.0;
        if (savingsRate >= 5) return 40.0;
        return 20.0;
    }

    private Double calculateDebtScore(Long customerId, WealthProfile profile) {
        Double ratio = calculateDebtToIncomeRatio(customerId, profile);
        if (ratio <= 10) return 100.0;
        if (ratio <= 30) return 80.0;
        if (ratio <= 50) return 60.0;
        if (ratio <= 70) return 40.0;
        return 20.0;
    }

    private Double calculateDebtToIncomeRatio(Long customerId, WealthProfile profile) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) return 0.0;
        
        List<Loan> loans = loanRepository.findByCustomer(customer);
        double totalDebt = 0.0;
        for (Loan l : loans) {
            if (l.getStatus() == LoanStatus.DISBURSED) {
                Double out = l.getOutstandingAmount();
                totalDebt += (out != null ? out : 0.0);
            }
        }
        
        Double annualIncome = profile.getMonthlyIncome() * 12;
        return annualIncome > 0 ? (totalDebt / annualIncome) * 100 : 0.0;
    }

    private Double calculateEmergencyFundScore(WealthProfile profile) {
        Double monthsObj = profile.getEmergencyFundMonths();
    double months = monthsObj != null ? monthsObj : 0.0;
        if (months >= 6) return 100.0;
        if (months >= 3) return 70.0;
        if (months >= 1) return 40.0;
        return 20.0;
    }

    private Double calculateInvestmentScore(Long customerId) {
        PortfolioAnalysisResponse analysis = analyzePortfolio(customerId);
        return analysis.getDiversificationScore();
    }

    private Double calculateInsuranceScore(Long customerId, WealthProfile profile) {
        List<InsurancePolicy> policies = insurancePolicyRepository.findByCustomerId(customerId);
        Double totalCoverage = policies.stream()
            .filter(p -> p.getStatus() == InsurancePolicyStatus.ACTIVE && 
                        p.getInsuranceType() != null &&
                        (p.getInsuranceType().toString().contains("LIFE") ||
                         p.getInsuranceType().toString().contains("TERM")))
            .mapToDouble(p -> p.getCoverageAmount() != null ? p.getCoverageAmount().doubleValue() : 0.0)
            .sum();
        
        Double annualIncome = profile.getMonthlyIncome() * 12;
        Double coverageRatio = annualIncome > 0 ? totalCoverage / annualIncome : 0.0;
        
        if (coverageRatio >= 10) return 100.0;
        if (coverageRatio >= 7) return 80.0;
        if (coverageRatio >= 5) return 60.0;
        if (coverageRatio >= 3) return 40.0;
        return 20.0;
    }

    private Double calculateRetirementScore(Long customerId) {
        RetirementPlan plan = retirementPlanRepository.findLatestByCustomerId(customerId).orElse(null);
        if (plan == null) return 20.0;
        
        if (plan.getOnTrack()) return 100.0;
        
        Double readiness = (plan.getProjectedCorpus() / plan.getCorpusRequired()) * 100;
        if (readiness >= 80) return 80.0;
        if (readiness >= 60) return 60.0;
        if (readiness >= 40) return 40.0;
        return 20.0;
    }

    private HealthScoreCategory categorizeScore(Double score) {
        if (score >= 80) return HealthScoreCategory.EXCELLENT;
        if (score >= 60) return HealthScoreCategory.GOOD;
        if (score >= 40) return HealthScoreCategory.FAIR;
        if (score >= 20) return HealthScoreCategory.POOR;
        return HealthScoreCategory.CRITICAL;
    }

    // ==================== Username-based wrapper methods ====================

    public WealthProfile createOrUpdateWealthProfileByUsername(String username, WealthProfileRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return createOrUpdateWealthProfile(customer.getId(), request);
    }

    public WealthProfile getWealthProfileByUsername(String username) {
        return wealthProfileRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Wealth profile not found"));
    }

    public PortfolioAnalysisResponse analyzePortfolioByUsername(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return analyzePortfolio(customer.getId());
    }

    public RetirementPlanResponse createRetirementPlanByUsername(String username, RetirementPlanRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return createRetirementPlan(customer.getId(), request);
    }

    public FinancialHealthResponse calculateFinancialHealthByUsername(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return calculateFinancialHealth(customer.getId());
    }

    public List<InvestmentRecommendation> getRecommendationsByUsername(String username) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return investmentRecommendationRepository.findActiveRecommendations(customer.getId());
    }
}
