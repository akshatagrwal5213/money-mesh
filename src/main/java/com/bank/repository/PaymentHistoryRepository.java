package com.bank.repository;

import com.bank.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    
    // Find all payment history for a customer
    List<PaymentHistory> findByCustomerId(Long customerId);
    
    // Find payment history within date range
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.paymentDate BETWEEN :startDate AND :endDate ORDER BY ph.paymentDate DESC")
    List<PaymentHistory> findByCustomerIdAndPaymentDateBetween(
        @Param("customerId") Long customerId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Find recent payment history
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.paymentDate >= :sinceDate ORDER BY ph.paymentDate DESC")
    List<PaymentHistory> findRecentPayments(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Count on-time payments
    @Query("SELECT COUNT(ph) FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.status = 'ON_TIME' AND ph.paymentDate >= :sinceDate")
    Long countOnTimePayments(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Count total payments since date
    @Query("SELECT COUNT(ph) FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.paymentDate >= :sinceDate")
    Long countTotalPayments(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Find late payments
    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.customer.id = :customerId AND ph.status IN ('LATE', 'MISSED') AND ph.paymentDate >= :sinceDate ORDER BY ph.paymentDate DESC")
    List<PaymentHistory> findLatePayments(
        @Param("customerId") Long customerId,
        @Param("sinceDate") LocalDate sinceDate
    );
    
    // Find payment history ordered by date
    List<PaymentHistory> findByCustomerIdOrderByPaymentDateDesc(Long customerId);
}
