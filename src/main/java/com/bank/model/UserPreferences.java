package com.bank.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"user", "preferences"})
    private Customer customer;
    
    // Notification Preferences
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;
    
    @Column(name = "sms_notifications", nullable = false)
    private Boolean smsNotifications = true;
    
    @Column(name = "push_notifications", nullable = false)
    private Boolean pushNotifications = true;
    
    @Column(name = "transaction_alerts", nullable = false)
    private Boolean transactionAlerts = true;
    
    @Column(name = "budget_alerts", nullable = false)
    private Boolean budgetAlerts = true;
    
    @Column(name = "investment_alerts", nullable = false)
    private Boolean investmentAlerts = true;
    
    @Column(name = "promotional_alerts", nullable = false)
    private Boolean promotionalAlerts = false;
    
    // Display Preferences
    @Column(nullable = false)
    private String theme = "light"; // light, dark, auto
    
    @Column(nullable = false)
    private String language = "en";
    
    @Column(nullable = false)
    private String currency = "INR";
    
    @Column(nullable = false)
    private String timezone = "Asia/Kolkata";
    
    @Column(name = "date_format", nullable = false)
    private String dateFormat = "DD/MM/YYYY";
    
    // Security Preferences
    @Column(name = "two_factor_enabled", nullable = false)
    private Boolean twoFactorEnabled = false;
    
    @Column(name = "biometric_enabled", nullable = false)
    private Boolean biometricEnabled = false;
    
    @Column(name = "auto_logout_minutes")
    private Integer autoLogoutMinutes = 15;
    
    @Column(name = "login_alerts", nullable = false)
    private Boolean loginAlerts = true;
    
    // Privacy Preferences
    @Column(name = "share_analytics", nullable = false)
    private Boolean shareAnalytics = true;
    
    @Column(name = "marketing_emails", nullable = false)
    private Boolean marketingEmails = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public UserPreferences() {}
    
    public UserPreferences(Customer customer) {
        this.customer = customer;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getSmsNotifications() {
        return smsNotifications;
    }
    
    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getTransactionAlerts() {
        return transactionAlerts;
    }
    
    public void setTransactionAlerts(Boolean transactionAlerts) {
        this.transactionAlerts = transactionAlerts;
    }
    
    public Boolean getBudgetAlerts() {
        return budgetAlerts;
    }
    
    public void setBudgetAlerts(Boolean budgetAlerts) {
        this.budgetAlerts = budgetAlerts;
    }
    
    public Boolean getInvestmentAlerts() {
        return investmentAlerts;
    }
    
    public void setInvestmentAlerts(Boolean investmentAlerts) {
        this.investmentAlerts = investmentAlerts;
    }
    
    public Boolean getPromotionalAlerts() {
        return promotionalAlerts;
    }
    
    public void setPromotionalAlerts(Boolean promotionalAlerts) {
        this.promotionalAlerts = promotionalAlerts;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getDateFormat() {
        return dateFormat;
    }
    
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
    
    public Boolean getTwoFactorEnabled() {
        return twoFactorEnabled;
    }
    
    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }
    
    public Boolean getBiometricEnabled() {
        return biometricEnabled;
    }
    
    public void setBiometricEnabled(Boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }
    
    public Integer getAutoLogoutMinutes() {
        return autoLogoutMinutes;
    }
    
    public void setAutoLogoutMinutes(Integer autoLogoutMinutes) {
        this.autoLogoutMinutes = autoLogoutMinutes;
    }
    
    public Boolean getLoginAlerts() {
        return loginAlerts;
    }
    
    public void setLoginAlerts(Boolean loginAlerts) {
        this.loginAlerts = loginAlerts;
    }
    
    public Boolean getShareAnalytics() {
        return shareAnalytics;
    }
    
    public void setShareAnalytics(Boolean shareAnalytics) {
        this.shareAnalytics = shareAnalytics;
    }
    
    public Boolean getMarketingEmails() {
        return marketingEmails;
    }
    
    public void setMarketingEmails(Boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
