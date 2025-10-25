package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Customer;
import com.bank.model.InvestmentStatus;
import com.bank.model.RecurringDeposit;

@Repository
public interface RecurringDepositRepository extends JpaRepository<RecurringDeposit, Long> {
    
    Optional<RecurringDeposit> findByRdNumber(String rdNumber);
    
    List<RecurringDeposit> findByCustomer(Customer customer);
    
    List<RecurringDeposit> findByCustomerAndStatus(Customer customer, InvestmentStatus status);
    
    List<RecurringDeposit> findByStatus(InvestmentStatus status);
    
    List<RecurringDeposit> findByNextInstallmentDateBefore(LocalDate date);
    
    List<RecurringDeposit> findByMaturityDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<RecurringDeposit> findByCustomerAndAutoDebit(Customer customer, Boolean autoDebit);
    
    boolean existsByRdNumber(String rdNumber);
}
