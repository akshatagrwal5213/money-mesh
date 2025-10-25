package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Customer;
import com.bank.model.InvestmentStatus;
import com.bank.model.MutualFund;
import com.bank.model.SipFrequency;
import com.bank.model.SipInvestment;

@Repository
public interface SipInvestmentRepository extends JpaRepository<SipInvestment, Long> {
    
    Optional<SipInvestment> findBySipNumber(String sipNumber);
    
    List<SipInvestment> findByCustomer(Customer customer);
    
    List<SipInvestment> findByCustomerAndStatus(Customer customer, InvestmentStatus status);
    
    List<SipInvestment> findByMutualFund(MutualFund mutualFund);
    
    List<SipInvestment> findByStatus(InvestmentStatus status);
    
    List<SipInvestment> findByNextInstallmentDateBefore(LocalDate date);
    
    List<SipInvestment> findByFrequency(SipFrequency frequency);
    
    List<SipInvestment> findByCustomerAndAutoDebit(Customer customer, Boolean autoDebit);
    
    boolean existsBySipNumber(String sipNumber);
}
