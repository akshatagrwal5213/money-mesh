package com.bank.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.GoalContributionRequest;
import com.bank.dto.GoalRequest;
import com.bank.model.FinancialGoal;
import com.bank.service.GoalService;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    
    @Autowired
    private GoalService goalService;
    
    @PostMapping
    public ResponseEntity<FinancialGoal> createGoal(
            @RequestBody GoalRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        FinancialGoal goal = goalService.createGoal(username, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(goal);
    }
    
    @GetMapping
    public ResponseEntity<List<FinancialGoal>> getAllGoals(Authentication authentication) {
        String username = authentication.getName();
        List<FinancialGoal> goals = goalService.getAllGoals(username);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<FinancialGoal>> getActiveGoals(Authentication authentication) {
        String username = authentication.getName();
        List<FinancialGoal> goals = goalService.getActiveGoals(username);
        return ResponseEntity.ok(goals);
    }
    
    @GetMapping("/{goalId}")
    public ResponseEntity<FinancialGoal> getGoalById(@PathVariable Long goalId) {
        FinancialGoal goal = goalService.getGoalById(goalId);
        return ResponseEntity.ok(goal);
    }
    
    @PutMapping("/{goalId}")
    public ResponseEntity<FinancialGoal> updateGoal(
            @PathVariable Long goalId,
            @RequestBody GoalRequest request) {
        FinancialGoal goal = goalService.updateGoal(goalId, request);
        return ResponseEntity.ok(goal);
    }
    
    @PostMapping("/contribute")
    public ResponseEntity<FinancialGoal> contributeToGoal(@RequestBody GoalContributionRequest request) {
        FinancialGoal goal = goalService.contributeToGoal(request);
        return ResponseEntity.ok(goal);
    }
    
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{goalId}/pause")
    public ResponseEntity<Void> pauseGoal(@PathVariable Long goalId) {
        goalService.pauseGoal(goalId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{goalId}/resume")
    public ResponseEntity<Void> resumeGoal(@PathVariable Long goalId) {
        goalService.resumeGoal(goalId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{goalId}/cancel")
    public ResponseEntity<Void> cancelGoal(@PathVariable Long goalId) {
        goalService.cancelGoal(goalId);
        return ResponseEntity.ok().build();
    }
}
