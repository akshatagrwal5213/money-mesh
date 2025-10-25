package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bank.model.ClaimStatus;
import com.bank.model.InsuranceClaim;

@Repository
public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    
    List<InsuranceClaim> findByPolicyId(Long policyId);
    
    List<InsuranceClaim> findByStatus(ClaimStatus status);
    
    Optional<InsuranceClaim> findByClaimNumber(String claimNumber);
    
    @Query("SELECT c FROM InsuranceClaim c WHERE c.policy.customer.id = :customerId")
    List<InsuranceClaim> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT c FROM InsuranceClaim c WHERE c.policy.customer.id = :customerId AND c.status = :status")
    List<InsuranceClaim> findByCustomerIdAndStatus(@Param("customerId") Long customerId, @Param("status") ClaimStatus status);
    
    @Query("SELECT COUNT(c) FROM InsuranceClaim c WHERE c.policy.id = :policyId AND c.status IN ('SUBMITTED', 'UNDER_REVIEW', 'PENDING_DOCUMENTS')")
    long countPendingClaimsByPolicy(@Param("policyId") Long policyId);
}
