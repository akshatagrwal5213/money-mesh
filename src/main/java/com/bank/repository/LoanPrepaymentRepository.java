package com.bank.repository;

import com.bank.model.LoanPrepayment;
import com.bank.model.PrepaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanPrepaymentRepository extends JpaRepository<LoanPrepayment, Long> {
    
    List<LoanPrepayment> findByLoanId(Long loanId);
    
    List<LoanPrepayment> findByCustomerId(Long customerId);
    
    List<LoanPrepayment> findByLoanIdOrderByPrepaymentDateDesc(Long loanId);
    
    @Query("SELECT SUM(p.prepaymentAmount) FROM LoanPrepayment p WHERE p.loan.id = :loanId")
    Double getTotalPrepayments(@Param("loanId") Long loanId);
    
    @Query("SELECT SUM(p.interestSaved) FROM LoanPrepayment p WHERE p.loan.id = :loanId")
    Double getTotalInterestSaved(@Param("loanId") Long loanId);
    
    List<LoanPrepayment> findByPrepaymentType(PrepaymentType prepaymentType);
}
