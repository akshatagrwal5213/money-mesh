package com.bank.repository;

import com.bank.model.ReferralBonus;
import com.bank.model.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralBonusRepository extends JpaRepository<ReferralBonus, Long> {
    
    List<ReferralBonus> findByReferrerId(Long referrerId);
    
    List<ReferralBonus> findByStatus(ReferralStatus status);
    
    Optional<ReferralBonus> findByReferralCode(String referralCode);
    
    List<ReferralBonus> findByReferrerIdAndBonusCreditedTrue(Long referrerId);
    
    @Query("SELECT COUNT(r) FROM ReferralBonus r WHERE r.referrer.id = :referrerId AND r.bonusCredited = true")
    Long countSuccessfulReferrals(@Param("referrerId") Long referrerId);
    
    @Query("SELECT SUM(r.bonusPoints) FROM ReferralBonus r WHERE r.referrer.id = :referrerId AND r.bonusCredited = true")
    Integer getTotalReferralPoints(@Param("referrerId") Long referrerId);
    
    @Query("SELECT r FROM ReferralBonus r WHERE r.referrer.id = :referrerId ORDER BY r.referralDate DESC")
    List<ReferralBonus> findByReferrerIdOrderByReferralDateDesc(@Param("referrerId") Long referrerId);
}
