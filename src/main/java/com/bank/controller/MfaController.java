package com.bank.controller;

import com.bank.model.AppUser;
import com.bank.model.MfaMethod;
import com.bank.model.MfaSettings;
import com.bank.repository.AppUserRepository;
import com.bank.service.MfaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mfa")
public class MfaController {

    @Autowired
    private MfaService mfaService;

    @Autowired
    private AppUserRepository userRepository;

    /**
     * Get MFA settings for current user
     */
    @GetMapping("/settings")
    public ResponseEntity<?> getMfaSettings(Principal principal) {
        try {
            AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            MfaSettings settings = mfaService.getMfaSettings(user).orElse(null);
            
            Map<String, Object> response = new HashMap<>();
            if (settings != null) {
                response.put("enabled", settings.isMfaEnabled());
                response.put("method", settings.getPreferredMethod());
                response.put("email", settings.getBackupEmail());
                response.put("phone", settings.getPhoneNumber() != null ? maskPhone(settings.getPhoneNumber()) : null);
            } else {
                response.put("enabled", false);
                response.put("method", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Enable MFA for current user
     */
    @PostMapping("/enable")
    public ResponseEntity<?> enableMfa(
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        try {
            AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String methodStr = request.get("method");
            String contactInfo = request.get("contactInfo");

            if (methodStr == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "MFA method is required"));
            }

            MfaMethod method = MfaMethod.valueOf(methodStr.toUpperCase());
            
            MfaSettings settings = mfaService.enableMfa(user, method, contactInfo);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "MFA enabled successfully");
            response.put("method", settings.getPreferredMethod());

            // If TOTP, return QR code data
            if (method == MfaMethod.TOTP) {
                String qrData = mfaService.generateTotpQrData(user);
                response.put("qrData", qrData);
                response.put("secret", settings.getMfaSecret());
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid MFA method"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Disable MFA for current user
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disableMfa(Principal principal) {
        try {
            AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            mfaService.disableMfa(user);

            return ResponseEntity.ok(Map.of("message", "MFA disabled successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Send MFA code to user
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendMfaCode(Principal principal) {
        try {
            AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            mfaService.sendMfaCode(user, "Login verification");

            return ResponseEntity.ok(Map.of(
                "message", "MFA code sent successfully",
                "sentTo", maskContactInfo(user)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verify MFA code
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyMfaCode(
            @RequestBody Map<String, String> request,
            Principal principal
    ) {
        try {
            AppUser user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

            String code = request.get("code");
            if (code == null || code.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "MFA code is required"));
            }

            boolean valid = mfaService.verifyMfaCode(user, code);

            if (valid) {
                return ResponseEntity.ok(Map.of(
                    "message", "MFA verification successful",
                    "verified", true
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "Invalid or expired MFA code",
                        "verified", false
                    ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Helper method to mask phone number
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return "****";
        }
        return "****" + phone.substring(phone.length() - 4);
    }

    /**
     * Helper method to mask contact information
     */
    private String maskContactInfo(AppUser user) {
        MfaSettings settings = mfaService.getMfaSettings(user).orElse(null);
        if (settings == null) {
            return "email";
        }

        switch (settings.getPreferredMethod()) {
            case EMAIL:
                return "email";
            case SMS:
                return maskPhone(settings.getPhoneNumber());
            case TOTP:
                return "authenticator app";
            default:
                return "unknown";
        }
    }
}
