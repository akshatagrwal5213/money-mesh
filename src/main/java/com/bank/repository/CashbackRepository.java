package com.bank.repository;

import com.bank.model.Cashback;
import com.bank.model.RewardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CashbackRepository extends JpaRepository<Cashback, Long> {
    
    List<Cashback> findByCustomerId(Long customerId);
    
    List<Cashback> findByCustomerIdAndIsCreditedTrue(Long customerId);
    
    List<Cashback> findByIsCreditedFalse();
    
    @Query("SELECT SUM(c.cashbackAmount) FROM Cashback c WHERE c.customer.id = :customerId AND c.isCredited = true")
    Double getTotalCashback(@Param("customerId") Long customerId);
    
    @Query("SELECT c FROM Cashback c WHERE c.customer.id = :customerId ORDER BY c.transactionDate DESC")
    List<Cashback> findByCustomerIdOrderByTransactionDateDesc(@Param("customerId") Long customerId);
    
    List<Cashback> findByCategory(RewardCategory category);
    
    @Query("SELECT c FROM Cashback c WHERE c.transactionDate BETWEEN :startDate AND :endDate")
    List<Cashback> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
