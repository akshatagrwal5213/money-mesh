package com.bank.controller;

import com.bank.dto.*;
import com.bank.model.*;
import com.bank.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    // Fixed Deposit Endpoints
    
    @PostMapping("/fd/create")
    public ResponseEntity<FixedDeposit> createFixedDeposit(
            @RequestBody FixedDepositRequest request,
            Authentication authentication) {
        FixedDeposit fd = investmentService.createFixedDeposit(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(fd);
    }
    
    @GetMapping("/fd/my-deposits")
    public ResponseEntity<List<FixedDeposit>> getMyFixedDeposits(Authentication authentication) {
        List<FixedDeposit> deposits = investmentService.getCustomerFixedDeposits(authentication.getName());
        return ResponseEntity.ok(deposits);
    }
    
    @GetMapping("/fd/{fdNumber}")
    public ResponseEntity<FixedDeposit> getFixedDepositDetails(
            @PathVariable String fdNumber,
            Authentication authentication) {
        FixedDeposit fd = investmentService.getFixedDepositDetails(fdNumber, authentication.getName());
        return ResponseEntity.ok(fd);
    }
    
    @PostMapping("/fd/{fdNumber}/close")
    public ResponseEntity<FixedDeposit> closeFixedDeposit(
            @PathVariable String fdNumber,
            Authentication authentication) {
        FixedDeposit fd = investmentService.closeFixedDeposit(fdNumber, authentication.getName());
        return ResponseEntity.ok(fd);
    }

    // Recurring Deposit Endpoints
    
    @PostMapping("/rd/create")
    public ResponseEntity<RecurringDeposit> createRecurringDeposit(
            @RequestBody RecurringDepositRequest request,
            Authentication authentication) {
        RecurringDeposit rd = investmentService.createRecurringDeposit(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(rd);
    }
    
    @GetMapping("/rd/my-deposits")
    public ResponseEntity<List<RecurringDeposit>> getMyRecurringDeposits(Authentication authentication) {
        List<RecurringDeposit> deposits = investmentService.getCustomerRecurringDeposits(authentication.getName());
        return ResponseEntity.ok(deposits);
    }
    
    @PostMapping("/rd/{rdNumber}/pay-installment")
    public ResponseEntity<RecurringDeposit> payRDInstallment(
            @PathVariable String rdNumber,
            Authentication authentication) {
        RecurringDeposit rd = investmentService.payRDInstallment(rdNumber, authentication.getName());
        return ResponseEntity.ok(rd);
    }

    // Mutual Fund Endpoints
    
    @GetMapping("/mutual-funds")
    public ResponseEntity<List<MutualFund>> getAllMutualFunds() {
        List<MutualFund> funds = investmentService.getAllMutualFunds();
        return ResponseEntity.ok(funds);
    }
    
    @GetMapping("/mutual-funds/{fundCode}")
    public ResponseEntity<MutualFund> getMutualFundDetails(@PathVariable String fundCode) {
        MutualFund fund = investmentService.getMutualFundDetails(fundCode);
        return ResponseEntity.ok(fund);
    }
    
    @PostMapping("/mutual-funds/invest")
    public ResponseEntity<MutualFundHolding> investInMutualFund(
            @RequestBody MutualFundInvestmentRequest request,
            Authentication authentication) {
        MutualFundHolding holding = investmentService.investInMutualFund(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(holding);
    }
    
    @GetMapping("/mutual-funds/my-holdings")
    public ResponseEntity<List<MutualFundHolding>> getMyMutualFundHoldings(Authentication authentication) {
        List<MutualFundHolding> holdings = investmentService.getCustomerMutualFundHoldings(authentication.getName());
        return ResponseEntity.ok(holdings);
    }

    // SIP Endpoints
    
    @PostMapping("/sip/create")
    public ResponseEntity<SipInvestment> createSip(
            @RequestBody SipRequest request,
            Authentication authentication) {
        SipInvestment sip = investmentService.createSip(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(sip);
    }
    
    @GetMapping("/sip/my-sips")
    public ResponseEntity<List<SipInvestment>> getMySips(Authentication authentication) {
        List<SipInvestment> sips = investmentService.getCustomerSips(authentication.getName());
        return ResponseEntity.ok(sips);
    }
    
    @PostMapping("/sip/{sipNumber}/cancel")
    public ResponseEntity<SipInvestment> cancelSip(
            @PathVariable String sipNumber,
            Authentication authentication) {
        SipInvestment sip = investmentService.cancelSip(sipNumber, authentication.getName());
        return ResponseEntity.ok(sip);
    }

    // Portfolio Endpoints
    
    @GetMapping("/portfolio")
    public ResponseEntity<InvestmentPortfolio> getMyPortfolio(Authentication authentication) {
        InvestmentPortfolio portfolio = investmentService.getCustomerPortfolio(authentication.getName());
        return ResponseEntity.ok(portfolio);
    }
}
