package com.bank.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.model.AppUser;
import com.bank.model.AuditLog;
import com.bank.model.AuditStatus;
import com.bank.repository.AppUserRepository;
import com.bank.repository.AuditLogRepository;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Transactional
    public void logAction(AppUser user, String action, AuditStatus status, String ipAddress, String userAgent, String details) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setStatus(status);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }

    @Transactional
    public void logAction(AppUser user, String action, AuditStatus status, String ipAddress, String userAgent) {
        logAction(user, action, status, ipAddress, userAgent, null);
    }

    /**
     * Simple logging method for internal operations (username, action, details)
     */
    @Transactional
    public void logAction(String username, String action, String details) {
        AppUser user = userRepository.findByUsername(username).orElse(null);
        logAction(user, action, AuditStatus.SUCCESS, "system", "internal", details);
    }

    public List<AuditLog> getUserAuditLogs(AppUser user) {
        return auditLogRepository.findByUserOrderByTimestampDesc(user);
    }

    public List<AuditLog> getFailedLogins() {
        return auditLogRepository.findByUserAndActionOrderByTimestampDesc(null, "FAILED_LOGIN");
    }

    public long countRecentFailedLogins(AppUser user, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return auditLogRepository.countByUserAndActionAndTimestampAfter(user, "FAILED_LOGIN", since);
    }

    public boolean isAccountLocked(AppUser user) {
        // Lock account if more than 5 failed login attempts in the last 15 minutes
        return countRecentFailedLogins(user, 15) >= 5;
    }
}
