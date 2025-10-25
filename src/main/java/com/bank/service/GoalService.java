package com.bank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.GoalContributionRequest;
import com.bank.dto.GoalRequest;
import com.bank.exception.InsufficientFundsException;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Account;
import com.bank.model.Customer;
import com.bank.model.FinancialGoal;
import com.bank.model.GoalStatus;
import com.bank.model.GoalType;
import com.bank.repository.AccountRepository;
import com.bank.repository.CustomerRepository;
import com.bank.repository.FinancialGoalRepository;

@Service
@Transactional
public class GoalService {
    
    @Autowired
    private FinancialGoalRepository goalRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    public FinancialGoal createGoal(String username, GoalRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        FinancialGoal goal = new FinancialGoal();
        goal.setCustomer(customer);
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setType(GoalType.valueOf(request.getType().toUpperCase()));
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setMonthlyContribution(request.getMonthlyContribution());
        goal.setIsAutomated(request.getIsAutomated() != null ? request.getIsAutomated() : false);
        
        if (request.getLinkedAccountId() != null) {
            Account account = accountRepository.findById(request.getLinkedAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            goal.setLinkedAccount(account);
        }
        
        goal.setStatus(GoalStatus.IN_PROGRESS);
        
        return goalRepository.save(goal);
    }
    
    public List<FinancialGoal> getAllGoals(String username) {
        return goalRepository.findByCustomer_User_Username(username);
    }
    
    public List<FinancialGoal> getActiveGoals(String username) {
        return goalRepository.findByCustomer_User_UsernameAndStatus(username, GoalStatus.IN_PROGRESS);
    }
    
    public FinancialGoal getGoalById(Long goalId) {
        return goalRepository.findById(goalId)
            .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }
    
    public FinancialGoal updateGoal(Long goalId, GoalRequest request) {
        FinancialGoal goal = getGoalById(goalId);
        
        if (request.getName() != null) {
            goal.setName(request.getName());
        }
        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }
        if (request.getType() != null) {
            goal.setType(GoalType.valueOf(request.getType().toUpperCase()));
        }
        if (request.getTargetAmount() != null) {
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }
        if (request.getMonthlyContribution() != null) {
            goal.setMonthlyContribution(request.getMonthlyContribution());
        }
        if (request.getIsAutomated() != null) {
            goal.setIsAutomated(request.getIsAutomated());
        }
        if (request.getLinkedAccountId() != null) {
            Account account = accountRepository.findById(request.getLinkedAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
            goal.setLinkedAccount(account);
        }
        
        return goalRepository.save(goal);
    }
    
    public FinancialGoal contributeToGoal(GoalContributionRequest request) {
        FinancialGoal goal = getGoalById(request.getGoalId());
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        
        // Check if account has sufficient balance
        if (account.getBalance() < request.getAmount().doubleValue()) {
            throw new InsufficientFundsException("Insufficient balance in account");
        }
        
        // Deduct from account
        account.setBalance(account.getBalance() - request.getAmount().doubleValue());
        accountRepository.save(account);
        
        // Add to goal
        BigDecimal newAmount = goal.getCurrentAmount().add(request.getAmount());
        goal.setCurrentAmount(newAmount);
        
        // Check if goal is completed
        if (goal.isCompleted()) {
            goal.setStatus(GoalStatus.COMPLETED);
            goal.setCompletedAt(LocalDate.now());
        } else {
            // Update status based on progress
            double progress = goal.getProgressPercentage();
            long daysRemaining = goal.getDaysRemaining();
            
            if (progress >= 75 || daysRemaining > 0) {
                goal.setStatus(GoalStatus.ON_TRACK);
            } else if (progress < 50 && daysRemaining < 90) {
                goal.setStatus(GoalStatus.BEHIND);
            }
        }
        
        return goalRepository.save(goal);
    }
    
    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new ResourceNotFoundException("Goal not found");
        }
        goalRepository.deleteById(goalId);
    }
    
    public void pauseGoal(Long goalId) {
        FinancialGoal goal = getGoalById(goalId);
        goal.setStatus(GoalStatus.PAUSED);
        goalRepository.save(goal);
    }
    
    public void resumeGoal(Long goalId) {
        FinancialGoal goal = getGoalById(goalId);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        goalRepository.save(goal);
    }
    
    public void cancelGoal(Long goalId) {
        FinancialGoal goal = getGoalById(goalId);
        goal.setStatus(GoalStatus.CANCELLED);
        goalRepository.save(goal);
    }
    
    // Process automated contributions (to be called by scheduled job)
    public void processAutomatedContributions() {
        List<FinancialGoal> automatedGoals = goalRepository.findByIsAutomated(true);
        
        for (FinancialGoal goal : automatedGoals) {
            if (goal.getStatus() != GoalStatus.IN_PROGRESS) {
                continue;
            }
            
            if (goal.getLinkedAccount() == null || goal.getMonthlyContribution() == null) {
                continue;
            }
            
            Account account = goal.getLinkedAccount();
            BigDecimal contribution = goal.getMonthlyContribution();
            
            // Check if account has sufficient balance
            if (account.getBalance() >= contribution.doubleValue()) {
                GoalContributionRequest request = new GoalContributionRequest();
                request.setGoalId(goal.getId());
                request.setAmount(contribution);
                request.setAccountId(account.getId());
                
                contributeToGoal(request);
            }
        }
    }
}
