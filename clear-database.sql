-- ============================================
-- Clear Database Script (Preserve Admin Only)
-- ============================================
-- This script removes all customer data while keeping admin accounts

USE banking_system;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- 1. Clear all transactions
TRUNCATE TABLE transaction;

-- 2. Clear pending transfers
TRUNCATE TABLE pending_transfer;

-- 3. Clear all payment and transaction related tables
TRUNCATE TABLE card_transaction;
TRUNCATE TABLE bill_payment;
TRUNCATE TABLE bill_payments;
TRUNCATE TABLE upi_transactions;
TRUNCATE TABLE transaction_analytics;
TRUNCATE TABLE payment_histories;

-- 4. Clear investment related tables
TRUNCATE TABLE investment_portfolio;
TRUNCATE TABLE mutual_fund_holdings;
TRUNCATE TABLE sip_investments;
TRUNCATE TABLE fixed_deposit;
TRUNCATE TABLE fixed_deposits;
TRUNCATE TABLE recurring_deposits;
TRUNCATE TABLE portfolio_analyses;
TRUNCATE TABLE investment_recommendations;

-- 5. Clear loan related tables
TRUNCATE TABLE loan_payment;
TRUNCATE TABLE loan_repayments;
TRUNCATE TABLE loan_prepayments;
TRUNCATE TABLE loan_foreclosures;
TRUNCATE TABLE loan_restructures;
TRUNCATE TABLE loan_collaterals;
TRUNCATE TABLE emi_schedules;
TRUNCATE TABLE overdue_trackings;
TRUNCATE TABLE loan_application;
TRUNCATE TABLE loan;
TRUNCATE TABLE loans;
TRUNCATE TABLE loan_eligibilities;

-- 6. Clear financial planning tables
TRUNCATE TABLE budgets;
TRUNCATE TABLE financial_goals;
TRUNCATE TABLE savings_goal;
TRUNCATE TABLE retirement_plans;
TRUNCATE TABLE wealth_profiles;
TRUNCATE TABLE financial_health_scores;

-- 7. Clear tax related tables
TRUNCATE TABLE tax_deductions;
TRUNCATE TABLE tax_payments;
TRUNCATE TABLE tax_documents;
TRUNCATE TABLE capital_gains;

-- 8. Clear insurance tables
TRUNCATE TABLE insurance_premium_payments;
TRUNCATE TABLE insurance_claims;
TRUNCATE TABLE insurance_policies;

-- 9. Clear credit and rewards tables
TRUNCATE TABLE credit_scores;
TRUNCATE TABLE credit_reports;
TRUNCATE TABLE credit_inquiries;
TRUNCATE TABLE credit_disputes;
TRUNCATE TABLE reward_points;
TRUNCATE TABLE points_redemptions;
TRUNCATE TABLE cashbacks;
TRUNCATE TABLE referral_bonuses;
TRUNCATE TABLE milestone_rewards;

-- 10. Clear notification and preference tables
TRUNCATE TABLE notification;
TRUNCATE TABLE notifications;
TRUNCATE TABLE notification_preference;

-- 11. Clear QR codes and beneficiaries
TRUNCATE TABLE qr_codes;
TRUNCATE TABLE beneficiary;

-- 12. Clear cards (customer cards only)
DELETE FROM card WHERE account_id NOT IN (
    SELECT a.id FROM account a
    INNER JOIN customer c ON a.customer_id = c.id
    INNER JOIN app_user u ON c.user_id = u.id
    WHERE u.role LIKE '%ADMIN%'
);

-- 13. Clear all accounts (customer accounts only)
DELETE FROM account WHERE customer_id NOT IN (
    SELECT c.id FROM customer c
    INNER JOIN app_user u ON c.user_id = u.id
    WHERE u.role LIKE '%ADMIN%'
);

-- 14. Clear all KYC documents
TRUNCATE TABLE kyc_document;

-- 15. Clear customer tier info
TRUNCATE TABLE customer_tier_info;

-- 16. Clear all customers (non-admin customers)
DELETE FROM customer WHERE user_id NOT IN (
    SELECT id FROM app_user WHERE role LIKE '%ADMIN%'
);

-- 17. Clear user related tables for non-admin users
TRUNCATE TABLE user_profile;
TRUNCATE TABLE user_preferences;
TRUNCATE TABLE user_device;
TRUNCATE TABLE user_sessions;
TRUNCATE TABLE session_log;
TRUNCATE TABLE mfa_settings;
TRUNCATE TABLE two_factor_auth;
TRUNCATE TABLE refresh_tokens;

-- 18. Clear OTP verifications
TRUNCATE TABLE otp_verification;

-- 19. Clear spending analytics
TRUNCATE TABLE spending_category;

-- 20. Clear audit logs
TRUNCATE TABLE audit_logs;

-- 21. Clear all non-admin users
DELETE FROM app_user WHERE role NOT LIKE '%ADMIN%';

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Show remaining admin users
SELECT '============================================' as '';
SELECT 'REMAINING ADMIN USERS IN DATABASE' as '';
SELECT '============================================' as '';
SELECT 
    id as 'User ID',
    username as 'Username',
    role as 'Role',
    password as 'Encrypted Password'
FROM app_user 
WHERE role LIKE '%ADMIN%';

-- Show counts after cleanup
SELECT '' as '';
SELECT '============================================' as '';
SELECT 'DATABASE CLEANUP SUMMARY' as '';
SELECT '============================================' as '';
SELECT 
    'Total Users (Admin Only)' as 'Table',
    COUNT(*) as 'Count'
FROM app_user
UNION ALL
SELECT 
    'Total Customers',
    COUNT(*)
FROM customer
UNION ALL
SELECT 
    'Total Accounts',
    COUNT(*)
FROM account
UNION ALL
SELECT 
    'Total Transactions',
    COUNT(*)
FROM transaction
UNION ALL
SELECT 
    'Total Cards',
    COUNT(*)
FROM card
UNION ALL
SELECT 
    'Total Loans',
    COUNT(*)
FROM loan;

SELECT '' as '';
SELECT '============================================' as '';
SELECT '✅ Database cleared successfully!' as '';
SELECT 'Only admin users and their data remain.' as '';
SELECT '============================================' as '';
