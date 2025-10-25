USE banking_system;
SET FOREIGN_KEY_CHECKS = 0;

-- Customer 1: john_doe (Profile only, no account)
INSERT INTO app_user (username, password, role) VALUES ('john_doe', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');
SET @john_user_id = LAST_INSERT_ID();
INSERT INTO customer (user_id, name, email, phone) VALUES (@john_user_id, 'John Doe', 'john.doe@email.com', '1234567890');

-- Customer 2: jane_smith (One account with transactions)
INSERT INTO app_user (username, password, role) VALUES ('jane_smith', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');
SET @jane_user_id = LAST_INSERT_ID();
INSERT INTO customer (user_id, name, email, phone) VALUES (@jane_user_id, 'Jane Smith', 'jane.smith@email.com', '2345678901');
SET @jane_customer_id = LAST_INSERT_ID();

INSERT INTO account (account_number, customer_id, user_id, account_type, balance, status, created_at) 
VALUES ('ACC1001234567', @jane_customer_id, @jane_user_id, 'SAVINGS', 75500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 6 MONTH));
SET @jane_account_id = LAST_INSERT_ID();

-- Jane's transactions
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
(@jane_account_id, 50000.00, 'DEPOSIT', 'COMPLETED', 'Initial Deposit', DATE_SUB(NOW(), INTERVAL 6 MONTH), 50000.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - Month 1', DATE_SUB(NOW(), INTERVAL 5 MONTH), 58500.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - Month 2', DATE_SUB(NOW(), INTERVAL 4 MONTH), 67000.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - Month 3', DATE_SUB(NOW(), INTERVAL 3 MONTH), 75500.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - Month 4', DATE_SUB(NOW(), INTERVAL 2 MONTH), 84000.00),
(@jane_account_id, 8500.00, 'DEPOSIT', 'COMPLETED', 'Salary - Month 5', DATE_SUB(NOW(), INTERVAL 1 MONTH), 92500.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent', DATE_SUB(NOW(), INTERVAL 150 DAY), 90000.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent', DATE_SUB(NOW(), INTERVAL 120 DAY), 87500.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent', DATE_SUB(NOW(), INTERVAL 90 DAY), 85000.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent', DATE_SUB(NOW(), INTERVAL 60 DAY), 82500.00),
(@jane_account_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Rent', DATE_SUB(NOW(), INTERVAL 30 DAY), 80000.00),
(@jane_account_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 145 DAY), 89650.00),
(@jane_account_id, -420.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 130 DAY), 89230.00),
(@jane_account_id, -380.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 115 DAY), 86850.00),
(@jane_account_id, -340.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 100 DAY), 86510.00),
(@jane_account_id, -390.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 85 DAY), 84120.00),
(@jane_account_id, -410.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 70 DAY), 83710.00),
(@jane_account_id, -370.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 55 DAY), 81340.00),
(@jane_account_id, -360.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 40 DAY), 80980.00),
(@jane_account_id, -400.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 25 DAY), 78580.00),
(@jane_account_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 10 DAY), 76230.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 140 DAY), 89500.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 110 DAY), 87000.00),
(@jane_account_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity Bill', DATE_SUB(NOW(), INTERVAL 80 DAY), 84570.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 135 DAY), 89420.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 105 DAY), 86920.00),
(@jane_account_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Internet Bill', DATE_SUB(NOW(), INTERVAL 75 DAY), 84490.00),
(@jane_account_id, -85.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant', DATE_SUB(NOW(), INTERVAL 142 DAY), 89335.00),
(@jane_account_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant', DATE_SUB(NOW(), INTERVAL 127 DAY), 89110.00),
(@jane_account_id, -95.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant', DATE_SUB(NOW(), INTERVAL 112 DAY), 86825.00),
(@jane_account_id, -200.00, 'WITHDRAWAL', 'COMPLETED', 'Entertainment', DATE_SUB(NOW(), INTERVAL 97 DAY), 86310.00),
(@jane_account_id, -60.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 138 DAY), 89360.00),
(@jane_account_id, -55.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 122 DAY), 89055.00),
(@jane_account_id, -65.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 108 DAY), 86760.00),
(@jane_account_id, -50.00, 'WITHDRAWAL', 'COMPLETED', 'Transportation', DATE_SUB(NOW(), INTERVAL 92 DAY), 86260.00),
(@jane_account_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Shopping', DATE_SUB(NOW(), INTERVAL 125 DAY), 88805.00),
(@jane_account_id, -180.00, 'WITHDRAWAL', 'COMPLETED', 'Electronics', DATE_SUB(NOW(), INTERVAL 95 DAY), 86130.00),
(@jane_account_id, -320.00, 'WITHDRAWAL', 'COMPLETED', 'Clothing', DATE_SUB(NOW(), INTERVAL 65 DAY), 84170.00),
(@jane_account_id, -730.00, 'WITHDRAWAL', 'COMPLETED', 'Various Expenses', DATE_SUB(NOW(), INTERVAL 5 DAY), 75500.00);

-- Customer 3: mike_johnson (Multiple accounts with full features)
INSERT INTO app_user (username, password, role) VALUES ('mike_johnson', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER');
SET @mike_user_id = LAST_INSERT_ID();
INSERT INTO customer (user_id, name, email, phone) VALUES (@mike_user_id, 'Mike Johnson', 'mike.johnson@email.com', '3456789012');
SET @mike_customer_id = LAST_INSERT_ID();

-- Mike's Savings Account
INSERT INTO account (account_number, customer_id, user_id, account_type, balance, status, created_at)
VALUES ('ACC2001234567', @mike_customer_id, @mike_user_id, 'SAVINGS', 125750.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 YEAR));
SET @mike_savings_id = LAST_INSERT_ID();

-- Mike's Checking Account
INSERT INTO account (account_number, customer_id, user_id, account_type, balance, status, created_at)
VALUES ('ACC2001234568', @mike_customer_id, @mike_user_id, 'CURRENT', 8500.00, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 YEAR));
SET @mike_checking_id = LAST_INSERT_ID();

-- Mike's Savings transactions
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
(@mike_savings_id, 100000.00, 'DEPOSIT', 'COMPLETED', 'Initial Deposit', DATE_SUB(NOW(), INTERVAL 2 YEAR), 100000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 12 MONTH), 112000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 11 MONTH), 124000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 10 MONTH), 136000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 9 MONTH), 148000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 8 MONTH), 160000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 7 MONTH), 172000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 6 MONTH), 184000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 5 MONTH), 196000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 4 MONTH), 208000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 3 MONTH), 220000.00),
(@mike_savings_id, 12000.00, 'DEPOSIT', 'COMPLETED', 'Salary', DATE_SUB(NOW(), INTERVAL 2 MONTH), 232000.00),
(@mike_savings_id, -50000.00, 'WITHDRAWAL', 'COMPLETED', 'Investment - Mutual Funds', DATE_SUB(NOW(), INTERVAL 9 MONTH), 98000.00),
(@mike_savings_id, -30000.00, 'WITHDRAWAL', 'COMPLETED', 'Fixed Deposit', DATE_SUB(NOW(), INTERVAL 6 MONTH), 154000.00),
(@mike_savings_id, -20000.00, 'WITHDRAWAL', 'COMPLETED', 'SIP Investment', DATE_SUB(NOW(), INTERVAL 3 MONTH), 200000.00),
(@mike_savings_id, -15000.00, 'WITHDRAWAL', 'COMPLETED', 'Car Down Payment', DATE_SUB(NOW(), INTERVAL 8 MONTH), 145000.00),
(@mike_savings_id, -8000.00, 'WITHDRAWAL', 'COMPLETED', 'Home Renovation', DATE_SUB(NOW(), INTERVAL 5 MONTH), 176000.00),
(@mike_savings_id, -5000.00, 'WITHDRAWAL', 'COMPLETED', 'Vacation Package', DATE_SUB(NOW(), INTERVAL 2 MONTH), 215000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 180 DAY), 145000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 150 DAY), 151000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 120 DAY), 173000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 90 DAY), 197000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 60 DAY), 212000.00),
(@mike_savings_id, -3000.00, 'TRANSFER', 'COMPLETED', 'Transfer to Checking', DATE_SUB(NOW(), INTERVAL 30 DAY), 229000.00),
(@mike_savings_id, -6250.00, 'WITHDRAWAL', 'COMPLETED', 'Monthly Expenses', DATE_SUB(NOW(), INTERVAL 1 MONTH), 125750.00);

-- Mike's Checking transactions
INSERT INTO transaction (account_id, amount, transaction_type, status, description, transaction_date, balance_after) VALUES
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 180 DAY), 5500.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 150 DAY), 6800.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 120 DAY), 8200.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 90 DAY), 9100.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 60 DAY), 10500.00),
(@mike_checking_id, 3000.00, 'TRANSFER', 'COMPLETED', 'From Savings', DATE_SUB(NOW(), INTERVAL 30 DAY), 11200.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 175 DAY), 2300.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 145 DAY), 3600.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 115 DAY), 5000.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 85 DAY), 5900.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 55 DAY), 7300.00),
(@mike_checking_id, -3200.00, 'WITHDRAWAL', 'COMPLETED', 'Mortgage', DATE_SUB(NOW(), INTERVAL 25 DAY), 8000.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity', DATE_SUB(NOW(), INTERVAL 170 DAY), 2050.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity', DATE_SUB(NOW(), INTERVAL 140 DAY), 3350.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity', DATE_SUB(NOW(), INTERVAL 110 DAY), 4750.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity', DATE_SUB(NOW(), INTERVAL 80 DAY), 5650.00),
(@mike_checking_id, -250.00, 'WITHDRAWAL', 'COMPLETED', 'Electricity', DATE_SUB(NOW(), INTERVAL 50 DAY), 7050.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet', DATE_SUB(NOW(), INTERVAL 168 DAY), 1930.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet', DATE_SUB(NOW(), INTERVAL 138 DAY), 3230.00),
(@mike_checking_id, -120.00, 'WITHDRAWAL', 'COMPLETED', 'Internet', DATE_SUB(NOW(), INTERVAL 108 DAY), 4630.00),
(@mike_checking_id, -450.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 160 DAY), 1480.00),
(@mike_checking_id, -480.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 130 DAY), 2750.00),
(@mike_checking_id, -420.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 100 DAY), 4210.00),
(@mike_checking_id, -500.00, 'WITHDRAWAL', 'COMPLETED', 'Groceries', DATE_SUB(NOW(), INTERVAL 70 DAY), 5030.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Insurance', DATE_SUB(NOW(), INTERVAL 165 DAY), 980.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Insurance', DATE_SUB(NOW(), INTERVAL 135 DAY), 2305.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Insurance', DATE_SUB(NOW(), INTERVAL 105 DAY), 3860.00),
(@mike_checking_id, -350.00, 'WITHDRAWAL', 'COMPLETED', 'Insurance', DATE_SUB(NOW(), INTERVAL 75 DAY), 4680.00),
(@mike_checking_id, -80.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 158 DAY), 900.00),
(@mike_checking_id, -85.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 128 DAY), 2220.00),
(@mike_checking_id, -75.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 98 DAY), 3785.00),
(@mike_checking_id, -90.00, 'WITHDRAWAL', 'COMPLETED', 'Gas', DATE_SUB(NOW(), INTERVAL 68 DAY), 4590.00),
(@mike_checking_id, -150.00, 'WITHDRAWAL', 'COMPLETED', 'Dining', DATE_SUB(NOW(), INTERVAL 155 DAY), 1330.00),
(@mike_checking_id, -95.00, 'WITHDRAWAL', 'COMPLETED', 'Restaurant', DATE_SUB(NOW(), INTERVAL 125 DAY), 2655.00),
(@mike_checking_id, -180.00, 'WITHDRAWAL', 'COMPLETED', 'Entertainment', DATE_SUB(NOW(), INTERVAL 95 DAY), 4030.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 150 DAY), 4300.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 120 DAY), 5700.00),
(@mike_checking_id, -2500.00, 'WITHDRAWAL', 'COMPLETED', 'Credit Card Payment', DATE_SUB(NOW(), INTERVAL 90 DAY), 6600.00),
(@mike_checking_id, -400.00, 'WITHDRAWAL', 'COMPLETED', 'Various Expenses', DATE_SUB(NOW(), INTERVAL 10 DAY), 8500.00);

-- Mike's investments
INSERT INTO fixed_deposit (account_id, amount, interest_rate, tenure_months, maturity_date, status, created_at) 
VALUES (@mike_savings_id, 30000.00, 6.50, 12, DATE_ADD(NOW(), INTERVAL 6 MONTH), 'ACTIVE', DATE_SUB(NOW(), INTERVAL 6 MONTH));

INSERT INTO financial_goals (customer_id, goal_name, target_amount, current_amount, target_date, priority, status) VALUES
(@mike_customer_id, 'Dream Home Purchase', 500000.00, 125750.00, DATE_ADD(NOW(), INTERVAL 3 YEAR), 'HIGH', 'IN_PROGRESS'),
(@mike_customer_id, 'Emergency Fund', 50000.00, 50000.00, NOW(), 'HIGH', 'ACHIEVED'),
(@mike_customer_id, 'Retirement Fund', 1000000.00, 56480.00, DATE_ADD(NOW(), INTERVAL 20 YEAR), 'MEDIUM', 'IN_PROGRESS'),
(@mike_customer_id, 'Vacation Fund', 15000.00, 8500.00, DATE_ADD(NOW(), INTERVAL 1 YEAR), 'LOW', 'IN_PROGRESS');

INSERT INTO budgets (customer_id, category, allocated_amount, spent_amount, month, year) VALUES
(@mike_customer_id, 'Housing', 3200.00, 3200.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Groceries', 500.00, 490.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Transportation', 400.00, 88.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Utilities', 370.00, 370.00, MONTH(NOW()), YEAR(NOW())),
(@mike_customer_id, 'Insurance', 350.00, 350.00, MONTH(NOW()), YEAR(NOW()));

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Sample customers created successfully!' as Status;
SELECT 'john_doe - Profile only (no account)' as Customer1;
SELECT 'jane_smith - Savings account with $75,500' as Customer2;
SELECT 'mike_johnson - Multiple accounts with $134,250 total' as Customer3;
SELECT 'All passwords: password123' as Note;
