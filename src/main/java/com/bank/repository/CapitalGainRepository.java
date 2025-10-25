package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.model.CapitalGain;

@Repository
public interface CapitalGainRepository extends JpaRepository<CapitalGain, Long> {
    
    List<CapitalGain> findByCustomerId(Long customerId);
    
    List<CapitalGain> findByCustomerIdAndFinancialYear(Long customerId, String financialYear);
    
    @Query("SELECT SUM(c.taxAmount) FROM CapitalGain c WHERE c.customer.id = :customerId AND c.financialYear = :financialYear")
    Double getTotalCapitalGainsTaxByCustomerAndYear(Long customerId, String financialYear);
}
