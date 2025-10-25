package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.model.InsurancePolicy;
import com.bank.model.InsurancePolicyStatus;
import com.bank.model.InsuranceType;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    
    List<InsurancePolicy> findByCustomerId(Long customerId);
    
    List<InsurancePolicy> findByCustomerIdAndStatus(Long customerId, InsurancePolicyStatus status);
    
    List<InsurancePolicy> findByCustomerIdAndInsuranceType(Long customerId, InsuranceType insuranceType);
    
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    
    @Query("SELECT p FROM InsurancePolicy p WHERE p.customer.id = :customerId AND p.nextPremiumDueDate <= :dueDate AND p.status = 'ACTIVE'")
    List<InsurancePolicy> findUpcomingPremiums(@Param("customerId") Long customerId, @Param("dueDate") LocalDate dueDate);
    
    @Query("SELECT p FROM InsurancePolicy p WHERE p.status = 'ACTIVE' AND p.endDate <= :endDate")
    List<InsurancePolicy> findExpiringPolicies(@Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM InsurancePolicy p WHERE p.customer.id = :customerId AND p.status = 'ACTIVE'")
    long countActivePolicies(@Param("customerId") Long customerId);
}
