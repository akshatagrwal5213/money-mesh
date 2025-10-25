package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.TransactionAnalytics;

@Repository
public interface TransactionAnalyticsRepository extends JpaRepository<TransactionAnalytics, Long> {
    
    List<TransactionAnalytics> findByCustomer_User_Username(String username);
    
    Optional<TransactionAnalytics> findByCustomer_User_UsernameAndPeriodStartAndPeriodEnd(
        String username, LocalDate periodStart, LocalDate periodEnd);
    
    List<TransactionAnalytics> findByCustomer_User_UsernameOrderByPeriodStartDesc(String username);
}
