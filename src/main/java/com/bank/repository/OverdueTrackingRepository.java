package com.bank.repository;

import com.bank.model.OverdueTracking;
import com.bank.model.OverdueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OverdueTrackingRepository extends JpaRepository<OverdueTracking, Long> {
    
    List<OverdueTracking> findByLoanId(Long loanId);
    
    List<OverdueTracking> findByCustomerId(Long customerId);
    
    List<OverdueTracking> findByIsResolvedFalse();
    
    List<OverdueTracking> findByCustomerIdAndIsResolvedFalse(Long customerId);
    
    List<OverdueTracking> findByOverdueStatus(OverdueStatus overdueStatus);
    
    @Query("SELECT o FROM OverdueTracking o WHERE o.loan.id = :loanId AND o.isResolved = false ORDER BY o.daysOverdue DESC")
    List<OverdueTracking> findActiveOverduesByLoanId(@Param("loanId") Long loanId);
    
    @Query("SELECT SUM(o.totalOverdueAmount) FROM OverdueTracking o WHERE o.customer.id = :customerId AND o.isResolved = false")
    Double getTotalOverdueAmount(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(o) FROM OverdueTracking o WHERE o.customer.id = :customerId AND o.isResolved = false")
    Long countActiveOverdues(@Param("customerId") Long customerId);
}
