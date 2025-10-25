package com.bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.FinancialHealthResponse;
import com.bank.dto.PortfolioAnalysisResponse;
import com.bank.dto.RetirementPlanRequest;
import com.bank.dto.RetirementPlanResponse;
import com.bank.dto.WealthProfileRequest;
import com.bank.model.InvestmentRecommendation;
import com.bank.model.WealthProfile;
import com.bank.service.WealthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wealth")
public class WealthController {

    @Autowired
    private WealthService wealthService;

    // ==================== Wealth Profile Endpoints ====================

    @PostMapping("/profile")
    public ResponseEntity<WealthProfile> createOrUpdateProfile(
            @Valid @RequestBody WealthProfileRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        WealthProfile profile = wealthService.createOrUpdateWealthProfileByUsername(username, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/profile")
    public ResponseEntity<WealthProfile> getProfile(Authentication authentication) {
        String username = authentication.getName();
        WealthProfile profile = wealthService.getWealthProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    // ==================== Portfolio Analysis Endpoints ====================

    @GetMapping("/portfolio-analysis")
    public ResponseEntity<PortfolioAnalysisResponse> analyzePortfolio(Authentication authentication) {
        String username = authentication.getName();
        PortfolioAnalysisResponse analysis = wealthService.analyzePortfolioByUsername(username);
        return ResponseEntity.ok(analysis);
    }

    // ==================== Retirement Planning Endpoints ====================

    @PostMapping("/retirement-plan")
    public ResponseEntity<RetirementPlanResponse> createRetirementPlan(
            @Valid @RequestBody RetirementPlanRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        RetirementPlanResponse plan = wealthService.createRetirementPlanByUsername(username, request);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/retirement-plan")
    public ResponseEntity<Map<String, Object>> getLatestRetirementPlan(Authentication authentication) {
        String username = authentication.getName();
        
        // Get the latest saved plan
        RetirementPlanResponse plan = wealthService.createRetirementPlanByUsername(username, 
            createDefaultRetirementRequest());
        
        Map<String, Object> response = new HashMap<>();
        response.put("plan", plan);
        response.put("message", "Create a custom retirement plan using POST /api/wealth/retirement-plan");
        
        return ResponseEntity.ok(response);
    }

    private RetirementPlanRequest createDefaultRetirementRequest() {
        RetirementPlanRequest request = new RetirementPlanRequest();
        request.setCurrentAge(30);
        request.setRetirementAge(60);
        request.setCurrentSavings(100000.0);
        request.setMonthlyInvestment(10000.0);
        request.setExpectedReturn(12.0);
        request.setInflationRate(6.0);
        request.setDesiredMonthlyIncome(50000.0);
        request.setLifeExpectancy(80);
        return request;
    }

    // ==================== Financial Health Endpoints ====================

    @GetMapping("/financial-health")
    public ResponseEntity<FinancialHealthResponse> calculateFinancialHealth(Authentication authentication) {
        String username = authentication.getName();
        FinancialHealthResponse healthResponse = wealthService.calculateFinancialHealthByUsername(username);
        return ResponseEntity.ok(healthResponse);
    }

    // ==================== Investment Recommendations Endpoints ====================

    @GetMapping("/recommendations")
    public ResponseEntity<List<InvestmentRecommendation>> getRecommendations(Authentication authentication) {
        String username = authentication.getName();
        List<InvestmentRecommendation> recommendations = wealthService.getRecommendationsByUsername(username);
        return ResponseEntity.ok(recommendations);
    }

    // ==================== Dashboard Summary Endpoint ====================

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        String username = authentication.getName();
        
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            // Get wealth profile
            WealthProfile profile = wealthService.getWealthProfileByUsername(username);
            dashboard.put("profile", profile);
        } catch (Exception e) {
            dashboard.put("profile", null);
            dashboard.put("profileMessage", "Please create a wealth profile first");
        }
        
        try {
            // Get portfolio analysis
            PortfolioAnalysisResponse analysis = wealthService.analyzePortfolioByUsername(username);
            dashboard.put("portfolio", analysis);
        } catch (Exception e) {
            dashboard.put("portfolio", null);
        }
        
        try {
            // Get financial health
            FinancialHealthResponse health = wealthService.calculateFinancialHealthByUsername(username);
            dashboard.put("health", health);
        } catch (Exception e) {
            dashboard.put("health", null);
            dashboard.put("healthMessage", "Create a wealth profile to see your financial health score");
        }
        
        try {
            // Get recommendations
            List<InvestmentRecommendation> recommendations = wealthService.getRecommendationsByUsername(username);
            dashboard.put("recommendations", recommendations);
        } catch (Exception e) {
            dashboard.put("recommendations", List.of());
        }
        
        return ResponseEntity.ok(dashboard);
    }

    // ==================== Net Worth Summary Endpoint ====================

    @GetMapping("/net-worth")
    public ResponseEntity<Map<String, Object>> getNetWorthSummary(Authentication authentication) {
        String username = authentication.getName();
        
        PortfolioAnalysisResponse analysis = wealthService.analyzePortfolioByUsername(username);
        
        Map<String, Object> netWorth = new HashMap<>();
        netWorth.put("totalNetWorth", analysis.getTotalNetWorth());
        netWorth.put("currentAllocation", analysis.getCurrentAllocation());
        netWorth.put("targetAllocation", analysis.getTargetAllocation());
        netWorth.put("diversificationScore", analysis.getDiversificationScore());
        netWorth.put("riskScore", analysis.getRiskScore());
        netWorth.put("needsRebalancing", analysis.getNeedsRebalancing());
        
        return ResponseEntity.ok(netWorth);
    }
}
