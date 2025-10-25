package com.bank.repository;

import com.bank.model.CreditScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditScoreRepository extends JpaRepository<CreditScore, Long> {
    
    // Find all credit scores for a customer
    List<CreditScore> findByCustomerId(Long customerId);
    
    // Find latest credit score for a customer
    @Query("SELECT cs FROM CreditScore cs WHERE cs.customer.id = :customerId ORDER BY cs.calculationDate DESC LIMIT 1")
    Optional<CreditScore> findLatestByCustomerId(@Param("customerId") Long customerId);
    
    // Find credit scores for a customer ordered by calculation date
    List<CreditScore> findByCustomerIdOrderByCalculationDateDesc(Long customerId);
    
    // Find credit scores within a date range
    @Query("SELECT cs FROM CreditScore cs WHERE cs.customer.id = :customerId AND cs.calculationDate BETWEEN :startDate AND :endDate ORDER BY cs.calculationDate DESC")
    List<CreditScore> findByCustomerIdAndCalculationDateBetween(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Get score history for trending
    @Query("SELECT cs FROM CreditScore cs WHERE cs.customer.id = :customerId AND cs.calculationDate >= :sinceDate ORDER BY cs.calculationDate ASC")
    List<CreditScore> getScoreHistory(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Check if customer has any credit scores
    boolean existsByCustomerId(Long customerId);
}
