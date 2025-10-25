package com.bank.repository;

import com.bank.model.EligibilityStatus;
import com.bank.model.LoanEligibility;
import com.bank.model.LoanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanEligibilityRepository extends JpaRepository<LoanEligibility, Long> {
    
    // Find all eligibilities for a customer
    List<LoanEligibility> findByCustomerId(Long customerId);
    
    // Find latest eligibility for a customer and loan type
    @Query("SELECT le FROM LoanEligibility le WHERE le.customer.id = :customerId AND le.loanType = :loanType ORDER BY le.checkDate DESC LIMIT 1")
    Optional<LoanEligibility> findLatestByCustomerIdAndLoanType(
        @Param("customerId") Long customerId,
        @Param("loanType") LoanType loanType
    );
    
    // Find eligible loans for a customer
    @Query("SELECT le FROM LoanEligibility le WHERE le.customer.id = :customerId AND le.eligible = true AND le.expiryDate >= :currentDate ORDER BY le.checkDate DESC")
    List<LoanEligibility> findEligibleLoans(
        @Param("customerId") Long customerId,
        @Param("currentDate") LocalDate currentDate
    );
    
    // Find all active (non-expired) eligibilities
    @Query("SELECT le FROM LoanEligibility le WHERE le.customer.id = :customerId AND le.expiryDate >= :currentDate ORDER BY le.checkDate DESC")
    List<LoanEligibility> findActiveEligibilities(
        @Param("customerId") Long customerId,
        @Param("currentDate") LocalDate currentDate
    );
    
    // Find by customer and eligibility status
    List<LoanEligibility> findByCustomerIdAndEligibilityStatus(Long customerId, EligibilityStatus status);
    
    // Find eligibilities ordered by date
    List<LoanEligibility> findByCustomerIdOrderByCheckDateDesc(Long customerId);
}
