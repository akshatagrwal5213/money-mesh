package com.bank.controller;

import com.bank.model.AppUser;
import com.bank.model.AuditLog;
import com.bank.repository.AppUserRepository;
import com.bank.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditLogController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AppUserRepository userRepository;

    /**
     * Get login history for the authenticated user
     */
    @GetMapping("/login-history")
    public ResponseEntity<?> getLoginHistory() {
        try {
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            // Get user from database
            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Get all login-related audit logs for this user
            List<AuditLog> logs = auditLogRepository.findByUserAndActionOrderByTimestampDesc(user, "LOGIN");

            // Transform to DTOs (to avoid circular references and expose only needed data)
            List<Map<String, Object>> loginHistory = logs.stream()
                .map(log -> {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("id", log.getId());
                    logData.put("timestamp", log.getTimestamp());
                    logData.put("ipAddress", log.getIpAddress());
                    logData.put("userAgent", log.getUserAgent());
                    logData.put("status", log.getStatus());
                    logData.put("details", log.getDetails());
                    return logData;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(loginHistory);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to fetch login history: " + e.getMessage()));
        }
    }

    /**
     * Get all audit logs for the authenticated user (admin feature)
     */
    @GetMapping("/history")
    public ResponseEntity<?> getAuditHistory() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            List<AuditLog> logs = auditLogRepository.findByUserOrderByTimestampDesc(user);

            List<Map<String, Object>> auditHistory = logs.stream()
                .map(log -> {
                    Map<String, Object> logData = new HashMap<>();
                    logData.put("id", log.getId());
                    logData.put("action", log.getAction());
                    logData.put("timestamp", log.getTimestamp());
                    logData.put("ipAddress", log.getIpAddress());
                    logData.put("userAgent", log.getUserAgent());
                    logData.put("status", log.getStatus());
                    logData.put("details", log.getDetails());
                    return logData;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(auditHistory);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to fetch audit history: " + e.getMessage()));
        }
    }
}
