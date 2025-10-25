package com.bank.repository;

import com.bank.model.CreditDispute;
import com.bank.model.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditDisputeRepository extends JpaRepository<CreditDispute, Long> {
    
    // Find all disputes for a customer
    List<CreditDispute> findByCustomerId(Long customerId);
    
    // Find disputes by customer and status
    List<CreditDispute> findByCustomerIdAndStatus(Long customerId, DisputeStatus status);
    
    // Find pending disputes
    @Query("SELECT cd FROM CreditDispute cd WHERE cd.customer.id = :customerId AND cd.status IN ('PENDING', 'INVESTIGATING') ORDER BY cd.disputeDate DESC")
    List<CreditDispute> findPendingDisputes(@Param("customerId") Long customerId);
    
    // Find resolved disputes
    @Query("SELECT cd FROM CreditDispute cd WHERE cd.customer.id = :customerId AND cd.status IN ('RESOLVED', 'REJECTED') ORDER BY cd.resolvedDate DESC")
    List<CreditDispute> findResolvedDisputes(@Param("customerId") Long customerId);
    
    // Find disputes ordered by date
    List<CreditDispute> findByCustomerIdOrderByDisputeDateDesc(Long customerId);
    
    // Count pending disputes
    @Query("SELECT COUNT(cd) FROM CreditDispute cd WHERE cd.customer.id = :customerId AND cd.status IN ('PENDING', 'INVESTIGATING')")
    Long countPendingDisputes(@Param("customerId") Long customerId);
}
