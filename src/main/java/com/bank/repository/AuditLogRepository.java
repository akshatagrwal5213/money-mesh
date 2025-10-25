package com.bank.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.AppUser;
import com.bank.model.AuditLog;
import com.bank.model.AuditStatus;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserOrderByTimestampDesc(AppUser user);
    List<AuditLog> findByActionAndTimestampAfter(String action, LocalDateTime timestamp);
    List<AuditLog> findByUserAndActionOrderByTimestampDesc(AppUser user, String action);
    List<AuditLog> findByStatusOrderByTimestampDesc(AuditStatus status);
    long countByUserAndActionAndTimestampAfter(AppUser user, String action, LocalDateTime timestamp);
}
