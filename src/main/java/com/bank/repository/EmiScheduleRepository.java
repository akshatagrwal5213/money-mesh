package com.bank.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.model.EmiSchedule;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiSchedule, Long> {
    
    List<EmiSchedule> findByLoanId(Long loanId);
    
    List<EmiSchedule> findByLoanIdOrderByEmiNumberAsc(Long loanId);
    
    List<EmiSchedule> findByLoanIdAndIsPaidFalse(Long loanId);
    
    @Query("SELECT e FROM EmiSchedule e WHERE e.loan.customer.id = :customerId AND e.isPaid = false AND e.dueDate <= :date ORDER BY e.dueDate ASC")
    List<EmiSchedule> findUpcomingDues(@Param("customerId") Long customerId, @Param("date") LocalDate date);
    
    @Query("SELECT e FROM EmiSchedule e WHERE e.loan.customer.id = :customerId AND e.isPaid = false AND e.dueDate < :currentDate")
    List<EmiSchedule> findOverdueEmis(@Param("customerId") Long customerId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT e FROM EmiSchedule e WHERE e.isPaid = false AND e.dueDate < :currentDate")
    List<EmiSchedule> findAllOverdueEmis(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT COUNT(e) FROM EmiSchedule e WHERE e.loan.id = :loanId AND e.isPaid = true")
    Long countPaidEmis(@Param("loanId") Long loanId);
    
    @Query("SELECT SUM(e.principalComponent) FROM EmiSchedule e WHERE e.loan.id = :loanId AND e.isPaid = true")
    Double getTotalPrincipalPaid(@Param("loanId") Long loanId);
}
