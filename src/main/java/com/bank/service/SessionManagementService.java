package com.bank.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.model.AppUser;
import com.bank.model.UserSession;
import com.bank.repository.UserSessionRepository;

@Service
public class SessionManagementService {

    @Autowired
    private UserSessionRepository sessionRepository;

    private static final int SESSION_TIMEOUT_MINUTES = 30;
    private static final int MAX_CONCURRENT_SESSIONS = 3;

    @Transactional
    public UserSession createSession(AppUser user, String ipAddress, String userAgent) {
        // Check for concurrent session limit
        long activeSessions = sessionRepository.countByUserAndActiveTrue(user);
        if (activeSessions >= MAX_CONCURRENT_SESSIONS) {
            // Deactivate oldest session
            List<UserSession> sessions = sessionRepository.findByUserAndActiveTrueOrderByLastActivityAtDesc(user);
            if (!sessions.isEmpty()) {
                UserSession oldest = sessions.get(sessions.size() - 1);
                oldest.setActive(false);
                sessionRepository.save(oldest);
            }
        }

        // Create new session
        UserSession session = new UserSession();
        session.setUser(user);
        session.setSessionToken(UUID.randomUUID().toString());
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES));
        
        return sessionRepository.save(session);
    }

    @Transactional
    public boolean updateSessionActivity(String sessionToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            if (session.isActive() && !session.isExpired()) {
                session.setLastActivityAt(LocalDateTime.now());
                session.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_TIMEOUT_MINUTES));
                sessionRepository.save(session);
                return true;
            } else {
                session.setActive(false);
                sessionRepository.save(session);
            }
        }
        return false;
    }

    @Transactional
    public void invalidateSession(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken).ifPresent(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void invalidateAllUserSessions(AppUser user) {
        List<UserSession> sessions = sessionRepository.findByUserAndActiveTrue(user);
        sessions.forEach(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    public List<UserSession> getActiveSessions(AppUser user) {
        return sessionRepository.findByUserAndActiveTrueOrderByLastActivityAtDesc(user);
    }

    public boolean isSessionValid(String sessionToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionToken(sessionToken);
        return sessionOpt.isPresent() && 
               sessionOpt.get().isActive() && 
               !sessionOpt.get().isExpired();
    }

    /**
     * Scheduled task to clean up expired sessions (runs every hour)
     */
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredSessions() {
        sessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
