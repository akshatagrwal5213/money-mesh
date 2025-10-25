package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Customer;
import com.bank.model.InvestmentStatus;
import com.bank.model.MutualFund;
import com.bank.model.MutualFundHolding;

@Repository
public interface MutualFundHoldingRepository extends JpaRepository<MutualFundHolding, Long> {
    
    Optional<MutualFundHolding> findByFolioNumber(String folioNumber);
    
    List<MutualFundHolding> findByCustomer(Customer customer);
    
    List<MutualFundHolding> findByCustomerAndStatus(Customer customer, InvestmentStatus status);
    
    List<MutualFundHolding> findByMutualFund(MutualFund mutualFund);
    
    Optional<MutualFundHolding> findByCustomerAndMutualFundAndStatus(Customer customer, MutualFund mutualFund, InvestmentStatus status);
    
    List<MutualFundHolding> findByStatus(InvestmentStatus status);
    
    boolean existsByFolioNumber(String folioNumber);
}
