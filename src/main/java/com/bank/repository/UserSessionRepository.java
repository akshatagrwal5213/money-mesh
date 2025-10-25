package com.bank.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.AppUser;
import com.bank.model.UserSession;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionToken(String sessionToken);
    List<UserSession> findByUserAndActiveTrue(AppUser user);
    List<UserSession> findByUserAndActiveTrueOrderByLastActivityAtDesc(AppUser user);
    void deleteByUser(AppUser user);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
    long countByUserAndActiveTrue(AppUser user);
}
