package com.bank.dto;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidationResult {
    private boolean valid;
    private List<String> errors = new ArrayList<>();
    private int strength; // 0-100

    public PasswordValidationResult() {
    }

    public PasswordValidationResult(boolean valid) {
        this.valid = valid;
    }

    public void addError(String error) {
        this.errors.add(error);
        this.valid = false;
    }

    // Getters and Setters
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
