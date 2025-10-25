package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.FundCategory;
import com.bank.model.MutualFund;
import com.bank.model.RiskLevel;

@Repository
public interface MutualFundRepository extends JpaRepository<MutualFund, Long> {
    
    Optional<MutualFund> findByFundCode(String fundCode);
    
    List<MutualFund> findByCategory(FundCategory category);
    
    List<MutualFund> findByRiskLevel(RiskLevel riskLevel);
    
    List<MutualFund> findByAmc(String amc);
    
    List<MutualFund> findByCategoryAndRiskLevel(FundCategory category, RiskLevel riskLevel);
    
    List<MutualFund> findBySipAvailableTrue();
    
    List<MutualFund> findByIsActiveTrue();
    
    List<MutualFund> findByFundNameContainingIgnoreCase(String name);
    
    boolean existsByFundCode(String fundCode);
}
