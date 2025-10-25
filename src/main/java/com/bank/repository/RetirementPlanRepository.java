package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bank.model.RetirementPlan;

public interface RetirementPlanRepository extends JpaRepository<RetirementPlan, Long> {
    
    List<RetirementPlan> findByCustomerIdOrderByLastUpdatedDateDesc(Long customerId);
    
    @Query("SELECT r FROM RetirementPlan r WHERE r.customer.id = :customerId ORDER BY r.lastUpdatedDate DESC")
    Optional<RetirementPlan> findLatestByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT r FROM RetirementPlan r WHERE r.customer.user.username = :username ORDER BY r.lastUpdatedDate DESC")
    Optional<RetirementPlan> findLatestByUsername(@Param("username") String username);
}
