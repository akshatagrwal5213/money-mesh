package com.bank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.model.InvestmentRecommendation;
import com.bank.model.RecommendationType;

public interface InvestmentRecommendationRepository extends JpaRepository<InvestmentRecommendation, Long> {
    
    List<InvestmentRecommendation> findByCustomerIdAndIsActiveTrueOrderByPriorityAsc(Long customerId);
    
    List<InvestmentRecommendation> findByCustomerIdAndRecommendationTypeAndIsActiveTrue(
        Long customerId, 
        RecommendationType type
    );
    
    @Query("SELECT r FROM InvestmentRecommendation r WHERE r.customer.id = :customerId AND r.isActive = true AND r.isImplemented = false ORDER BY r.priority, r.potentialImpact DESC")
    List<InvestmentRecommendation> findActiveRecommendations(@Param("customerId") Long customerId);
}
