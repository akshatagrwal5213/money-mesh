package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.model.DeductionType;
import com.bank.model.TaxDeduction;

@Repository
public interface TaxDeductionRepository extends JpaRepository<TaxDeduction, Long> {
    
    List<TaxDeduction> findByCustomerId(Long customerId);
    
    List<TaxDeduction> findByCustomerIdAndFinancialYear(Long customerId, String financialYear);
    
    @Query("SELECT SUM(d.amount) FROM TaxDeduction d WHERE d.customer.id = :customerId AND d.financialYear = :financialYear")
    Double getTotalDeductionsByCustomerAndYear(Long customerId, String financialYear);
    
    @Query("SELECT SUM(d.amount) FROM TaxDeduction d WHERE d.customer.id = :customerId AND d.financialYear = :financialYear AND d.deductionType IN :types")
    Double getDeductionsByTypeAndYear(Long customerId, String financialYear, List<DeductionType> types);
}
