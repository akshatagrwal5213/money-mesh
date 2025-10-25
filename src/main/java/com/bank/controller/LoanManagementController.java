package com.bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.EmiScheduleDto;
import com.bank.dto.ForeclosureRequest;
import com.bank.dto.ForeclosureResponse;
import com.bank.dto.PrepaymentRequest;
import com.bank.dto.PrepaymentResponse;
import com.bank.dto.RestructureRequest;
import com.bank.dto.RestructureResponse;
import com.bank.service.LoanManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loan-management")
@CrossOrigin(origins = "*")
public class LoanManagementController {
    
    @Autowired
    private LoanManagementService loanManagementService;
    
    // Generate or get EMI schedule
    @GetMapping("/loans/{loanId}/emi-schedule")
    public ResponseEntity<List<EmiScheduleDto>> getEmiSchedule(@PathVariable Long loanId) {
        List<EmiScheduleDto> schedule = loanManagementService.generateEmiSchedule(loanId);
        return ResponseEntity.ok(schedule);
    }
    
    // Record EMI payment
    @PostMapping("/emi/{emiId}/pay")
    public ResponseEntity<Map<String, Object>> recordEmiPayment(
            @PathVariable Long emiId,
            @RequestParam Double amount,
            @RequestParam(required = false) String reference) {
        
        EmiScheduleDto result = loanManagementService.recordEmiPayment(emiId, amount, reference);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "EMI payment recorded successfully");
        response.put("emi", result);
        
        return ResponseEntity.ok(response);
    }
    
    // Calculate prepayment details
    @PostMapping("/loans/{loanId}/prepayment/calculate")
    public ResponseEntity<Map<String, Object>> calculatePrepayment(
            @PathVariable Long loanId,
            @RequestParam Double amount) {
        
        // This is a calculation endpoint - doesn't actually process
        Map<String, Object> calculation = new HashMap<>();
        calculation.put("loanId", loanId);
        calculation.put("prepaymentAmount", amount);
        calculation.put("message", "Use /prepayment endpoint to process");
        
        return ResponseEntity.ok(calculation);
    }
    
    // Process prepayment
    @PostMapping("/prepayment")
    public ResponseEntity<Map<String, Object>> processPrepayment(@Valid @RequestBody PrepaymentRequest request) {
        PrepaymentResponse response = loanManagementService.processPrepayment(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Prepayment processed successfully");
        result.put("prepayment", response);
        result.put("interestSaved", response.getInterestSaved());
        result.put("tenureReduced", response.getTenureReduced());
        
        return ResponseEntity.ok(result);
    }
    
    // Request loan restructure
    @PostMapping("/restructure/request")
    public ResponseEntity<Map<String, Object>> requestRestructure(@Valid @RequestBody RestructureRequest request) {
        RestructureResponse response = loanManagementService.requestRestructure(request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Restructure request submitted successfully");
        result.put("restructure", response);
        result.put("status", "PENDING_APPROVAL");
        
        return ResponseEntity.ok(result);
    }
    
    // Approve restructure request (Admin endpoint)
    @PostMapping("/restructure/{restructureId}/approve")
    public ResponseEntity<Map<String, Object>> approveRestructure(
            @PathVariable Long restructureId,
            @RequestParam(required = false) String remarks) {
        
        RestructureResponse response = loanManagementService.approveRestructure(restructureId, remarks);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Restructure request approved");
        result.put("restructure", response);
        
        return ResponseEntity.ok(result);
    }
    
    // Implement approved restructure
    @PostMapping("/restructure/{restructureId}/implement")
    public ResponseEntity<Map<String, Object>> implementRestructure(@PathVariable Long restructureId) {
        RestructureResponse response = loanManagementService.implementRestructure(restructureId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Restructure implemented successfully");
        result.put("restructure", response);
        result.put("newEmiAmount", response.getNewEmiAmount());
        result.put("newTenure", response.getNewTenureMonths());
        
        return ResponseEntity.ok(result);
    }
    
    // Calculate foreclosure amount
    @GetMapping("/loans/{loanId}/foreclosure/calculate")
    public ResponseEntity<ForeclosureResponse> calculateForeclosure(@PathVariable Long loanId) {
        ForeclosureResponse response = loanManagementService.calculateForeclosureAmount(loanId);
        return ResponseEntity.ok(response);
    }
    
    // Process foreclosure
    @PostMapping("/foreclosure")
    public ResponseEntity<Map<String, Object>> processForeclosure(
            @Valid @RequestBody ForeclosureRequest request,
            @RequestParam Double paymentAmount,
            @RequestParam String reference) {
        
        ForeclosureResponse response = loanManagementService.processForeclosure(request, paymentAmount, reference);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Loan foreclosed successfully");
        result.put("foreclosure", response);
        result.put("interestSaved", response.getInterestSaved());
        
        return ResponseEntity.ok(result);
    }
    
    // Trigger overdue check (Admin/Scheduled endpoint)
    @PostMapping("/admin/check-overdues")
    public ResponseEntity<Map<String, String>> checkOverdues() {
        loanManagementService.checkOverdueEmis();
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Overdue EMIs checked and updated");
        
        return ResponseEntity.ok(response);
    }
}
