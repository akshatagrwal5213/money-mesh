package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.model.TaxPayment;

@Repository
public interface TaxPaymentRepository extends JpaRepository<TaxPayment, Long> {
    
    List<TaxPayment> findByCustomerId(Long customerId);
    
    List<TaxPayment> findByCustomerIdAndFinancialYear(Long customerId, String financialYear);
    
    @Query("SELECT SUM(p.amount) FROM TaxPayment p WHERE p.customer.id = :customerId AND p.financialYear = :financialYear")
    Double getTotalPaymentsByCustomerAndYear(Long customerId, String financialYear);
}
