package com.bank.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.BudgetRequest;
import com.bank.model.Budget;
import com.bank.service.BudgetService;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    
    @Autowired
    private BudgetService budgetService;
    
    @PostMapping
    public ResponseEntity<Budget> createBudget(
            @RequestBody BudgetRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        Budget budget = budgetService.createBudget(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(budget);
    }
    
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets(Authentication authentication) {
        String username = authentication.getName();
        List<Budget> budgets = budgetService.getAllBudgets(username);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Budget>> getActiveBudgets(Authentication authentication) {
        String username = authentication.getName();
        List<Budget> budgets = budgetService.getActiveBudgets(username);
        return ResponseEntity.ok(budgets);
    }
    
    @GetMapping("/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long budgetId) {
        Budget budget = budgetService.getBudgetById(budgetId);
        return ResponseEntity.ok(budget);
    }
    
    @PutMapping("/{budgetId}")
    public ResponseEntity<Budget> updateBudget(
            @PathVariable Long budgetId,
            @RequestBody BudgetRequest request) {
        Budget budget = budgetService.updateBudget(budgetId, request);
        return ResponseEntity.ok(budget);
    }
    
    @PostMapping("/{budgetId}/add-expense")
    public ResponseEntity<Void> addExpense(
            @PathVariable Long budgetId,
            @RequestParam BigDecimal amount) {
        budgetService.addExpense(budgetId, amount);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{budgetId}/deactivate")
    public ResponseEntity<Void> deactivateBudget(@PathVariable Long budgetId) {
        budgetService.deactivateBudget(budgetId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{budgetId}/reset")
    public ResponseEntity<Void> resetBudget(@PathVariable Long budgetId) {
        budgetService.resetBudget(budgetId);
        return ResponseEntity.ok().build();
    }
}
