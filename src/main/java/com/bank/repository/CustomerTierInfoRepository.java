package com.bank.repository;

import com.bank.model.CustomerTierInfo;
import com.bank.model.CustomerTierLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerTierInfoRepository extends JpaRepository<CustomerTierInfo, Long> {
    
    Optional<CustomerTierInfo> findByCustomerId(Long customerId);
    
    List<CustomerTierInfo> findByTierLevel(CustomerTierLevel tierLevel);
    
    @Query("SELECT COUNT(c) FROM CustomerTierInfo c WHERE c.tierLevel = :tierLevel")
    Long countByTierLevel(@Param("tierLevel") CustomerTierLevel tierLevel);
    
    @Query("SELECT c FROM CustomerTierInfo c WHERE c.activePoints >= :threshold")
    List<CustomerTierInfo> findEligibleForUpgrade(@Param("threshold") Integer threshold);
}
