package com.bank.dto;

public class PreferencesRequest {
    
    // Notification Preferences
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean transactionAlerts;
    private Boolean budgetAlerts;
    private Boolean investmentAlerts;
    private Boolean promotionalAlerts;
    
    // Display Preferences
    private String theme;
    private String language;
    private String currency;
    private String timezone;
    private String dateFormat;
    
    // Security Preferences
    private Boolean twoFactorEnabled;
    private Boolean biometricEnabled;
    private Integer autoLogoutMinutes;
    private Boolean loginAlerts;
    
    // Privacy Preferences
    private Boolean shareAnalytics;
    private Boolean marketingEmails;
    
    // Constructors
    public PreferencesRequest() {}
    
    // Getters and Setters
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
}
