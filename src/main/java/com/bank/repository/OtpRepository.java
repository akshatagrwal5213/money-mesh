package com.bank.repository;

import com.bank.model.OtpVerification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository
extends JpaRepository<OtpVerification, Long> {
    public Optional<OtpVerification> findByEmailAndOtpCodeAndOtpTypeAndVerifiedFalse(String var1, String var2, OtpVerification.OtpType var3);

    public Optional<OtpVerification> findFirstByEmailAndOtpTypeAndVerifiedFalseOrderByCreatedAtDesc(String var1, OtpVerification.OtpType var2);

    public List<OtpVerification> findByEmailAndOtpTypeAndVerifiedFalse(String var1, OtpVerification.OtpType var2);

    public void deleteByExpiresAtBefore(LocalDateTime var1);

    public void deleteByEmailAndOtpType(String var1, OtpVerification.OtpType var2);
}
