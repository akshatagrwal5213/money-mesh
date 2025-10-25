package com.bank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.BudgetRequest;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Budget;
import com.bank.model.BudgetPeriod;
import com.bank.model.Customer;
import com.bank.repository.BudgetRepository;
import com.bank.repository.CustomerRepository;

@Service
@Transactional
public class BudgetService {
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public Budget createBudget(String username, BudgetRequest request) {
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        Budget budget = new Budget();
        budget.setCustomer(customer);
        budget.setName(request.getName());
        budget.setCategory(request.getCategory());
        budget.setBudgetAmount(request.getBudgetAmount());
        budget.setPeriod(BudgetPeriod.valueOf(request.getPeriod().toUpperCase()));
        
        // Calculate dates based on period
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        LocalDate endDate;
        
        if (request.getEndDate() != null) {
            endDate = request.getEndDate();
        } else {
            endDate = calculateEndDate(startDate, budget.getPeriod());
        }
        
        budget.setStartDate(startDate);
        budget.setEndDate(endDate);
        
        if (request.getAlertThreshold() != null) {
            budget.setAlertThreshold(request.getAlertThreshold());
        }
        
        return budgetRepository.save(budget);
    }
    
    private LocalDate calculateEndDate(LocalDate startDate, BudgetPeriod period) {
        switch (period) {
            case WEEKLY:
                return startDate.plusWeeks(1);
            case MONTHLY:
                return startDate.plusMonths(1);
            case QUARTERLY:
                return startDate.plusMonths(3);
            case YEARLY:
                return startDate.plusYears(1);
            default:
                return startDate.plusMonths(1);
        }
    }
    
    public List<Budget> getAllBudgets(String username) {
        return budgetRepository.findByCustomer_User_Username(username);
    }
    
    public List<Budget> getActiveBudgets(String username) {
        return budgetRepository.findByCustomer_User_UsernameAndIsActive(username, true);
    }
    
    public Budget getBudgetById(Long budgetId) {
        return budgetRepository.findById(budgetId)
            .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
    }
    
    public Budget updateBudget(Long budgetId, BudgetRequest request) {
        Budget budget = getBudgetById(budgetId);
        
        if (request.getName() != null) {
            budget.setName(request.getName());
        }
        if (request.getCategory() != null) {
            budget.setCategory(request.getCategory());
        }
        if (request.getBudgetAmount() != null) {
            budget.setBudgetAmount(request.getBudgetAmount());
        }
        if (request.getPeriod() != null) {
            budget.setPeriod(BudgetPeriod.valueOf(request.getPeriod().toUpperCase()));
        }
        if (request.getStartDate() != null) {
            budget.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            budget.setEndDate(request.getEndDate());
        }
        if (request.getAlertThreshold() != null) {
            budget.setAlertThreshold(request.getAlertThreshold());
        }
        
        return budgetRepository.save(budget);
    }
    
    public void addExpense(Long budgetId, BigDecimal amount) {
        Budget budget = getBudgetById(budgetId);
        
        BigDecimal newSpent = budget.getSpentAmount().add(amount);
        budget.setSpentAmount(newSpent);
        
        budgetRepository.save(budget);
        
        // Check if alert should be sent
        if (budget.shouldSendAlert()) {
            budget.setAlertSent(true);
            budgetRepository.save(budget);
            
            notificationService.sendBudgetAlert(
                budget.getCustomer().getUser().getUsername(),
                budget.getName(),
                budget.getPercentageSpent()
            );
        }
    }
    
    public void deleteBudget(Long budgetId) {
        if (!budgetRepository.existsById(budgetId)) {
            throw new ResourceNotFoundException("Budget not found");
        }
        budgetRepository.deleteById(budgetId);
    }
    
    public void deactivateBudget(Long budgetId) {
        Budget budget = getBudgetById(budgetId);
        budget.setIsActive(false);
        budgetRepository.save(budget);
    }
    
    public void resetBudget(Long budgetId) {
        Budget budget = getBudgetById(budgetId);
        budget.setSpentAmount(BigDecimal.ZERO);
        budget.setAlertSent(false);
        
        // Move to next period
        LocalDate newStart = budget.getEndDate().plusDays(1);
        LocalDate newEnd = calculateEndDate(newStart, budget.getPeriod());
        budget.setStartDate(newStart);
        budget.setEndDate(newEnd);
        
        budgetRepository.save(budget);
    }
}
