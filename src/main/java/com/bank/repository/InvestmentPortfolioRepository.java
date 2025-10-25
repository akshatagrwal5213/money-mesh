package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.Customer;
import com.bank.model.InvestmentPortfolio;

@Repository
public interface InvestmentPortfolioRepository extends JpaRepository<InvestmentPortfolio, Long> {
    
    Optional<InvestmentPortfolio> findByCustomer(Customer customer);
    
    boolean existsByCustomer(Customer customer);
}
