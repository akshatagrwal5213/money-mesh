package com.bank.service;

import com.bank.model.AppUser;
import com.bank.model.OtpVerification;
import com.bank.repository.OtpRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OtpService {
    @Autowired
    private OtpRepository otpRepository;
    @Autowired
    private EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private static final SecureRandom random = new SecureRandom();

    @Transactional
    public String generateAndSendOtp(String email, OtpVerification.OtpType otpType, AppUser user) {
        this.invalidateExistingOtps(email, otpType);
        String otpCode = this.generateOtpCode();
        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtpCode(otpCode);
        otpVerification.setOtpType(otpType);
        otpVerification.setUser(user);
    otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otpVerification.setVerified(false);
        otpVerification.setCreatedAt(LocalDateTime.now());
        this.otpRepository.save(otpVerification);
        this.emailService.sendOtpEmail(email, otpCode, otpType.name());
        return otpCode;
    }

    @Transactional
    public boolean verifyOtp(String email, String otpCode, OtpVerification.OtpType otpType) {
        Optional<OtpVerification> otpOpt = this.otpRepository.findByEmailAndOtpCodeAndOtpTypeAndVerifiedFalse(email, otpCode, otpType);
        if (otpOpt.isEmpty()) {
            return false;
        }
        OtpVerification otp = otpOpt.get();
        if (otp.isExpired()) {
            return false;
        }
        otp.setVerified(true);
        this.otpRepository.save(otp);
        return true;
    }

    public boolean isOtpValid(String email, String otpCode, OtpVerification.OtpType otpType) {
        Optional<OtpVerification> otpOpt = this.otpRepository.findByEmailAndOtpCodeAndOtpTypeAndVerifiedFalse(email, otpCode, otpType);
        if (otpOpt.isEmpty()) {
            return false;
        }
        return !otpOpt.get().isExpired();
    }

    @Transactional
    public void invalidateExistingOtps(String email, OtpVerification.OtpType otpType) {
        this.otpRepository.deleteByEmailAndOtpType(email, otpType);
    }

    private String generateOtpCode() {
        // Generate an OTP of length OTP_LENGTH (e.g. 6 digits)
        int min = (int) Math.pow(10, OTP_LENGTH - 1);
        int otp = min + random.nextInt(9 * min);
        return String.valueOf(otp);
    }

    @Transactional
    public void cleanupExpiredOtps() {
        this.otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Transactional
    public String resendOtp(String email, OtpVerification.OtpType otpType) {
        OtpVerification otp;
        Optional<OtpVerification> recentOtp = this.otpRepository.findFirstByEmailAndOtpTypeAndVerifiedFalseOrderByCreatedAtDesc(email, otpType);
        if (recentOtp.isPresent() && (otp = recentOtp.get()).getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1L))) {
            this.emailService.sendOtpEmail(email, otp.getOtpCode(), otpType.name());
            return "OTP resent successfully";
        }
        this.generateAndSendOtp(email, otpType, recentOtp.isPresent() ? recentOtp.get().getUser() : null);
        return "New OTP generated and sent successfully";
    }
}
