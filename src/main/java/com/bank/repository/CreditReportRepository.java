package com.bank.repository;

import com.bank.model.CreditReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditReportRepository extends JpaRepository<CreditReport, Long> {
    
    // Find all reports for a customer
    List<CreditReport> findByCustomerId(Long customerId);
    
    // Find latest report for a customer
    @Query("SELECT cr FROM CreditReport cr WHERE cr.customer.id = :customerId ORDER BY cr.generatedDate DESC LIMIT 1")
    Optional<CreditReport> findLatestByCustomerId(@Param("customerId") Long customerId);
    
    // Find reports ordered by generated date
    List<CreditReport> findByCustomerIdOrderByGeneratedDateDesc(Long customerId);
    
    // Find reports within date range
    @Query("SELECT cr FROM CreditReport cr WHERE cr.customer.id = :customerId AND cr.generatedDate BETWEEN :startDate AND :endDate ORDER BY cr.generatedDate DESC")
    List<CreditReport> findByCustomerIdAndGeneratedDateBetween(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Check if customer has any reports
    boolean existsByCustomerId(Long customerId);
}
