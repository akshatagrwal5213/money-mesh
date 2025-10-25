package com.bank.repository;

import com.bank.model.AppUser;
import com.bank.model.DeviceSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceSessionRepository extends JpaRepository<DeviceSession, Long> {
    List<DeviceSession> findByUserAndIsActiveTrueOrderByLastActivityTimeDesc(AppUser user);
    Optional<DeviceSession> findBySessionToken(String sessionToken);
    List<DeviceSession> findByUserOrderByLastActivityTimeDesc(AppUser user);
    long countByUserAndIsActiveTrue(AppUser user);
}
