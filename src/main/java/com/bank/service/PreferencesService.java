package com.bank.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.dto.PreferencesRequest;
import com.bank.exception.ResourceNotFoundException;
import com.bank.model.Customer;
import com.bank.model.UserPreferences;
import com.bank.repository.CustomerRepository;
import com.bank.repository.UserPreferencesRepository;

@Service
@Transactional
public class PreferencesService {
    
    @Autowired
    private UserPreferencesRepository preferencesRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public UserPreferences getPreferences(String username) {
        Optional<UserPreferences> preferences = preferencesRepository.findByCustomer_User_Username(username);
        
        if (preferences.isPresent()) {
            return preferences.get();
        }
        
        // Create default preferences if not exists
        Customer customer = customerRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        UserPreferences newPreferences = new UserPreferences(customer);
        return preferencesRepository.save(newPreferences);
    }
    
    public UserPreferences updatePreferences(String username, PreferencesRequest request) {
        UserPreferences preferences = getPreferences(username);
        
        // Notification preferences
        if (request.getEmailNotifications() != null) {
            preferences.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getSmsNotifications() != null) {
            preferences.setSmsNotifications(request.getSmsNotifications());
        }
        if (request.getPushNotifications() != null) {
            preferences.setPushNotifications(request.getPushNotifications());
        }
        if (request.getTransactionAlerts() != null) {
            preferences.setTransactionAlerts(request.getTransactionAlerts());
        }
        if (request.getBudgetAlerts() != null) {
            preferences.setBudgetAlerts(request.getBudgetAlerts());
        }
        if (request.getInvestmentAlerts() != null) {
            preferences.setInvestmentAlerts(request.getInvestmentAlerts());
        }
        if (request.getPromotionalAlerts() != null) {
            preferences.setPromotionalAlerts(request.getPromotionalAlerts());
        }
        
        // Display preferences
        if (request.getTheme() != null) {
            preferences.setTheme(request.getTheme());
        }
        if (request.getLanguage() != null) {
            preferences.setLanguage(request.getLanguage());
        }
        if (request.getCurrency() != null) {
            preferences.setCurrency(request.getCurrency());
        }
        if (request.getTimezone() != null) {
            preferences.setTimezone(request.getTimezone());
        }
        if (request.getDateFormat() != null) {
            preferences.setDateFormat(request.getDateFormat());
        }
        
        // Security preferences
        if (request.getTwoFactorEnabled() != null) {
            preferences.setTwoFactorEnabled(request.getTwoFactorEnabled());
        }
        if (request.getBiometricEnabled() != null) {
            preferences.setBiometricEnabled(request.getBiometricEnabled());
        }
        if (request.getAutoLogoutMinutes() != null) {
            preferences.setAutoLogoutMinutes(request.getAutoLogoutMinutes());
        }
        if (request.getLoginAlerts() != null) {
            preferences.setLoginAlerts(request.getLoginAlerts());
        }
        
        // Privacy preferences
        if (request.getShareAnalytics() != null) {
            preferences.setShareAnalytics(request.getShareAnalytics());
        }
        if (request.getMarketingEmails() != null) {
            preferences.setMarketingEmails(request.getMarketingEmails());
        }
        
        return preferencesRepository.save(preferences);
    }
    
    public void resetToDefaults(String username) {
        UserPreferences preferences = getPreferences(username);
        
        // Reset to defaults
        preferences.setEmailNotifications(true);
        preferences.setSmsNotifications(true);
        preferences.setPushNotifications(true);
        preferences.setTransactionAlerts(true);
        preferences.setBudgetAlerts(true);
        preferences.setInvestmentAlerts(true);
        preferences.setPromotionalAlerts(false);
        
        preferences.setTheme("light");
        preferences.setLanguage("en");
        preferences.setCurrency("INR");
        preferences.setTimezone("Asia/Kolkata");
        preferences.setDateFormat("DD/MM/YYYY");
        
        preferences.setTwoFactorEnabled(false);
        preferences.setBiometricEnabled(false);
        preferences.setAutoLogoutMinutes(15);
        preferences.setLoginAlerts(true);
        
        preferences.setShareAnalytics(true);
        preferences.setMarketingEmails(false);
        
        preferencesRepository.save(preferences);
    }
}
