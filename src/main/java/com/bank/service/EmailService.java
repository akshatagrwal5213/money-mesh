package com.bank.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        String subject = this.getEmailSubject(purpose);
        String body = this.buildOtpEmailBody(otpCode, purpose);
        logger.info("=====================================");
        logger.info("\ud83d\udce7 EMAIL SENT");
        logger.info("To: {}", (Object)toEmail);
        logger.info("Subject: {}", (Object)subject);
        logger.info("OTP Code: {}", (Object)otpCode);
        logger.info("Purpose: {}", (Object)purpose);
        logger.info("=====================================");
        logger.info("\n{}\n", (Object)body);
        try {
            Thread.sleep(100L);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private String getEmailSubject(String purpose) {
        return switch (purpose.toUpperCase()) {
            case "RESET_PASSWORD" -> "\ud83d\udd10 MoneyMesh - Password Reset OTP";
            case "VERIFY_EMAIL" -> "\u2705 MoneyMesh - Verify Your Email";
            case "LOGIN" -> "\ud83d\udd11 MoneyMesh - Login OTP";
            case "TRANSACTION" -> "\ud83d\udcb8 MoneyMesh - Transaction Verification OTP";
            default -> "\ud83d\udd78\ufe0f MoneyMesh - Verification OTP";
        };
    }

    private String buildOtpEmailBody(String otpCode, String purpose) {
        return String.format("\n\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557\n\u2551      \ud83d\udd78\ufe0f  MoneyMesh Banking System      \u2551\n\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d\n\nHello,\n\nYour One-Time Password (OTP) for %s is:\n\n        \u250c\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510\n        \u2502   %s   \u2502\n        \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518\n\nThis OTP is valid for 10 minutes.\n\n\u26a0\ufe0f  Security Notice:\n\u2022 Never share this OTP with anyone\n\u2022 MoneyMesh staff will never ask for your OTP\n\u2022 If you didn't request this, please contact support\n\nThank you for using MoneyMesh!\n\nBest regards,\nMoneyMesh Security Team\n\n\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\nThis is an automated message. Please do not reply.\n\n", purpose.toLowerCase().replace("_", " "), otpCode);
    }

    public void sendPasswordResetSuccessEmail(String toEmail, String username) {
        logger.info("=====================================");
        logger.info("\ud83d\udce7 PASSWORD RESET SUCCESS EMAIL");
        logger.info("To: {}", (Object)toEmail);
        logger.info("=====================================");
        logger.info("\n\u2554\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2557\n\u2551      \ud83d\udd78\ufe0f  MoneyMesh Banking System      \u2551\n\u255a\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u255d\n\nHello {},\n\n\u2705 Your password has been successfully reset!\n\nYou can now login with your new password.\n\nIf you didn't make this change, please contact our\nsupport team immediately.\n\nBest regards,\nMoneyMesh Security Team\n\n", (Object)username);
    }

    public void sendTransactionAlert(String toEmail, String username, String transactionType, Double amount) {
        logger.info("=====================================");
        logger.info("\ud83d\udce7 TRANSACTION ALERT");
        logger.info("To: {}", (Object)toEmail);
        logger.info("Transaction: {} - \u20b9{}", (Object)transactionType, (Object)amount);
        logger.info("=====================================");
    }
}
