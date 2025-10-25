-- Module 10: Card Management System - Schema Updates
-- This script creates the required tables for the Card Management module

USE banking_system;

-- Create Card table
CREATE TABLE IF NOT EXISTS card (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,
    card_number VARCHAR(19) NOT NULL UNIQUE,
    card_holder_name VARCHAR(255) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    card_network VARCHAR(20) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    pin VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    daily_limit DECIMAL(15,2) DEFAULT 50000.00,
    monthly_limit DECIMAL(15,2) DEFAULT 500000.00,
    daily_spent DECIMAL(15,2) DEFAULT 0.00,
    monthly_spent DECIMAL(15,2) DEFAULT 0.00,
    last_reset_date DATE,
    issued_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    blocked_at DATETIME,
    block_reason VARCHAR(500),
    card_limit DECIMAL(15,2),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES account(id),
    INDEX idx_account_id (account_id),
    INDEX idx_card_number (card_number),
    INDEX idx_status (status),
    INDEX idx_card_type (card_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create CardTransaction table
CREATE TABLE IF NOT EXISTS card_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_id BIGINT NOT NULL,
    merchant_name VARCHAR(255) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(255),
    category VARCHAR(50),
    reference_number VARCHAR(100) UNIQUE,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decline_reason VARCHAR(500),
    is_international BOOLEAN DEFAULT FALSE,
    mcc_code VARCHAR(10),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (card_id) REFERENCES card(id),
    INDEX idx_card_id (card_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_status (status),
    INDEX idx_reference_number (reference_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Verify tables were created
SELECT 'Card table created successfully' AS Status 
FROM information_schema.tables 
WHERE table_schema = 'banking_system' AND table_name = 'card';

SELECT 'CardTransaction table created successfully' AS Status 
FROM information_schema.tables 
WHERE table_schema = 'banking_system' AND table_name = 'card_transaction';
