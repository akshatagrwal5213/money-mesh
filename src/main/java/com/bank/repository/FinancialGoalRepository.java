package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.FinancialGoal;
import com.bank.model.GoalStatus;

@Repository
public interface FinancialGoalRepository extends JpaRepository<FinancialGoal, Long> {
    
    List<FinancialGoal> findByCustomer_User_Username(String username);
    
    List<FinancialGoal> findByCustomer_User_UsernameAndStatus(String username, GoalStatus status);
    
    List<FinancialGoal> findByCustomer_User_UsernameOrderByTargetDateAsc(String username);
    
    List<FinancialGoal> findByIsAutomated(Boolean isAutomated);
}
