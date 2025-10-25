package com.bank.controller;

import com.bank.model.AppUser;
import com.bank.model.OtpVerification;
import com.bank.repository.AppUserRepository;
import com.bank.service.OtpService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value={"/api/auth"})
@CrossOrigin(origins={"*"})
public class PasswordResetController {
    @Autowired
    private AppUserRepository userRepository;
    @Autowired
    private OtpService otpService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping(value={"/forgot-password"})
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        AppUser user = this.userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(Map.of("message", "If this username exists, an OTP has been sent to the registered email", "success", true));
        }
        String email = username + "@moneymesh.com";
        try {
            this.otpService.generateAndSendOtp(email, OtpVerification.OtpType.RESET_PASSWORD, user);
            HashMap<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP has been sent to your registered email");
            response.put("email", this.maskEmail(email));
            response.put("username", username);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping(value={"/verify-reset-otp"})
    public ResponseEntity<?> verifyResetOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otpCode = request.get("otp");
        if (username == null || otpCode == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username and OTP are required"));
        }
        String email = username + "@moneymesh.com";
        boolean isValid = this.otpService.verifyOtp(email, otpCode, OtpVerification.OtpType.RESET_PASSWORD);
        if (isValid) {
            return ResponseEntity.ok(Map.of("success", true, "message", "OTP verified successfully", "username", username));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
    }

    @PostMapping(value={"/reset-password"})
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String otpCode = request.get("otp");
        String newPassword = request.get("newPassword");
        if (username == null || otpCode == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username, OTP, and new password are required"));
        }
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters long"));
        }
        String email = username + "@moneymesh.com";
        boolean isValid = this.otpService.isOtpValid(email, otpCode, OtpVerification.OtpType.RESET_PASSWORD);
        if (!isValid) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired OTP"));
        }
        AppUser user = this.userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        user.setPassword(this.passwordEncoder.encode((CharSequence)newPassword));
        this.userRepository.save(user);
        this.otpService.invalidateExistingOtps(email, OtpVerification.OtpType.RESET_PASSWORD);
        return ResponseEntity.ok(Map.of("success", true, "message", "Password reset successfully! You can now login with your new password."));
    }

    @PostMapping(value={"/resend-otp"})
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String type = request.get("type");
        if (username == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }
        String email = username + "@moneymesh.com";
        OtpVerification.OtpType otpType = OtpVerification.OtpType.valueOf(type != null ? type : "RESET_PASSWORD");
        try {
            String message = this.otpService.resendOtp(email, otpType);
            return ResponseEntity.ok(Map.of("success", true, "message", message));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to resend OTP: " + e.getMessage()));
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        if (username.length() <= 3) {
            return username.charAt(0) + "***@" + domain;
        }
        return username.substring(0, 2) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }
}
