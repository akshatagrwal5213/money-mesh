package com.bank.repository;

import com.bank.model.Loan;
import com.bank.model.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {
    
    List<LoanRepayment> findByLoan(Loan loan);
    
    List<LoanRepayment> findByLoanOrderByPaymentDateDesc(Loan loan);
}
