package com.bank.repository;

import com.bank.model.MilestoneReward;
import com.bank.model.MilestoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRewardRepository extends JpaRepository<MilestoneReward, Long> {
    
    List<MilestoneReward> findByCustomerId(Long customerId);
    
    Optional<MilestoneReward> findByCustomerIdAndMilestoneType(Long customerId, MilestoneType milestoneType);
    
    List<MilestoneReward> findByMilestoneType(MilestoneType milestoneType);
    
    List<MilestoneReward> findByIsCreditedFalse();
    
    @Query("SELECT SUM(m.bonusPoints) FROM MilestoneReward m WHERE m.customer.id = :customerId AND m.isCredited = true")
    Integer getTotalMilestonePoints(@Param("customerId") Long customerId);
    
    @Query("SELECT m FROM MilestoneReward m WHERE m.customer.id = :customerId ORDER BY m.achievedDate DESC")
    List<MilestoneReward> findByCustomerIdOrderByAchievedDateDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(m) FROM MilestoneReward m WHERE m.customer.id = :customerId")
    Long countMilestonesAchieved(@Param("customerId") Long customerId);
}
