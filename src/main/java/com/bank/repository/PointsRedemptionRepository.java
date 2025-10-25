package com.bank.repository;

import com.bank.model.PointsRedemption;
import com.bank.model.RedemptionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PointsRedemptionRepository extends JpaRepository<PointsRedemption, Long> {
    
    List<PointsRedemption> findByCustomerId(Long customerId);
    
    List<PointsRedemption> findByRedemptionType(RedemptionType redemptionType);
    
    List<PointsRedemption> findByStatus(String status);
    
    @Query("SELECT SUM(p.pointsRedeemed) FROM PointsRedemption p WHERE p.customer.id = :customerId")
    Integer getTotalPointsRedeemed(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM PointsRedemption p WHERE p.customer.id = :customerId ORDER BY p.redemptionDate DESC")
    List<PointsRedemption> findByCustomerIdOrderByRedemptionDateDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM PointsRedemption p WHERE p.redemptionDate BETWEEN :startDate AND :endDate")
    List<PointsRedemption> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
