package com.bank.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.model.AppUser;
import com.bank.model.MfaSettings;

@Repository
public interface MfaSettingsRepository extends JpaRepository<MfaSettings, Long> {
    Optional<MfaSettings> findByUser(AppUser user);
    void deleteByUser(AppUser user);
}
