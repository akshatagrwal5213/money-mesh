package com.bank.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.model.AppUser;
import com.bank.model.MfaMethod;
import com.bank.model.MfaSettings;
import com.bank.model.OtpVerification;
import com.bank.repository.MfaSettingsRepository;

@Service
public class MfaService {

    @Autowired
    private MfaSettingsRepository mfaSettingsRepository;

    @Autowired
    private OtpService otpService;

    /**
     * Enable MFA for a user
     */
    @Transactional
    public MfaSettings enableMfa(AppUser user, MfaMethod method, String contactInfo) {
        MfaSettings settings = mfaSettingsRepository.findByUser(user)
            .orElse(new MfaSettings());
        
        settings.setUser(user);
        settings.setMfaEnabled(true);
        settings.setPreferredMethod(method);
        settings.setLastModifiedAt(LocalDateTime.now());

        if (method == MfaMethod.EMAIL) {
            settings.setBackupEmail(contactInfo);
        } else if (method == MfaMethod.SMS) {
            settings.setPhoneNumber(contactInfo);
        } else if (method == MfaMethod.TOTP) {
            // Generate secret for TOTP
            settings.setMfaSecret(generateTotpSecret());
        }

        return mfaSettingsRepository.save(settings);
    }

    /**
     * Disable MFA for a user
     */
    @Transactional
    public void disableMfa(AppUser user) {
        mfaSettingsRepository.findByUser(user).ifPresent(settings -> {
            settings.setMfaEnabled(false);
            settings.setLastModifiedAt(LocalDateTime.now());
            mfaSettingsRepository.save(settings);
        });
    }

    /**
     * Get MFA settings for a user
     */
    public Optional<MfaSettings> getMfaSettings(AppUser user) {
        return mfaSettingsRepository.findByUser(user);
    }

    /**
     * Check if MFA is enabled for a user
     */
    public boolean isMfaEnabled(AppUser user) {
        return mfaSettingsRepository.findByUser(user)
            .map(MfaSettings::isMfaEnabled)
            .orElse(false);
    }

    /**
     * Send MFA code to user
     */
    public void sendMfaCode(AppUser user, String purpose) throws Exception {
        MfaSettings settings = mfaSettingsRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("MFA not configured"));

        if (!settings.isMfaEnabled()) {
            throw new RuntimeException("MFA is not enabled");
        }

        String contact;
        OtpVerification.OtpType otpType;

        switch (settings.getPreferredMethod()) {
            case EMAIL:
                contact = settings.getBackupEmail();
                if (contact == null) {
                    contact = user.getUsername() + "@moneymesh.com";
                }
                otpType = OtpVerification.OtpType.MFA_EMAIL;
                break;
            case SMS:
                contact = settings.getPhoneNumber();
                if (contact == null) {
                    throw new RuntimeException("Phone number not configured");
                }
                otpType = OtpVerification.OtpType.MFA_SMS;
                break;
            case TOTP:
                // For TOTP, user needs to enter code from authenticator app
                // No need to send anything
                return;
            default:
                throw new RuntimeException("Unsupported MFA method");
        }

        // Generate and send OTP
        otpService.generateAndSendOtp(contact, otpType, user);
    }

    /**
     * Verify MFA code
     */
    public boolean verifyMfaCode(AppUser user, String code) {
        MfaSettings settings = mfaSettingsRepository.findByUser(user)
            .orElse(null);

        if (settings == null || !settings.isMfaEnabled()) {
            return false;
        }

        switch (settings.getPreferredMethod()) {
            case EMAIL:
                String email = settings.getBackupEmail();
                if (email == null) {
                    email = user.getUsername() + "@moneymesh.com";
                }
                return otpService.verifyOtp(email, code, OtpVerification.OtpType.MFA_EMAIL);
            
            case SMS:
                String phone = settings.getPhoneNumber();
                if (phone == null) {
                    return false;
                }
                return otpService.verifyOtp(phone, code, OtpVerification.OtpType.MFA_SMS);
            
            case TOTP:
                // Verify TOTP code
                return verifyTotpCode(settings.getMfaSecret(), code);
            
            default:
                return false;
        }
    }

    /**
     * Generate TOTP secret
     */
    private String generateTotpSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Verify TOTP code (basic implementation)
     * In production, use a library like GoogleAuth or Commons-Codec
     */
    private boolean verifyTotpCode(String secret, String code) {
        // This is a simplified implementation
        // In production, use a proper TOTP library
        if (secret == null || code == null) {
            return false;
        }
        
        // For now, we'll just check if the code matches a simple hash
        // TODO: Implement proper TOTP algorithm (RFC 6238)
        return code.length() == 6 && code.matches("\\d{6}");
    }

    /**
     * Generate QR code data for TOTP setup
     */
    public String generateTotpQrData(AppUser user) {
        MfaSettings settings = mfaSettingsRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("MFA not configured"));

        if (settings.getMfaSecret() == null) {
            throw new RuntimeException("TOTP secret not generated");
        }

        // Generate otpauth:// URL for QR code
        String issuer = "MoneyMesh";
        String accountName = user.getUsername();
        
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer,
            accountName,
            settings.getMfaSecret(),
            issuer
        );
    }
}
