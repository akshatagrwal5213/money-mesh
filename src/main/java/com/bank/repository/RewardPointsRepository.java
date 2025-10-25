package com.bank.repository;

import com.bank.model.RewardPoints;
import com.bank.model.RewardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RewardPointsRepository extends JpaRepository<RewardPoints, Long> {
    
    List<RewardPoints> findByCustomerId(Long customerId);
    
    List<RewardPoints> findByCustomerIdAndIsExpiredFalse(Long customerId);
    
    List<RewardPoints> findByCustomerIdAndIsRedeemedFalse(Long customerId);
    
    @Query("SELECT SUM(r.points) FROM RewardPoints r WHERE r.customer.id = :customerId")
    Integer getTotalPoints(@Param("customerId") Long customerId);
    
    @Query("SELECT SUM(r.points) FROM RewardPoints r WHERE r.customer.id = :customerId AND r.isExpired = false AND r.isRedeemed = false")
    Integer getActivePoints(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM RewardPoints r WHERE r.expiryDate < :currentDate AND r.isExpired = false")
    List<RewardPoints> findExpiredPoints(@Param("currentDate") LocalDate currentDate);
    
    List<RewardPoints> findByCategory(RewardCategory category);
    
    @Query("SELECT r FROM RewardPoints r WHERE r.customer.id = :customerId ORDER BY r.earnedDate DESC")
    List<RewardPoints> findByCustomerIdOrderByEarnedDateDesc(@Param("customerId") Long customerId);
}
