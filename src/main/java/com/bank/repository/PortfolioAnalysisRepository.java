package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.model.PortfolioAnalysis;

public interface PortfolioAnalysisRepository extends JpaRepository<PortfolioAnalysis, Long> {
    
    List<PortfolioAnalysis> findByCustomerIdOrderByAnalysisDateDesc(Long customerId);
    
    @Query("SELECT p FROM PortfolioAnalysis p WHERE p.customer.id = :customerId ORDER BY p.analysisDate DESC")
    Optional<PortfolioAnalysis> findLatestByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM PortfolioAnalysis p WHERE p.customer.id = :customerId AND p.analysisDate BETWEEN :startDate AND :endDate ORDER BY p.analysisDate")
    List<PortfolioAnalysis> findByCustomerIdAndDateRange(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
