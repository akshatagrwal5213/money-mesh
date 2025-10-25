package com.bank.repository;

import com.bank.model.LoanRestructure;
import com.bank.model.RestructureReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRestructureRepository extends JpaRepository<LoanRestructure, Long> {
    
    List<LoanRestructure> findByLoanId(Long loanId);
    
    List<LoanRestructure> findByCustomerId(Long customerId);
    
    List<LoanRestructure> findByIsApprovedFalse();
    
    List<LoanRestructure> findByIsApprovedTrueAndIsImplementedFalse();
    
    List<LoanRestructure> findByCustomerIdAndIsApprovedTrue(Long customerId);
    
    List<LoanRestructure> findByReason(RestructureReason reason);
    
    @Query("SELECT r FROM LoanRestructure r WHERE r.loan.id = :loanId ORDER BY r.requestDate DESC")
    List<LoanRestructure> findByLoanIdOrderByRequestDateDesc(@Param("loanId") Long loanId);
}
