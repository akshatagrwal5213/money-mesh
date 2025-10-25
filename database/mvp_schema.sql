-- MoneyMesh MVP - Complete Database Schema

-- ============================================
-- PHASE 1: Enhanced Authentication & Security
-- ============================================

-- OTP Verification Table
CREATE TABLE IF NOT EXISTS otp_verification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    otp_type ENUM('LOGIN', 'RESET_PASSWORD', 'VERIFY_EMAIL', 'TRANSACTION') NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_email_type (email, otp_type),
    INDEX idx_expires (expires_at)
);

-- 2FA Settings Table
CREATE TABLE IF NOT EXISTS two_factor_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    secret_key VARCHAR(255),
    backup_codes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- User Devices Table
CREATE TABLE IF NOT EXISTS user_device (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    device_fingerprint VARCHAR(255) NOT NULL,
    device_name VARCHAR(255),
    device_type VARCHAR(50),
    ip_address VARCHAR(45),
    location VARCHAR(255),
    last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    trusted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_device (user_id, device_fingerprint)
);

-- Session Logs Table
CREATE TABLE IF NOT EXISTS session_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(500),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_id BIGINT,
    status ENUM('ACTIVE', 'EXPIRED', 'LOGGED_OUT') DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES user_device(id) ON DELETE SET NULL,
    INDEX idx_user_session (user_id, status)
);

-- ============================================
-- PHASE 2: User Profile & KYC
-- ============================================

-- User Profile Table
CREATE TABLE IF NOT EXISTS user_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    phone_number VARCHAR(20),
    alternate_phone VARCHAR(20),
    pan_number VARCHAR(10),
    aadhar_number VARCHAR(12),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    country VARCHAR(100) DEFAULT 'India',
    occupation VARCHAR(100),
    annual_income DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- KYC Documents Table
CREATE TABLE IF NOT EXISTS kyc_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_type ENUM('PAN_CARD', 'AADHAR_CARD', 'PASSPORT', 'DRIVING_LICENSE', 'VOTER_ID', 'UTILITY_BILL', 'PHOTO') NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    document_number VARCHAR(50),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    verified_by BIGINT,
    verification_date TIMESTAMP NULL,
    rejection_reason TEXT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (verified_by) REFERENCES app_user(id) ON DELETE SET NULL,
    INDEX idx_user_status (user_id, status)
);

-- Notification Preferences Table
CREATE TABLE IF NOT EXISTS notification_preference (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    email_enabled BOOLEAN DEFAULT TRUE,
    sms_enabled BOOLEAN DEFAULT TRUE,
    push_enabled BOOLEAN DEFAULT TRUE,
    transaction_alerts BOOLEAN DEFAULT TRUE,
    login_alerts BOOLEAN DEFAULT TRUE,
    promotional_emails BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- ============================================
-- PHASE 3: Enhanced Account Management
-- ============================================

-- Account Types Table
CREATE TABLE IF NOT EXISTS account_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    min_balance DECIMAL(15,2) DEFAULT 0,
    interest_rate DECIMAL(5,2) DEFAULT 0,
    features TEXT,
    active BOOLEAN DEFAULT TRUE
);

-- Insert Default Account Types
INSERT INTO account_type (name, code, min_balance, interest_rate, features) VALUES
('Savings Account', 'SAV', 1000.00, 4.00, 'Basic savings with 4% interest'),
('Current Account', 'CUR', 5000.00, 0.00, 'Business account with no interest'),
('Fixed Deposit', 'FD', 10000.00, 6.50, 'Fixed tenure with higher interest'),
('Recurring Deposit', 'RD', 500.00, 5.50, 'Monthly deposit scheme')
ON DUPLICATE KEY UPDATE name=name;

-- Enhanced Account Table (Add account_type_id)
ALTER TABLE account ADD COLUMN IF NOT EXISTS account_type_id BIGINT DEFAULT 1;
ALTER TABLE account ADD COLUMN IF NOT EXISTS branch_code VARCHAR(20) DEFAULT 'MAIN001';
ALTER TABLE account ADD COLUMN IF NOT EXISTS ifsc_code VARCHAR(20) DEFAULT 'MNMS0000001';
ALTER TABLE account ADD COLUMN IF NOT EXISTS nickname VARCHAR(100);
ALTER TABLE account ADD COLUMN IF NOT EXISTS is_primary BOOLEAN DEFAULT FALSE;
ALTER TABLE account ADD FOREIGN KEY (account_type_id) REFERENCES account_type(id);

-- Fixed Deposits Table
CREATE TABLE IF NOT EXISTS fixed_deposit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    principal_amount DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    maturity_amount DECIMAL(15,2) NOT NULL,
    start_date DATE NOT NULL,
    maturity_date DATE NOT NULL,
    status ENUM('ACTIVE', 'MATURED', 'CLOSED') DEFAULT 'ACTIVE',
    auto_renew BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    INDEX idx_user_status (user_id, status)
);

-- ============================================
-- PHASE 4: Advanced Transfers & Payments
-- ============================================

-- Beneficiaries Table
CREATE TABLE IF NOT EXISTS beneficiary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    beneficiary_name VARCHAR(255) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(20),
    bank_name VARCHAR(255),
    nickname VARCHAR(100),
    verified BOOLEAN DEFAULT FALSE,
    daily_limit DECIMAL(15,2) DEFAULT 50000.00,
    is_favorite BOOLEAN DEFAULT FALSE,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_transfer_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_beneficiary (user_id, is_favorite)
);

-- Transfer Limits Table
CREATE TABLE IF NOT EXISTS transfer_limit (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    daily_limit DECIMAL(15,2) DEFAULT 100000.00,
    per_transaction_limit DECIMAL(15,2) DEFAULT 50000.00,
    monthly_limit DECIMAL(15,2) DEFAULT 500000.00,
    daily_used DECIMAL(15,2) DEFAULT 0,
    monthly_used DECIMAL(15,2) DEFAULT 0,
    last_reset_date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Scheduled Transfers Table
CREATE TABLE IF NOT EXISTS scheduled_transfer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    from_account_id BIGINT NOT NULL,
    to_account_number VARCHAR(50) NOT NULL,
    beneficiary_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    frequency ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY') NOT NULL,
    next_execution_date DATE NOT NULL,
    end_date DATE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (from_account_id) REFERENCES account(id) ON DELETE CASCADE,
    FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(id) ON DELETE SET NULL,
    INDEX idx_next_execution (active, next_execution_date)
);

-- ============================================
-- PHASE 5: Bill Payments & Recharges
-- ============================================

-- Billers Table
CREATE TABLE IF NOT EXISTS biller (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category ENUM('ELECTRICITY', 'WATER', 'GAS', 'MOBILE', 'DTH', 'BROADBAND', 'CREDIT_CARD', 'LOAN_EMI', 'INSURANCE', 'OTHER') NOT NULL,
    logo_url VARCHAR(500),
    active BOOLEAN DEFAULT TRUE,
    parameters JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Sample Billers
INSERT INTO biller (name, category, active) VALUES
('State Electricity Board', 'ELECTRICITY', TRUE),
('City Water Corporation', 'WATER', TRUE),
('Gas Authority', 'GAS', TRUE),
('Airtel Mobile', 'MOBILE', TRUE),
('Jio Mobile', 'MOBILE', TRUE),
('Tata Sky DTH', 'DTH', TRUE),
('ACT Broadband', 'BROADBAND', TRUE)
ON DUPLICATE KEY UPDATE name=name;

-- Bill Payments Table
CREATE TABLE IF NOT EXISTS bill_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    biller_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    bill_number VARCHAR(100),
    consumer_number VARCHAR(100),
    amount DECIMAL(15,2) NOT NULL,
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    payment_reference VARCHAR(100),
    paid_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (biller_id) REFERENCES biller(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
    INDEX idx_user_biller (user_id, biller_id)
);

-- Auto Pay Settings Table
CREATE TABLE IF NOT EXISTS auto_pay (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    biller_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    max_amount DECIMAL(15,2) NOT NULL,
    frequency ENUM('MONTHLY', 'QUARTERLY', 'YEARLY') DEFAULT 'MONTHLY',
    next_payment_date DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (biller_id) REFERENCES biller(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

-- ============================================
-- PHASE 6: Notifications & Alerts
-- ============================================

-- Notifications Table
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type ENUM('TRANSACTION', 'LOGIN', 'SECURITY', 'BILL', 'PROMOTIONAL', 'SYSTEM') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSON,
    is_read BOOLEAN DEFAULT FALSE,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    INDEX idx_user_read (user_id, is_read),
    INDEX idx_created (created_at DESC)
);

-- Alert Rules Table
CREATE TABLE IF NOT EXISTS alert_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    alert_type ENUM('LOW_BALANCE', 'LARGE_TRANSACTION', 'UNUSUAL_LOGIN', 'BILL_DUE', 'FD_MATURITY') NOT NULL,
    condition_field VARCHAR(50),
    condition_operator ENUM('EQUALS', 'GREATER_THAN', 'LESS_THAN') DEFAULT 'LESS_THAN',
    threshold_value DECIMAL(15,2),
    notification_channel ENUM('EMAIL', 'SMS', 'PUSH', 'ALL') DEFAULT 'ALL',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- ============================================
-- PHASE 7: Analytics & Categorization
-- ============================================

-- Spending Categories Table
CREATE TABLE IF NOT EXISTS spending_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(20),
    parent_category_id BIGINT,
    active BOOLEAN DEFAULT TRUE
);

-- Insert Default Categories
INSERT INTO spending_category (name, icon, color) VALUES
('Food & Dining', '🍔', '#FF6B6B'),
('Shopping', '🛍️', '#4ECDC4'),
('Transportation', '🚗', '#45B7D1'),
('Bills & Utilities', '💡', '#96CEB4'),
('Entertainment', '🎬', '#FFEAA7'),
('Healthcare', '🏥', '#DFE6E9'),
('Education', '📚', '#74B9FF'),
('Travel', '✈️', '#A29BFE'),
('Investments', '💰', '#FD79A8'),
('Others', '📦', '#FDCB6E')
ON DUPLICATE KEY UPDATE name=name;

-- Transaction Categories Mapping
CREATE TABLE IF NOT EXISTS transaction_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    transaction_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    confidence_score DECIMAL(3,2) DEFAULT 1.00,
    auto_categorized BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (transaction_id) REFERENCES transaction(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES spending_category(id) ON DELETE CASCADE,
    UNIQUE KEY unique_transaction_category (transaction_id)
);

-- Budget Plans Table
CREATE TABLE IF NOT EXISTS budget_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    month CHAR(7) NOT NULL,
    budget_limit DECIMAL(15,2) NOT NULL,
    spent_amount DECIMAL(15,2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES spending_category(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_category_month (user_id, category_id, month)
);

-- Financial Goals Table
CREATE TABLE IF NOT EXISTS financial_goal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goal_name VARCHAR(255) NOT NULL,
    target_amount DECIMAL(15,2) NOT NULL,
    current_amount DECIMAL(15,2) DEFAULT 0,
    deadline DATE,
    category VARCHAR(50),
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Create Indexes for Performance
CREATE INDEX idx_transaction_date ON transaction(transaction_date DESC);
CREATE INDEX idx_transaction_type ON transaction(transaction_type);
CREATE INDEX idx_account_customer ON account(customer_id);
CREATE INDEX idx_card_account ON card(account_id);

-- ============================================
-- Views for Analytics
-- ============================================

-- Monthly Spending Summary View
CREATE OR REPLACE VIEW monthly_spending_summary AS
SELECT 
    t.account_id,
    a.customer_id,
    DATE_FORMAT(t.transaction_date, '%Y-%m') as month,
    tc.category_id,
    sc.name as category_name,
    SUM(t.amount) as total_spent,
    COUNT(t.id) as transaction_count
FROM transaction t
LEFT JOIN account a ON t.account_id = a.id
LEFT JOIN transaction_category tc ON t.id = tc.transaction_id
LEFT JOIN spending_category sc ON tc.category_id = sc.id
WHERE t.transaction_type IN ('WITHDRAW', 'TRANSFER_OUT')
GROUP BY t.account_id, a.customer_id, month, tc.category_id, sc.name;

-- User Account Summary View
CREATE OR REPLACE VIEW user_account_summary AS
SELECT 
    c.id as customer_id,
    c.name as customer_name,
    c.email,
    COUNT(DISTINCT a.id) as total_accounts,
    SUM(a.balance) as total_balance,
    COUNT(DISTINCT ca.id) as total_cards,
    COUNT(DISTINCT fd.id) as total_fixed_deposits
FROM customer c
LEFT JOIN account a ON c.id = a.customer_id
LEFT JOIN card ca ON a.id = ca.account_id
LEFT JOIN fixed_deposit fd ON c.id = fd.user_id
GROUP BY c.id, c.name, c.email;
