package com.bank.controller;

import com.bank.model.AppUser;
import com.bank.model.AuditLog;
import com.bank.model.AuditStatus;
import com.bank.model.DeviceSession;
import com.bank.repository.AppUserRepository;
import com.bank.repository.AuditLogRepository;
import com.bank.repository.DeviceSessionRepository;
import com.bank.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/auth"})
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AppUserRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @PostMapping(value={"/register"})
    public ResponseEntity<?> register(@RequestBody AppUser user) {
        user.setPassword(this.passwordEncoder.encode((CharSequence)user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("ROLE_CUSTOMER");
        }
        AppUser saved = (AppUser)this.repo.save(user);
        return ResponseEntity.ok(Map.of("id", saved.getId(), "username", saved.getUsername()));
    }

    @PostMapping(value={"/login"})
    public ResponseEntity<?> login(@RequestBody AppUser user, HttpServletRequest request) {
        try {
            this.authManager.authenticate((Authentication)new UsernamePasswordAuthenticationToken((Object)user.getUsername(), (Object)user.getPassword()));
            AppUser u = this.repo.findByUsername(user.getUsername()).orElse(null);
            String token = this.jwtUtil.generateToken(u.getUsername());
            String role = u.getRole();
            String displayRole = role != null && role.startsWith("ROLE_") ? role.substring(5) : role;
            
            // Log successful login
            logAudit(u, "LOGIN", request, AuditStatus.SUCCESS, "User logged in successfully");
            
            // Create device session
            createDeviceSession(u, token, request);
            
            return ResponseEntity.ok(Map.of("token", token, "user", Map.of("id", u.getId(), "username", u.getUsername(), "roles", new String[]{displayRole})));
        } catch (AuthenticationException e) {
            // Log failed login attempt
            AppUser u = this.repo.findByUsername(user.getUsername()).orElse(null);
            if (u != null) {
                logAudit(u, "LOGIN", request, AuditStatus.FAILURE, "Failed login attempt: " + e.getMessage());
            }
            throw e;
        }
    }
    
    private void logAudit(AppUser user, String action, HttpServletRequest request, AuditStatus status, String details) {
        try {
            AuditLog log = new AuditLog();
            log.setUser(user);
            log.setAction(action);
            log.setTimestamp(LocalDateTime.now());
            log.setIpAddress(getClientIP(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            log.setStatus(status);
            log.setDetails(details);
            auditLogRepository.save(log);
        } catch (Exception e) {
            // Don't fail the request if audit logging fails
            logger.error("Failed to log audit: {}", e.getMessage(), e);
        }
    }
    
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    private void createDeviceSession(AppUser user, String token, HttpServletRequest request) {
        try {
            String userAgent = request.getHeader("User-Agent");
            String deviceName = parseDeviceName(userAgent);
            
            logger.debug("Creating device session for user: {}", user.getUsername());
            logger.debug("Device: {}", deviceName);
            logger.debug("IP: {}", getClientIP(request));
            
            DeviceSession session = new DeviceSession();
            session.setUser(user);
            session.setSessionToken(token);
            session.setDeviceName(deviceName);
            session.setIpAddress(getClientIP(request));
            session.setUserAgent(userAgent);
            session.setLoginTime(LocalDateTime.now());
            session.setLastActivityTime(LocalDateTime.now());
            session.setIsActive(true);
            
            DeviceSession saved = deviceSessionRepository.save(session);
            logger.debug("Device session created with ID: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Failed to create device session: {}", e.getMessage(), e);
        }
    }
    
    private String parseDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return "Unknown Device";
        }
        
        String browser = "Unknown";
        String os = "Unknown";
        
        // Parse browser
        if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            browser = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            browser = "Safari";
        } else if (userAgent.contains("Edg")) {
            browser = "Edge";
        } else if (userAgent.contains("Opera")) {
            browser = "Opera";
        }
        
        // Parse OS
        if (userAgent.contains("Windows")) {
            os = "Windows";
        } else if (userAgent.contains("Mac OS X") || userAgent.contains("Macintosh")) {
            os = "macOS";
        } else if (userAgent.contains("Linux")) {
            os = "Linux";
        } else if (userAgent.contains("Android")) {
            os = "Android";
        } else if (userAgent.contains("iOS") || userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            os = "iOS";
        }
        
        return browser + " on " + os;
    }
    
    @PostMapping(value={"/change-password"})
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        try {
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            // Validate input
            if (currentPassword == null || currentPassword.isEmpty() || 
                newPassword == null || newPassword.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Current password and new password are required"));
            }
            
            // Get user from database
            AppUser user = this.repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify current password
            if (!this.passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Current password is incorrect"));
            }
            
            // Validate new password strength
            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "New password must be at least 6 characters long"));
            }
            
            // Update password
            user.setPassword(this.passwordEncoder.encode(newPassword));
            this.repo.save(user);
            
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to change password: " + e.getMessage()));
        }
    }
}
