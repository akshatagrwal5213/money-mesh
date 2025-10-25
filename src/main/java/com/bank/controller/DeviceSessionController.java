package com.bank.controller;

import com.bank.model.AppUser;
import com.bank.model.DeviceSession;
import com.bank.repository.AppUserRepository;
import com.bank.repository.DeviceSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceSessionController {

    @Autowired
    private DeviceSessionRepository deviceSessionRepository;

    @Autowired
    private AppUserRepository userRepository;

    /**
     * Get all active device sessions for the authenticated user
     */
    @GetMapping
    public ResponseEntity<?> getActiveSessions(HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            List<DeviceSession> sessions = deviceSessionRepository
                .findByUserAndIsActiveTrueOrderByLastActivityTimeDesc(user);

            // Get current session token from request
            String currentToken = extractToken(request);

            List<Map<String, Object>> sessionData = sessions.stream()
                .map(session -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id", session.getId());
                    data.put("deviceName", session.getDeviceName());
                    data.put("ipAddress", session.getIpAddress());
                    data.put("location", session.getLocation());
                    data.put("loginTime", session.getLoginTime());
                    data.put("lastActivityTime", session.getLastActivityTime());
                    data.put("isCurrentDevice", session.getSessionToken().equals(currentToken));
                    return data;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(sessionData);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to fetch device sessions: " + e.getMessage()));
        }
    }

    /**
     * Revoke a specific device session
     */
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> revokeSession(@PathVariable Long sessionId, HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            DeviceSession session = deviceSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

            // Verify the session belongs to the authenticated user
            if (!session.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403)
                    .body(Map.of("message", "Unauthorized to revoke this session"));
            }

            // Check if trying to revoke current session
            String currentToken = extractToken(request);
            if (session.getSessionToken().equals(currentToken)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "Cannot revoke your current session. Please log out instead."));
            }

            // Revoke the session
            session.setIsActive(false);
            session.setRevokedAt(LocalDateTime.now());
            deviceSessionRepository.save(session);

            return ResponseEntity.ok(Map.of("message", "Device session revoked successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to revoke session: " + e.getMessage()));
        }
    }

    /**
     * Revoke all other device sessions (keep only current)
     */
    @PostMapping("/revoke-all")
    public ResponseEntity<?> revokeAllOtherSessions(HttpServletRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            String currentToken = extractToken(request);

            List<DeviceSession> sessions = deviceSessionRepository
                .findByUserAndIsActiveTrueOrderByLastActivityTimeDesc(user);

            int revokedCount = 0;
            for (DeviceSession session : sessions) {
                if (!session.getSessionToken().equals(currentToken)) {
                    session.setIsActive(false);
                    session.setRevokedAt(LocalDateTime.now());
                    deviceSessionRepository.save(session);
                    revokedCount++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "message", revokedCount + " device session(s) revoked successfully",
                "revokedCount", revokedCount
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("message", "Failed to revoke sessions: " + e.getMessage()));
        }
    }

    /**
     * Extract JWT token from request header
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return "";
    }
}
