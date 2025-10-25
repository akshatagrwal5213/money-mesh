package com.bank.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.bank.dto.PasswordValidationResult;

@Service
public class PasswordPolicyService {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    // Password patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]");

    /**
     * Validate password against security policy
     */
    public PasswordValidationResult validatePassword(String password) {
        PasswordValidationResult result = new PasswordValidationResult();
        result.setValid(true);

        if (password == null || password.isEmpty()) {
            result.addError("Password cannot be empty");
            result.setStrength(0);
            return result;
        }

        // Length check
        if (password.length() < MIN_LENGTH) {
            result.addError("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            result.addError("Password must not exceed " + MAX_LENGTH + " characters");
        }

        // Complexity checks
        boolean hasUppercase = UPPERCASE_PATTERN.matcher(password).find();
        boolean hasLowercase = LOWERCASE_PATTERN.matcher(password).find();
        boolean hasDigit = DIGIT_PATTERN.matcher(password).find();
        boolean hasSpecialChar = SPECIAL_CHAR_PATTERN.matcher(password).find();

        if (!hasUppercase) {
            result.addError("Password must contain at least one uppercase letter");
        }

        if (!hasLowercase) {
            result.addError("Password must contain at least one lowercase letter");
        }

        if (!hasDigit) {
            result.addError("Password must contain at least one digit");
        }

        if (!hasSpecialChar) {
            result.addError("Password must contain at least one special character");
        }

        // Common password check (basic implementation)
        if (isCommonPassword(password)) {
            result.addError("Password is too common. Please choose a stronger password");
        }

        // Calculate strength
        int strength = calculatePasswordStrength(password, hasUppercase, hasLowercase, hasDigit, hasSpecialChar);
        result.setStrength(strength);

        return result;
    }

    /**
     * Calculate password strength (0-100)
     */
    private int calculatePasswordStrength(String password, boolean hasUppercase, 
                                          boolean hasLowercase, boolean hasDigit, 
                                          boolean hasSpecialChar) {
        int strength = 0;

        // Length contribution (up to 40 points)
        strength += Math.min(password.length() * 3, 40);

        // Character variety (up to 40 points, 10 each)
        if (hasUppercase) strength += 10;
        if (hasLowercase) strength += 10;
        if (hasDigit) strength += 10;
        if (hasSpecialChar) strength += 10;

        // Entropy/uniqueness (up to 20 points)
        long uniqueChars = password.chars().distinct().count();
        strength += Math.min((int)(uniqueChars * 1.5), 20);

        return Math.min(strength, 100);
    }

    /**
     * Check if password is in common password list
     */
    private boolean isCommonPassword(String password) {
        String[] commonPasswords = {
            "password", "123456", "12345678", "qwerty", "abc123",
            "monkey", "1234567", "letmein", "trustno1", "dragon",
            "baseball", "111111", "iloveyou", "master", "sunshine",
            "ashley", "bailey", "passw0rd", "shadow", "123123",
            "654321", "superman", "qazwsx", "michael", "football"
        };

        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check password strength level
     */
    public String getStrengthLevel(int strength) {
        if (strength < 30) return "Weak";
        if (strength < 60) return "Moderate";
        if (strength < 80) return "Strong";
        return "Very Strong";
    }
}
