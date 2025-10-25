package com.bank.repository;

import com.bank.model.FixedDeposit;
import com.bank.model.InvestmentStatus;
import com.bank.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    
    Optional<FixedDeposit> findByFdNumber(String fdNumber);
    
    List<FixedDeposit> findByCustomer(Customer customer);
    
    List<FixedDeposit> findByCustomerAndStatus(Customer customer, InvestmentStatus status);
    
    List<FixedDeposit> findByStatus(InvestmentStatus status);
    
    List<FixedDeposit> findByMaturityDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<FixedDeposit> findByMaturityDateBefore(LocalDate date);
    
    List<FixedDeposit> findByCustomerAndMaturityDateBetween(Customer customer, LocalDate startDate, LocalDate endDate);
    
    boolean existsByFdNumber(String fdNumber);
}
