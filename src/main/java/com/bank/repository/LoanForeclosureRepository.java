package com.bank.repository;

import com.bank.model.LoanForeclosure;
import com.bank.model.ForeclosureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanForeclosureRepository extends JpaRepository<LoanForeclosure, Long> {
    
    Optional<LoanForeclosure> findByLoanId(Long loanId);
    
    List<LoanForeclosure> findByCustomerId(Long customerId);
    
    List<LoanForeclosure> findByStatus(ForeclosureStatus status);
    
    List<LoanForeclosure> findByStatusOrderByRequestDateAsc(ForeclosureStatus status);
    
    @Query("SELECT f FROM LoanForeclosure f WHERE f.customer.id = :customerId ORDER BY f.requestDate DESC")
    List<LoanForeclosure> findByCustomerIdOrderByRequestDateDesc(@Param("customerId") Long customerId);
    
    @Query("SELECT COUNT(f) FROM LoanForeclosure f WHERE f.status = :status")
    Long countByStatus(@Param("status") ForeclosureStatus status);
}
