package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.model.FinancialHealthScore;

public interface FinancialHealthScoreRepository extends JpaRepository<FinancialHealthScore, Long> {
    
    List<FinancialHealthScore> findByCustomerIdOrderByScoreDateDesc(Long customerId);
    
    @Query("SELECT f FROM FinancialHealthScore f WHERE f.customer.id = :customerId ORDER BY f.scoreDate DESC")
    Optional<FinancialHealthScore> findLatestByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT f FROM FinancialHealthScore f WHERE f.customer.id = :customerId AND f.scoreDate BETWEEN :startDate AND :endDate ORDER BY f.scoreDate")
    List<FinancialHealthScore> findByCustomerIdAndDateRange(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
