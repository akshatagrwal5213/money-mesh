import React, { useState, useEffect } from 'react';
import {
  getPreferences,
  updatePreferences,
  PreferencesRequest
} from '../services/module7Service';
import { useAuth } from '../contexts/AuthContext';
import { useTheme } from '../contexts/ThemeContext';
import ChangePasswordModal from '../components/ChangePasswordModal';
import LoginHistoryModal from '../components/LoginHistoryModal';
import ManageDevicesModal from '../components/ManageDevicesModal';
import DeleteAccountModal from '../components/DeleteAccountModal';
import { auditService } from '../services/auditService';
import api from '../services/api';
import './SettingsPage.css';

const SettingsPage: React.FC = () => {
  const { hasAccounts } = useAuth();
  const { theme, setTheme } = useTheme();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [activeTab, setActiveTab] = useState('notifications');

  // Debug logging
  useEffect(() => {
    console.log('[SettingsPage] hasAccounts value:', hasAccounts);
  }, [hasAccounts]);
  const [changePasswordModalOpen, setChangePasswordModalOpen] = useState(false);
  const [loginHistoryModalOpen, setLoginHistoryModalOpen] = useState(false);
  const [manageDevicesModalOpen, setManageDevicesModalOpen] = useState(false);
  const [deleteAccountModalOpen, setDeleteAccountModalOpen] = useState(false);
  const [preferences, setPreferences] = useState<PreferencesRequest>({
    emailNotifications: true,
    smsNotifications: false,
    pushNotifications: true,
    transactionAlerts: true,
    budgetAlerts: true,
    investmentAlerts: true,
    promotionalAlerts: false,
    theme: theme, // Initialize with current theme from ThemeContext
    language: 'en',
    currency: 'INR',
    timezone: 'Asia/Kolkata',
    dateFormat: 'DD/MM/YYYY',
    twoFactorEnabled: false,
    biometricEnabled: false,
    autoLogoutMinutes: 30,
    loginAlerts: true,
    shareAnalytics: true,
    marketingEmails: false
  });

  useEffect(() => {
    loadPreferences();
  }, []);

  const loadPreferences = async () => {
    try {
      setLoading(true);
      const data = await getPreferences();
      if (data) {
        setPreferences({
          emailNotifications: data.emailNotifications,
          smsNotifications: data.smsNotifications,
          pushNotifications: data.pushNotifications,
          transactionAlerts: data.transactionAlerts,
          budgetAlerts: data.budgetAlerts,
          investmentAlerts: data.investmentAlerts,
          promotionalAlerts: data.promotionalAlerts,
          theme: data.theme,
          language: data.language,
          currency: data.currency,
          timezone: data.timezone,
          dateFormat: data.dateFormat,
          twoFactorEnabled: data.twoFactorEnabled,
          biometricEnabled: data.biometricEnabled,
          autoLogoutMinutes: data.autoLogoutMinutes,
          loginAlerts: data.loginAlerts,
          shareAnalytics: data.shareAnalytics,
          marketingEmails: data.marketingEmails
        });
        // Sync theme with ThemeContext
        if (data.theme && (data.theme === 'LIGHT' || data.theme === 'DARK')) {
          setTheme(data.theme as 'LIGHT' | 'DARK');
        }
      }
    } catch (error) {
      console.error('Failed to load preferences:', error);
    } finally {
      setLoading(false);
    }
  };

  // Sync preferences.theme with actual ThemeContext theme
  useEffect(() => {
    setPreferences(prev => ({ ...prev, theme }));
  }, [theme]);

  const handleSave = async () => {
    console.log('Save button clicked');
    console.log('Current preferences:', preferences);
    
    try {
      setSaving(true);
      console.log('Calling updatePreferences API...');
      const result = await updatePreferences(preferences);
      console.log('Preferences saved successfully:', result);
      
      // Dispatch custom event to notify Layout component to reload preferences
      window.dispatchEvent(new Event('preferencesUpdated'));
      
      alert('Preferences saved successfully!');
    } catch (error: any) {
      console.error('Failed to save preferences:', error);
      console.error('Error details:', error.response?.data);
      alert(`Failed to save preferences: ${error.response?.data?.message || error.message || 'Unknown error'}`);
    } finally {
      setSaving(false);
    }
  };

  const handleReset = async () => {
    if (!confirm('Are you sure you want to reset all preferences to defaults?')) return;
    
    const defaults: PreferencesRequest = {
      emailNotifications: true,
      smsNotifications: false,
      pushNotifications: true,
      transactionAlerts: true,
      budgetAlerts: true,
      investmentAlerts: true,
      promotionalAlerts: false,
      theme: 'LIGHT',
      language: 'en',
      currency: 'INR',
      timezone: 'Asia/Kolkata',
      dateFormat: 'DD/MM/YYYY',
      twoFactorEnabled: false,
      biometricEnabled: false,
      autoLogoutMinutes: 30,
      loginAlerts: true,
      shareAnalytics: true,
      marketingEmails: false
    };
    
    setPreferences(defaults);
    try {
      await updatePreferences(defaults);
      setTheme('LIGHT'); // Reset theme to light
      
      // Dispatch event to update auto-logout timer
      window.dispatchEvent(new CustomEvent('preferencesUpdated', {
        detail: { autoLogoutMinutes: 30 }
      }));
      
      alert('Preferences reset to defaults');
    } catch (error) {
      console.error('Failed to reset preferences:', error);
    }
  };

  const handleDownloadData = async () => {
    try {
      // Show loading state
      const button = document.activeElement as HTMLButtonElement;
      const originalText = button?.textContent || '';
      if (button) button.textContent = 'Downloading...';

      // Fetch all user data
      const [customerData, accountsData, transactionsData, preferencesData, loginHistoryData, deviceSessionsData] = await Promise.all([
        api.get('/customer/me').catch(() => ({ data: null })),
        api.get('/accounts').catch(() => ({ data: [] })),
        api.get('/transaction').catch(() => ({ data: [] })),
        getPreferences().catch(() => null),
        auditService.getLoginHistory().catch(() => []),
        auditService.getDeviceSessions().catch(() => [])
      ]);

      // Compile all data
      const userData = {
        exportDate: new Date().toISOString(),
        exportVersion: '1.0',
        profile: customerData.data,
        accounts: accountsData.data,
        transactions: transactionsData.data,
        preferences: preferencesData,
        loginHistory: loginHistoryData,
        deviceSessions: deviceSessionsData,
        dataRights: {
          message: 'This is your personal data as stored in our system. You have the right to request corrections or deletion.',
          contact: 'support@bankingsystem.com'
        }
      };

      // Create and download JSON file
      const dataStr = JSON.stringify(userData, null, 2);
      const dataBlob = new Blob([dataStr], { type: 'application/json' });
      const url = URL.createObjectURL(dataBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `my-banking-data-${new Date().toISOString().split('T')[0]}.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);

      // Restore button text
      if (button) button.textContent = originalText;
      
      alert('Your data has been downloaded successfully!');
    } catch (error) {
      console.error('Failed to download data:', error);
      alert('Failed to download data. Please try again.');
    }
  };

  const handleDeleteAccount = async () => {
    // Determine if this is account deletion or profile deletion
    const isAccountDeletion = hasAccounts;
    const deleteType = isAccountDeletion ? 'Accounts' : 'Profile';
    
    // Different messages based on deletion type
    const warningMessage = isAccountDeletion
      ? '⚠️ WARNING: Delete All Bank Accounts?\n\n' +
        'This will permanently delete:\n' +
        '• All your bank accounts and balances\n' +
        '• Complete transaction history\n' +
        '• All debit and credit cards\n' +
        '• Pending transfers\n\n' +
        'Your profile and login will remain active.\n' +
        'You can create new accounts after deletion.\n\n' +
        'This action CANNOT be undone!\n\n' +
        'Click OK to continue or Cancel to keep your accounts.'
      : '⚠️ WARNING: Delete Profile?\n\n' +
        'This will permanently delete:\n' +
        '• Your profile information\n' +
        '• All preferences and settings\n' +
        '• Login credentials\n' +
        '• All associated data\n\n' +
        'This action CANNOT be undone!\n\n' +
        'Click OK to continue or Cancel to keep your profile.';
    
    // First confirmation
    const firstConfirm = confirm(warningMessage);

    if (!firstConfirm) return;

    // Second confirmation with typing requirement
    const username = localStorage.getItem('username');
    const typedConfirmation = prompt(
      '🔴 FINAL WARNING - Step 1 of 2\n\n' +
      `To permanently delete your ${deleteType.toLowerCase()}, type your username exactly:\n\n` +
      `"${username}"\n\n` +
      '(Case sensitive - must match exactly)'
    );

    if (typedConfirmation === null) {
      // User clicked cancel
      return;
    }

    if (typedConfirmation.trim() !== username) {
      alert(`❌ Username did not match.\n\nYou entered: "${typedConfirmation}"\nExpected: "${username}"\n\n${deleteType} deletion cancelled.`);
      return;
    }

    // Third confirmation - password verification
    const password = prompt(
      '🔴 FINAL VERIFICATION - Step 2 of 2\n\n' +
      `Enter your password to confirm ${deleteType.toLowerCase()} deletion:`
    );

    if (!password) {
      alert(`Password required. ${deleteType} deletion cancelled.`);
      return;
    }

    try {
      // Call appropriate API endpoint based on deletion type
      const endpoint = isAccountDeletion 
        ? '/customer/delete-accounts'  // Delete only bank accounts
        : '/customer/delete-profile';   // Delete entire profile
      
      await api.delete(endpoint, {
        data: { password: password }
      });
      
      if (isAccountDeletion) {
        // For account deletion, refresh the page to show no accounts
        alert('All your bank accounts have been permanently deleted. Your profile remains active.');
        window.location.reload();
      } else {
        // For profile deletion, logout and redirect
        alert('Your profile has been permanently deleted. You will now be logged out.');
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = '/login';
      }
    } catch (error: any) {
      console.error(`Failed to delete ${deleteType.toLowerCase()}:`, error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        alert(`Incorrect password. ${deleteType} deletion cancelled.`);
      } else if (error.response?.status === 400 && error.response?.data?.message?.includes('active bank accounts')) {
        // Specific error for trying to delete profile with accounts
        alert('⚠️ Cannot delete profile!\n\nYou have active bank accounts. Please delete all accounts first before deleting your profile.');
      } else {
        alert(error.response?.data?.message || `Failed to delete ${deleteType.toLowerCase()}. Please contact support.`);
      }
    }
  };

  const updatePreference = (key: keyof PreferencesRequest, value: any) => {
    setPreferences({ ...preferences, [key]: value });
    
    // If theme is changed, immediately apply it via ThemeContext
    if (key === 'theme' && (value === 'LIGHT' || value === 'DARK')) {
      setTheme(value as 'LIGHT' | 'DARK');
    }
  };

  if (loading) {
    return (
      <div className="settings-loading">
        <div className="spinner"></div>
        <p>Loading preferences...</p>
      </div>
    );
  }

  return (
    <div className="settings-page">
      <div className="settings-header">
        <h1>⚙️ Settings</h1>
        <div className="header-actions">
          <button 
            className="btn-primary" 
            onClick={(e) => {
              e.preventDefault();
              console.log('Button clicked!', e);
              handleSave();
            }} 
            disabled={saving}
            type="button"
          >
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </div>
      </div>

      <div className="settings-container">
        <div className="settings-tabs">
          <button
            className={activeTab === 'notifications' ? 'active' : ''}
            onClick={() => setActiveTab('notifications')}
          >
            🔔 Notifications
          </button>
          <button
            className={activeTab === 'security' ? 'active' : ''}
            onClick={() => setActiveTab('security')}
          >
            🔒 Security
          </button>
          <button
            className={activeTab === 'privacy' ? 'active' : ''}
            onClick={() => setActiveTab('privacy')}
          >
            🛡️ Privacy
          </button>
        </div>

        <div className="settings-content">
          {/* Notifications Tab */}
          {activeTab === 'notifications' && (
            <div className="settings-section">
              <h2>Notification Preferences</h2>
              <p className="section-description">
                Choose how you want to be notified about important events
              </p>

              <div className="settings-group">
                <h3>Notification Channels</h3>
                
                <div className="setting-item">
                  <div className="setting-info">
                    <label>Email Notifications</label>
                    <p>Receive notifications via email</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.emailNotifications}
                      onChange={(e) => updatePreference('emailNotifications', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>

                <div className="setting-item">
                  <div className="setting-info">
                    <label>SMS Notifications</label>
                    <p>Receive notifications via SMS</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.smsNotifications}
                      onChange={(e) => updatePreference('smsNotifications', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>

                <div className="setting-item">
                  <div className="setting-info">
                    <label>Push Notifications</label>
                    <p>Receive push notifications in browser</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.pushNotifications}
                      onChange={(e) => updatePreference('pushNotifications', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>
              </div>

              <div className="settings-group">
                <h3>Alert Types</h3>
                
                {/* Only show account-related alerts if user has accounts */}
                {hasAccounts && (
                  <>
                    <div className="setting-item">
                      <div className="setting-info">
                        <label>Transaction Alerts</label>
                        <p>Get notified about account transactions</p>
                      </div>
                      <label className="toggle">
                        <input
                          type="checkbox"
                          checked={preferences.transactionAlerts}
                          onChange={(e) => updatePreference('transactionAlerts', e.target.checked)}
                        />
                        <span className="toggle-slider"></span>
                      </label>
                    </div>

                    <div className="setting-item">
                      <div className="setting-info">
                        <label>Budget Alerts</label>
                        <p>Get notified when you reach budget thresholds</p>
                      </div>
                      <label className="toggle">
                        <input
                          type="checkbox"
                          checked={preferences.budgetAlerts}
                          onChange={(e) => updatePreference('budgetAlerts', e.target.checked)}
                        />
                        <span className="toggle-slider"></span>
                      </label>
                    </div>

                    <div className="setting-item">
                      <div className="setting-info">
                        <label>Investment Alerts</label>
                        <p>Get notified about investment updates</p>
                      </div>
                      <label className="toggle">
                        <input
                          type="checkbox"
                          checked={preferences.investmentAlerts}
                          onChange={(e) => updatePreference('investmentAlerts', e.target.checked)}
                        />
                        <span className="toggle-slider"></span>
                      </label>
                    </div>
                  </>
                )}

                <div className="setting-item">
                  <div className="setting-info">
                    <label>Login Alerts</label>
                    <p>Get notified about login activity</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.loginAlerts}
                      onChange={(e) => updatePreference('loginAlerts', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>
              </div>
            </div>
          )}

          {/* Security Tab */}
          {activeTab === 'security' && (
            <div className="settings-section">
              <h2>Security Settings</h2>
              <p className="section-description">
                Manage your account security options
              </p>

              <div className="settings-group">
                <div className="setting-item">
                  <div className="setting-info">
                    <label>Two-Factor Authentication</label>
                    <p>Add an extra layer of security to your account</p>
                    <small style={{ color: '#f97316', fontWeight: 500, marginTop: '4px', display: 'block' }}>
                      🚧 Coming Soon - Feature in development. Transfer OTP (₹10,000+) is currently active.
                    </small>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.twoFactorEnabled}
                      onChange={(e) => updatePreference('twoFactorEnabled', e.target.checked)}
                      disabled
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>

                <div className="setting-item">
                  <div className="setting-info">
                    <label>Biometric Authentication</label>
                    <p>Use fingerprint or face recognition</p>
                    <small style={{ color: '#f97316', fontWeight: 500, marginTop: '4px', display: 'block' }}>
                      🚧 Coming Soon - Feature in development
                    </small>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.biometricEnabled}
                      onChange={(e) => updatePreference('biometricEnabled', e.target.checked)}
                      disabled
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>

                <div className="setting-item vertical">
                  <label>
                    Auto-Logout Timer (minutes)
                    <span style={{ 
                      marginLeft: '8px', 
                      padding: '2px 8px', 
                      backgroundColor: '#10b981', 
                      color: 'white', 
                      borderRadius: '4px', 
                      fontSize: '12px',
                      fontWeight: '600'
                    }}>
                      ✓ Active
                    </span>
                  </label>
                  <input
                    type="number"
                    value={preferences.autoLogoutMinutes}
                    onChange={(e) => updatePreference('autoLogoutMinutes', parseInt(e.target.value))}
                    min="5"
                    max="120"
                  />
                  <small>
                    🔒 Automatically logout after period of inactivity. 
                    The timer resets with any mouse, keyboard, or touch activity.
                  </small>
                </div>
              </div>

              <div className="security-actions">
                <button 
                  className="btn-secondary" 
                  type="button"
                  onClick={() => setChangePasswordModalOpen(true)}
                >
                  Change Password
                </button>
                <button 
                  className="btn-secondary" 
                  type="button"
                  onClick={() => setLoginHistoryModalOpen(true)}
                >
                  View Login History
                </button>
                <button 
                  className="btn-secondary" 
                  type="button"
                  onClick={() => setManageDevicesModalOpen(true)}
                >
                  Manage Devices
                </button>
              </div>
            </div>
          )}

          {/* Privacy Tab */}
          {activeTab === 'privacy' && (
            <div className="settings-section">
              <h2>Privacy Settings</h2>
              <p className="section-description">
                Control your data and privacy preferences
              </p>

              <div className="settings-group">
                <div className="setting-item">
                  <div className="setting-info">
                    <label>Share Analytics</label>
                    <p>Share anonymized data for service improvements</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.shareAnalytics}
                      onChange={(e) => updatePreference('shareAnalytics', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>

                <div className="setting-item">
                  <div className="setting-info">
                    <label>Marketing Emails</label>
                    <p>Receive promotional emails and offers</p>
                  </div>
                  <label className="toggle">
                    <input
                      type="checkbox"
                      checked={preferences.marketingEmails}
                      onChange={(e) => updatePreference('marketingEmails', e.target.checked)}
                    />
                    <span className="toggle-slider"></span>
                  </label>
                </div>
              </div>

              <div className="privacy-actions">
                <button className="btn-secondary" onClick={handleDownloadData}>Download My Data</button>
                {hasAccounts && (
                  <button className="btn-secondary">Export Transactions</button>
                )}
                {hasAccounts ? (
                  <button className="btn-danger" onClick={() => {
                    console.log('[SettingsPage] Delete Bank Account button clicked');
                    setDeleteAccountModalOpen(true);
                  }}>
                    Delete Bank Account {/* hasAccounts: {String(hasAccounts)} */}
                  </button>
                ) : (
                  <button className="btn-danger" onClick={handleDeleteAccount}>
                    Delete Profile {/* hasAccounts: {String(hasAccounts)} */}
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      </div>

      <div className="settings-footer">
        <button className="btn-secondary" onClick={handleReset}>
          Reset to Defaults
        </button>
        <button className="btn-primary" onClick={handleSave} disabled={saving}>
          {saving ? 'Saving...' : 'Save All Changes'}
        </button>
      </div>

      {/* Change Password Modal */}
      <ChangePasswordModal 
        open={changePasswordModalOpen}
        onClose={() => setChangePasswordModalOpen(false)}
      />

      {/* Login History Modal */}
      <LoginHistoryModal 
        open={loginHistoryModalOpen}
        onClose={() => setLoginHistoryModalOpen(false)}
      />

      {/* Manage Devices Modal */}
      <ManageDevicesModal 
        open={manageDevicesModalOpen}
        onClose={() => setManageDevicesModalOpen(false)}
      />

      {/* Delete Account Modal */}
      <DeleteAccountModal 
        open={deleteAccountModalOpen}
        onClose={() => setDeleteAccountModalOpen(false)}
        onAccountDeleted={() => window.location.reload()}
      />
    </div>
  );
};

export default SettingsPage;
