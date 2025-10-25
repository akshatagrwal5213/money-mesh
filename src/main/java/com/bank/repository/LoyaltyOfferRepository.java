package com.bank.repository;

import com.bank.model.LoyaltyOffer;
import com.bank.model.CustomerTierLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoyaltyOfferRepository extends JpaRepository<LoyaltyOffer, Long> {
    
    List<LoyaltyOffer> findByCustomerId(Long customerId);
    
    List<LoyaltyOffer> findByTierLevel(CustomerTierLevel tierLevel);
    
    List<LoyaltyOffer> findByIsActiveTrueAndIsUsedFalse();
    
    @Query("SELECT o FROM LoyaltyOffer o WHERE o.customer.id = :customerId AND o.isActive = true AND o.isUsed = false AND o.validTill >= :currentDate")
    List<LoyaltyOffer> findActiveOffers(@Param("customerId") Long customerId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT o FROM LoyaltyOffer o WHERE (o.tierLevel = :tierLevel OR o.tierLevel IS NULL) AND o.isActive = true AND o.validTill >= :currentDate")
    List<LoyaltyOffer> findOffersByTier(@Param("tierLevel") CustomerTierLevel tierLevel, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT o FROM LoyaltyOffer o WHERE o.validTill < :currentDate AND o.isActive = true")
    List<LoyaltyOffer> findExpiredOffers(@Param("currentDate") LocalDate currentDate);
}
