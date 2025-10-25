package com.bank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.model.InsurancePremiumPayment;

@Repository
public interface InsurancePremiumPaymentRepository extends JpaRepository<InsurancePremiumPayment, Long> {
    
    List<InsurancePremiumPayment> findByPolicyId(Long policyId);
    
    Optional<InsurancePremiumPayment> findByPaymentReference(String paymentReference);
    
    @Query("SELECT p FROM InsurancePremiumPayment p WHERE p.policy.customer.id = :customerId ORDER BY p.paymentDate DESC")
    List<InsurancePremiumPayment> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT p FROM InsurancePremiumPayment p WHERE p.policy.id = :policyId AND p.paymentDate BETWEEN :startDate AND :endDate")
    List<InsurancePremiumPayment> findByPolicyAndDateRange(
        @Param("policyId") Long policyId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    @Query("SELECT p FROM InsurancePremiumPayment p WHERE p.policy.id = :policyId ORDER BY p.paymentDate DESC")
    List<InsurancePremiumPayment> findLatestPaymentsByPolicy(@Param("policyId") Long policyId);
}
