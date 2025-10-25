package com.bank.repository;

import com.bank.model.LoanCollateral;
import com.bank.model.CollateralType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanCollateralRepository extends JpaRepository<LoanCollateral, Long> {
    
    List<LoanCollateral> findByLoanId(Long loanId);
    
    List<LoanCollateral> findByCollateralType(CollateralType collateralType);
    
    List<LoanCollateral> findByIsReleasedFalse();
    
    @Query("SELECT c FROM LoanCollateral c WHERE c.loan.id = :loanId AND c.isReleased = false")
    List<LoanCollateral> findActivateCollateralsByLoanId(@Param("loanId") Long loanId);
    
    @Query("SELECT SUM(c.estimatedValue) FROM LoanCollateral c WHERE c.loan.id = :loanId AND c.isReleased = false")
    Double getTotalCollateralValue(@Param("loanId") Long loanId);
}
