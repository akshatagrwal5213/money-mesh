package com.bank.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.AnalyticsRequest;
import com.bank.dto.AnalyticsResponse;
import com.bank.model.TransactionAnalytics;
import com.bank.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @PostMapping("/transaction-analytics")
    public ResponseEntity<AnalyticsResponse> getTransactionAnalytics(
            @RequestBody AnalyticsRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        AnalyticsResponse analytics = analyticsService.getTransactionAnalytics(username, request);
        return ResponseEntity.ok(analytics);
    }
    
    @PostMapping("/save-analytics")
    public ResponseEntity<TransactionAnalytics> saveAnalytics(
            @RequestParam LocalDate periodStart,
            @RequestParam LocalDate periodEnd,
            Authentication authentication) {
        String username = authentication.getName();
        TransactionAnalytics analytics = analyticsService.saveAnalytics(username, periodStart, periodEnd);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<TransactionAnalytics>> getAllAnalytics(Authentication authentication) {
        String username = authentication.getName();
        List<TransactionAnalytics> analytics = analyticsService.getAllAnalytics(username);
        return ResponseEntity.ok(analytics);
    }
    
    @GetMapping("/account-summary")
    public ResponseEntity<Map<String, Object>> getAccountSummary(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> summary = analyticsService.getAccountSummary(username);
        return ResponseEntity.ok(summary);
    }
}
