package com.bank.repository;

import com.bank.model.QrCode;
import com.bank.model.QrCodeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {
    
    Optional<QrCode> findByQrCodeId(String qrCodeId);
    
    Page<QrCode> findByAccountId(Long accountId, Pageable pageable);
    
    List<QrCode> findByAccountIdAndType(Long accountId, QrCodeType type);
    
    List<QrCode> findByAccountIdAndIsActiveTrue(Long accountId);
    
    List<QrCode> findByExpiryDateBefore(LocalDateTime expiryDate);
    
    List<QrCode> findByIsActiveTrueAndExpiryDateAfter(LocalDateTime currentDate);
}
