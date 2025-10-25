package com.bank.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Budget;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    List<Budget> findByCustomer_User_Username(String username);
    
    List<Budget> findByCustomer_User_UsernameAndIsActive(String username, Boolean isActive);
    
    List<Budget> findByCustomer_User_UsernameAndCategory(String username, String category);
    
    List<Budget> findByCustomer_User_UsernameAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
        String username, LocalDate endDate, LocalDate startDate);
}
