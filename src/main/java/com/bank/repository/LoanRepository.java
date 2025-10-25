package com.bank.repository;

import com.bank.model.Customer;
import com.bank.model.Loan;
import com.bank.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    Optional<Loan> findByLoanNumber(String loanNumber);
    
    List<Loan> findByCustomer(Customer customer);
    
    List<Loan> findByCustomerAndStatus(Customer customer, LoanStatus status);
    
    List<Loan> findByStatus(LoanStatus status);
}
