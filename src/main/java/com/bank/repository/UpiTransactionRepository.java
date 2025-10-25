package com.bank.repository;

import com.bank.model.UpiTransaction;
import com.bank.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UpiTransactionRepository extends JpaRepository<UpiTransaction, Long> {
    
    Optional<UpiTransaction> findByUpiTransactionId(String upiTransactionId);
    
    Page<UpiTransaction> findByAccountId(Long accountId, Pageable pageable);
    
    List<UpiTransaction> findByAccountIdAndStatus(Long accountId, PaymentStatus status);
    
    Page<UpiTransaction> findByAccountIdAndTransactionDateBetween(
        Long accountId, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    List<UpiTransaction> findByUpiId(String upiId);
    
    List<UpiTransaction> findByStatus(PaymentStatus status);
}
