-- ============================================
-- Sample Customer Data Creation Script
-- ============================================
-- Creates 3 customers:
-- 1. Profile only (no account)
-- 2. Customer with savings account and transaction history
-- 3. Customer with multiple accounts and comprehensive data

USE banking_system;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- CUSTOMER 1: Profile Only (No Account)
-- ============================================
-- Username: john_doe | Password: password123

INSERT INTO app_user (username, password, role) VALUES
('john_doe', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');

INSERT INTO customer (user_id, full_name, email, phone_number, date_of_birth, address) VALUES
(LAST_INSERT_ID(), 'John Doe', 'john.doe@email.com', '1234567890', '1990-01-15', '123 Main Street, New York, NY 10001');

-- ============================================
-- CUSTOMER 2: Jane Smith (Savings Account with History)
-- ============================================
-- Username: jane_smith | Password: password123

INSERT INTO app_user (username, password, role) VALUES
('jane_smith', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');

SET @jane_user_id = LAST_INSERT_ID();

INSERT INTO customer (user_id, full_name, email, phone_number, date_of_birth, address) VALUES
(@jane_user_id, 'Jane Smith', 'jane.smith@email.com', '2345678901', '1992-05-20', '456 Oak Avenue, Los Angeles, CA 90001');

SET @jane_customer_id = LAST_INSERT_ID();

-- Jane's Savings Account
INSERT INTO account (account_number, customer_id, account_type, balance, status, created_at) VALUES
('ACC1001234567', @jane_customer_id, 'SAVINGS', 75500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 6 MONTH));

SET @jane_account_id = LAST_INSERT_ID();

-- Jane's Debit Card
INSERT INTO card (card_number, card_holder_name, account_id, cvv, expiry_date, card_type, credit_limit, status, issued_date) VALUES
('4532123456789012', 'JANE SMITH', @jane_account_id, '123', DATE_ADD(NOW(), INTERVAL 3 YEAR), 'DEBIT', 0.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 5 MONTH));

-- Jane's Transactions (Income, Expenses, Transfers)
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
-- Initial deposit
(@jane_account_id, 50000.00, 'DEPOSIT', 'COMPLETED', 'Initial Deposit', DATE_SUB(NOW(), INTERVAL 6 MONTH), 50000.00),

-- Salary deposits (last 6 months)
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - June', DATE_SUB(NOW(), INTERVAL 5 MONTH), 58500.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - July', DATE_SUB(NOW(), INTERVAL 4 MONTH), 67000.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - August', DATE_SUB(NOW(), INTERVAL 3 MONTH), 75500.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - September', DATE_SUB(NOW(), INTERVAL 2 MONTH), 84000.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - October', DATE_SUB(NOW(), INTERVAL 1 MONTH), 92500.00),

-- Rent payments
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent Payment', DATE_SUB(NOW(), INTERVAL 150 DAY), 90000.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent Payment', DATE_SUB(NOW(), INTERVAL 120 DAY), 87500.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent Payment', DATE_SUB(NOW(), INTERVAL 90 DAY), 85000.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent Payment', DATE_SUB(NOW(), INTERVAL 60 DAY), 82500.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent Payment', DATE_SUB(NOW(), INTERVAL 30 DAY), 80000.00),

-- Utility bills
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 140 DAY), 89850.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 110 DAY), 87350.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 80 DAY), 84850.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 135 DAY), 89770.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 105 DAY), 87270.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 75 DAY), 84770.00),

-- Groceries
(@jane_account_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Walmart', DATE_SUB(NOW(), INTERVAL 145 DAY), 89420.00),
(@jane_account_id, -420.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Whole Foods', DATE_SUB(NOW(), INTERVAL 130 DAY), 89000.00),
(@jane_account_id, -380.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Costco', DATE_SUB(NOW(), INTERVAL 115 DAY), 86620.00),
(@jane_account_id, -340.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Trader Joes', DATE_SUB(NOW(), INTERVAL 100 DAY), 86280.00),
(@jane_account_id, -390.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Walmart', DATE_SUB(NOW(), INTERVAL 85 DAY), 83890.00),
(@jane_account_id, -410.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Whole Foods', DATE_SUB(NOW(), INTERVAL 70 DAY), 83480.00),
(@jane_account_id, -370.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Costco', DATE_SUB(NOW(), INTERVAL 55 DAY), 81110.00),
(@jane_account_id, -360.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Walmart', DATE_SUB(NOW(), INTERVAL 40 DAY), 80750.00),
(@jane_account_id, -400.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Whole Foods', DATE_SUB(NOW(), INTERVAL 25 DAY), 78350.00),
(@jane_account_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Target', DATE_SUB(NOW(), INTERVAL 10 DAY), 76000.00),

-- Dining & Entertainment
(@jane_account_id, -85.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Olive Garden', DATE_SUB(NOW(), INTERVAL 142 DAY), 89335.00),
(@jane_account_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Cheesecake Factory', DATE_SUB(NOW(), INTERVAL 127 DAY), 88880.00),
(@jane_account_id, -95.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Chipotle', DATE_SUB(NOW(), INTERVAL 112 DAY), 86525.00),
(@jane_account_id, -200.00, 'WITHDRAWAL', 'COMPLETED', 'Movie Tickets & Dinner', DATE_SUB(NOW(), INTERVAL 97 DAY), 86080.00),
(@jane_account_id, -75.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Starbucks', DATE_SUB(NOW(), INTERVAL 82 DAY), 83815.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Concert Tickets', DATE_SUB(NOW(), INTERVAL 67 DAY), 83330.00),
(@jane_account_id, -110.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Red Lobster', DATE_SUB(NOW(), INTERVAL 52 DAY), 81000.00),
(@jane_account_id, -90.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Panera Bread', DATE_SUB(NOW(), INTERVAL 37 DAY), 80660.00),
(@jane_account_id, -180.00, 'WITHDRAWAL', 'COMPLETED', 'Theater Show', DATE_SUB(NOW(), INTERVAL 22 DAY), 78170.00),
(@jane_account_id, -125.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Outback Steakhouse', DATE_SUB(NOW(), INTERVAL 7 DAY), 75875.00),

-- Shopping
(@jane_account_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Clothing - Macys', DATE_SUB(NOW(), INTERVAL 125 DAY), 88630.00),
(@jane_account_id, -180.00, 'WITHDRAWAL', 'COMPLETED', 'Electronics - Best Buy', DATE_SUB(NOW(), INTERVAL 95 DAY), 85900.00),
(@jane_account_id, -320.00, 'WITHDRAWAL', 'COMPLETED', 'Clothing - Nordstrom', DATE_SUB(NOW(), INTERVAL 65 DAY), 83010.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Books - Amazon', DATE_SUB(NOW(), INTERVAL 35 DAY), 80510.00),
(@jane_account_id, -275.00, 'WITHDRAWAL', 'COMPLETED', 'Home Decor - IKEA', DATE_SUB(NOW(), INTERVAL 15 DAY), 75600.00),

-- Healthcare
(@jane_account_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Pharmacy - CVS', DATE_SUB(NOW(), INTERVAL 118 DAY), 86405.00),
(@jane_account_id, -200.00, 'WITHDRAWAL', 'COMPLETED', 'Doctor Visit Copay', DATE_SUB(NOW(), INTERVAL 88 DAY), 83615.00),
(@jane_account_id, -95.00, 'WITHDRAWAL', 'COMPLETED', 'Pharmacy - Walgreens', DATE_SUB(NOW(), INTERVAL 58 DAY), 80915.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Dental Checkup', DATE_SUB(NOW(), INTERVAL 28 DAY), 78200.00),

-- Transportation
(@jane_account_id, -60.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - Shell', DATE_SUB(NOW(), INTERVAL 138 DAY), 89710.00),
(@jane_account_id, -55.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - Chevron', DATE_SUB(NOW(), INTERVAL 122 DAY), 88575.00),
(@jane_account_id, -65.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - BP', DATE_SUB(NOW(), INTERVAL 108 DAY), 86460.00),
(@jane_account_id, -50.00, 'WITHDRAWAL', 'COMPLETED', 'Uber Rides', DATE_SUB(NOW(), INTERVAL 92 DAY), 85850.00),
(@jane_account_id, -58.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - Shell', DATE_SUB(NOW(), INTERVAL 78 DAY), 83757.00),
(@jane_account_id, -45.00, 'WITHDRAWAL', 'COMPLETED', 'Uber Rides', DATE_SUB(NOW(), INTERVAL 62 DAY), 82965.00),
(@jane_account_id, -62.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - Chevron', DATE_SUB(NOW(), INTERVAL 48 DAY), 80938.00),
(@jane_account_id, -55.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - BP', DATE_SUB(NOW(), INTERVAL 32 DAY), 80455.00),
(@jane_account_id, -40.00, 'WITHDRAWAL', 'COMPLETED', 'Parking Fee', DATE_SUB(NOW(), INTERVAL 18 DAY), 75560.00),
(@jane_account_id, -60.00, 'WITHDRAWAL', 'COMPLETED', 'Gas - Shell', DATE_SUB(NOW(), INTERVAL 5 DAY), 75500.00);

-- ============================================
-- CUSTOMER 3: Mike Johnson (Multiple Accounts & Comprehensive Data)
-- ============================================
-- Username: mike_johnson | Password: password123

INSERT INTO app_user (username, password, role) VALUES
('mike_johnson', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');

SET @mike_user_id = LAST_INSERT_ID();

INSERT INTO customer (user_id, full_name, email, phone_number, date_of_birth, address) VALUES
(@mike_user_id, 'Mike Johnson', 'mike.johnson@email.com', '3456789012', '1988-08-10', '789 Pine Road, Chicago, IL 60601');

SET @mike_customer_id = LAST_INSERT_ID();

-- Mike's Savings Account (Primary)
INSERT INTO account (account_number, customer_id, account_type, balance, status, created_at) VALUES
('ACC2001234567', @mike_customer_id, 'SAVINGS', 125750.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 YEAR));

SET @mike_savings_id = LAST_INSERT_ID();

-- Mike's Checking Account
INSERT INTO account (account_number, customer_id, account_type, balance, status, created_at) VALUES
('ACC2001234568', @mike_customer_id, 'CHECKING', 8500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 YEAR));

SET @mike_checking_id = LAST_INSERT_ID();

-- Mike's Debit Card
INSERT INTO card (card_number, card_holder_name, account_id, cvv, expiry_date, card_type, credit_limit, status, issued_date) VALUES
('4532234567890123', 'MIKE JOHNSON', @mike_checking_id, '456', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'DEBIT', 0.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 MONTH));

SET @mike_debit_card_id = LAST_INSERT_ID();

-- Mike's Credit Card
INSERT INTO card (card_number, card_holder_name, account_id, cvv, expiry_date, card_type, credit_limit, outstanding_balance, status, issued_date) VALUES
('5412345678901234', 'MIKE JOHNSON', @mike_checking_id, '789', DATE_ADD(NOW(), INTERVAL 4 YEAR), 'CREDIT', 50000.00, 12500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 18 MONTH));

-- Mike's Savings Account Transactions
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
-- Initial deposit
(@mike_savings_id, 100000.00, 'DEPOSIT', 'COMPLETED', 'Initial Deposit', DATE_SUB(NOW(), INTERVAL 2 YEAR), 100000.00),

-- Regular deposits (last 12 months)
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - November 2024', DATE_SUB(NOW(), INTERVAL 12 MONTH), 112000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - December 2024', DATE_SUB(NOW(), INTERVAL 11 MONTH), 124000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - January 2025', DATE_SUB(NOW(), INTERVAL 10 MONTH), 136000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - February 2025', DATE_SUB(NOW(), INTERVAL 9 MONTH), 148000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - March 2025', DATE_SUB(NOW(), INTERVAL 8 MONTH), 160000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - April 2025', DATE_SUB(NOW(), INTERVAL 7 MONTH), 172000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - May 2025', DATE_SUB(NOW(), INTERVAL 6 MONTH), 184000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - June 2025', DATE_SUB(NOW(), INTERVAL 5 MONTH), 196000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - July 2025', DATE_SUB(NOW(), INTERVAL 4 MONTH), 208000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - August 2025', DATE_SUB(NOW(), INTERVAL 3 MONTH), 220000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary - September 2025', DATE_SUB(NOW(), INTERVAL 2 MONTH), 232000.00),

-- Investment withdrawals
(@mike_savings_id, -50000.00, 'WITHDRAWAL', 'COMPLETED', 'Investment - Mutual Funds', DATE_SUB(NOW(), INTERVAL 9 MONTH), 148000.00),
(@mike_savings_id, -30000.00, 'WITHDRAWAL', 'COMPLETED', 'Fixed Deposit', DATE_SUB(NOW(), INTERVAL 6 MONTH), 166000.00),
(@mike_savings_id, -20000.00, 'WITHDRAWAL', 'COMPLETED', 'SIP Investment', DATE_SUB(NOW(), INTERVAL 3 MONTH), 200000.00),

-- Large purchases
(@mike_savings_id, -15000.00, 'WITHDRAWAL', 'COMPLETED', 'Car Down Payment', DATE_SUB(NOW(), INTERVAL 8 MONTH), 145000.00),
(@mike_savings_id, -8000.00, 'WITHDRAWAL', 'COMPLETED', 'Home Renovation', DATE_SUB(NOW(), INTERVAL 5 MONTH), 176000.00),
(@mike_savings_id, -5000.00, 'WITHDRAWAL', 'COMPLETED', 'Vacation Package', DATE_SUB(NOW(), INTERVAL 2 MONTH), 215000.00),

-- Transfers to checking
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 180 DAY), 145000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 150 DAY), 155000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 120 DAY), 173000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 90 DAY), 197000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 60 DAY), 212000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking Account', DATE_SUB(NOW(), INTERVAL 30 DAY), 229000.00);

-- Mike's Checking Account Transactions (Daily expenses)
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
-- Regular transfers from savings
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 180 DAY), 5500.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 150 DAY), 6800.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 120 DAY), 8200.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 90 DAY), 9100.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 60 DAY), 10500.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'Transfer from Savings', DATE_SUB(NOW(), INTERVAL 30 DAY), 11200.00),

-- Monthly expenses
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 175 DAY), 2300.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 145 DAY), 3600.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 115 DAY), 5000.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 85 DAY), 5900.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 55 DAY), 7300.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage Payment', DATE_SUB(NOW(), INTERVAL 25 DAY), 8000.00),

-- Utilities
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 170 DAY), 2050.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 140 DAY), 3350.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 110 DAY), 4750.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 80 DAY), 5650.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 50 DAY), 7050.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 20 DAY), 7750.00),

(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 168 DAY), 1930.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 138 DAY), 3230.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 108 DAY), 4630.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 78 DAY), 5530.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 48 DAY), 6930.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet & Cable', DATE_SUB(NOW(), INTERVAL 18 DAY), 7630.00),

-- Groceries & Dining (more frequent)
(@mike_checking_id, -450.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Whole Foods', DATE_SUB(NOW(), INTERVAL 160 DAY), 1480.00),
(@mike_checking_id, -480.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Costco', DATE_SUB(NOW(), INTERVAL 130 DAY), 2750.00),
(@mike_checking_id, -420.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Walmart', DATE_SUB(NOW(), INTERVAL 100 DAY), 4210.00),
(@mike_checking_id, -500.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Whole Foods', DATE_SUB(NOW(), INTERVAL 70 DAY), 5030.00),
(@mike_checking_id, -460.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Trader Joes', DATE_SUB(NOW(), INTERVAL 40 DAY), 6470.00),
(@mike_checking_id, -490.00, 'WITHDRAWAL', 'COMPLETED', 'Grocery Shopping - Costco', DATE_SUB(NOW(), INTERVAL 10 DAY), 7140.00),

(@mike_checking_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Fine Dining', DATE_SUB(NOW(), INTERVAL 155 DAY), 1330.00),
(@mike_checking_id, -95.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Lunch', DATE_SUB(NOW(), INTERVAL 125 DAY), 2655.00),
(@mike_checking_id, -180.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Date Night', DATE_SUB(NOW(), INTERVAL 95 DAY), 4030.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Family Dinner', DATE_SUB(NOW(), INTERVAL 65 DAY), 4910.00),
(@mike_checking_id, -200.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Anniversary Dinner', DATE_SUB(NOW(), INTERVAL 35 DAY), 6270.00),
(@mike_checking_id, -140.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant - Weekend Brunch', DATE_SUB(NOW(), INTERVAL 5 DAY), 8000.00),

-- Insurance
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 165 DAY), 980.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 135 DAY), 2305.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 105 DAY), 3860.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 75 DAY), 4680.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 45 DAY), 6120.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Car Insurance Premium', DATE_SUB(NOW(), INTERVAL 15 DAY), 7290.00),

-- Gas & Transportation
(@mike_checking_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 158 DAY), 900.00),
(@mike_checking_id, -85.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 128 DAY), 2220.00),
(@mike_checking_id, -75.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 98 DAY), 3785.00),
(@mike_checking_id, -90.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 68 DAY), 4590.00),
(@mike_checking_id, -82.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 38 DAY), 5988.00),
(@mike_checking_id, -88.00, 'WITHDRAWAL', 'COMPLETED', 'Gas Station', DATE_SUB(NOW(), INTERVAL 8 DAY), 7912.00),

-- Credit card payments
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 150 DAY), 4300.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 120 DAY), 5700.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 90 DAY), 6600.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 60 DAY), 8000.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 30 DAY), 8700.00);

-- Mike's Fixed Deposit
INSERT INTO fixed_deposit (account_id, amount, interest_rate, tenure_months, maturity_date, status, created_at) VALUES
(@mike_savings_id, 30000.00, 6.50, 12, DATE_ADD(NOW(), INTERVAL 6 MONTH), 'ACTIVE', DATE_SUB(NOW(), INTERVAL 6 MONTH));

-- Mike's SIP Investment
INSERT INTO sip_investments (account_id, fund_name, monthly_amount, start_date, end_date, status, total_invested, current_value) VALUES
(@mike_savings_id, 'Vanguard S&P 500 Index Fund', 2000.00, DATE_SUB(NOW(), INTERVAL 3 MONTH), DATE_ADD(NOW(), INTERVAL 9 MONTH), 'ACTIVE', 6000.00, 6480.00);

-- Mike's Mutual Fund Holdings
INSERT INTO mutual_fund_holdings (account_id, fund_name, units, purchase_price, current_price, purchase_date) VALUES
(@mike_savings_id, 'Fidelity Growth Fund', 250.00, 200.00, 218.50, DATE_SUB(NOW(), INTERVAL 9 MONTH)),
(@mike_savings_id, 'T. Rowe Price Blue Chip', 180.00, 300.00, 325.80, DATE_SUB(NOW(), INTERVAL 9 MONTH));

-- Mike's Financial Goals
INSERT INTO financial_goals (customer_id, goal_name, target_amount, current_amount, target_date, priority, status) VALUES
(@mike_customer_id, 'Dream Home Purchase', 500000.00, 125750.00, DATE_ADD(NOW(), INTERVAL 3 YEAR), 'HIGH', 'IN_PROGRESS'),
(@mike_customer_id, 'Emergency Fund', 50000.00, 50000.00, NOW(), 'HIGH', 'ACHIEVED'),
(@mike_customer_id, 'Retirement Fund', 1000000.00, 56480.00, DATE_ADD(NOW(), INTERVAL 20 YEAR), 'MEDIUM', 'IN_PROGRESS'),
(@mike_customer_id, 'Vacation Fund', 15000.00, 8500.00, DATE_ADD(NOW(), INTERVAL 1 YEAR), 'LOW', 'IN_PROGRESS');

-- Mike's Budget
INSERT INTO budgets (customer_id, category, allocated_amount, spent_amount, month, year) VALUES
(@mike_customer_id, 'Housing', 3200.00, 3200.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Groceries', 500.00, 490.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Dining', 300.00, 140.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Transportation', 400.00, 88.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Utilities', 370.00, 370.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Insurance', 350.00, 350.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Entertainment', 200.00, 0.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Healthcare', 150.00, 0.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Savings', 5000.00, 0.00, MONTH(NOW()), YEAR(NOW()));

-- Mike's Credit Score
INSERT INTO credit_scores (customer_id, score, score_date, factors) VALUES
(@mike_customer_id, 785, NOW(), 'Excellent payment history, Low credit utilization, Long credit history');

-- Mike's Reward Points
INSERT INTO reward_points (customer_id, points_balance, total_earned, total_redeemed, last_updated) VALUES
(@mike_customer_id, 12500, 18750, 6250, NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- SUMMARY
-- ============================================

SELECT '============================================' as '';
SELECT 'SAMPLE CUSTOMERS CREATED SUCCESSFULLY!' as '';
SELECT '============================================' as '';
SELECT '' as '';
SELECT 'CUSTOMER 1: Profile Only (No Account)' as '';
SELECT '  Username: john_doe' as '';
SELECT '  Password: password123' as '';
SELECT '  Status: Profile created, no bank account' as '';
SELECT '' as '';
SELECT 'CUSTOMER 2: Jane Smith (Single Account)' as '';
SELECT '  Username: jane_smith' as '';
SELECT '  Password: password123' as '';
SELECT '  Account: Savings (ACC1001234567)' as '';
SELECT CONCAT('  Balance: $', FORMAT(75500.00, 2)) as '';
SELECT '  Transactions: 60+ transactions with full history' as '';
SELECT '  Card: 1 Debit Card' as '';
SELECT '' as '';
SELECT 'CUSTOMER 3: Mike Johnson (Multiple Accounts)' as '';
SELECT '  Username: mike_johnson' as '';
SELECT '  Password: password123' as '';
SELECT '  Accounts: Savings + Checking' as '';
SELECT CONCAT('  Savings Balance: $', FORMAT(125750.00, 2)) as '';
SELECT CONCAT('  Checking Balance: $', FORMAT(8500.00, 2)) as '';
SELECT '  Transactions: 100+ transactions across accounts' as '';
SELECT '  Cards: 1 Debit + 1 Credit Card' as '';
SELECT '  Investments: Fixed Deposit, SIP, Mutual Funds' as '';
SELECT '  Features: Goals, Budget, Credit Score, Rewards' as '';
SELECT '' as '';
SELECT '============================================' as '';
SELECT 'All customers ready for testing!' as '';
SELECT '============================================' as '';

-- Show counts
SELECT '' as '';
SELECT 'DATABASE COUNTS:' as '';
SELECT 
    'Total Users' as 'Entity',
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
    'Total Fixed Deposits',
    COUNT(*)
FROM fixed_deposit
UNION ALL
SELECT 
    'Total SIP Investments',
    COUNT(*)
FROM sip_investments
UNION ALL
SELECT 
    'Total Mutual Funds',
    COUNT(*)
FROM mutual_fund_holdings
UNION ALL
SELECT 
    'Total Financial Goals',
    COUNT(*)
FROM financial_goals
UNION ALL
SELECT 
    'Total Budgets',
    COUNT(*)
FROM budgets;
