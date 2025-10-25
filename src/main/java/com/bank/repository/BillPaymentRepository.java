package com.bank.repository;

import com.bank.model.BillPayment;
import com.bank.model.BillType;
import com.bank.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {
    
    Optional<BillPayment> findByBillPaymentId(String billPaymentId);
    
    Page<BillPayment> findByAccountId(Long accountId, Pageable pageable);
    
    List<BillPayment> findByAccountIdAndBillType(Long accountId, BillType billType);
    
    List<BillPayment> findByAccountIdAndPaymentStatus(Long accountId, PaymentStatus status);
    
    Page<BillPayment> findByAccountIdAndPaymentDateBetween(
        Long accountId, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    List<BillPayment> findByDueDateBefore(LocalDate dueDate);
    
    List<BillPayment> findByAccountIdAndIsAutoPayEnabledTrue(Long accountId);
    
    List<BillPayment> findByConsumerNumberAndBillerCode(String consumerNumber, String billerCode);
}
