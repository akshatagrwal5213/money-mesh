-- Initialize test users with BCrypt encoded passwords
-- admin/admin123 and customer1/customer123 and testcustomer/test123
INSERT INTO app_user (username, password, role) VALUES
('admin', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'ADMIN'),
('customer1', '$2a$10$xAY.zl0MdQhGkqpOdAebBOs3hgQNJb1jZyNfQAJFr7sDgzLdDEZUe', 'CUSTOMER'),
('testcustomer', '$2a$10$7mMz7KQgJaO9t4QgBvT8XOEaHnHPR9.HuLGqnDJrZ6XyLVeGzTwE2', 'CUSTOMER');

-- Initialize test customer data
INSERT INTO customer (name, email, phone) VALUES
('Test Customer', 'customer@test.com', '1234567890'),
('John Doe', 'john@example.com', '9876543210');

-- Initialize test accounts
INSERT INTO account (account_number, balance, customer_id) VALUES
('ACC001', 10000.00, 1),
('ACC002', 25000.00, 1),
('ACC003', 5000.00, 2);

-- Initialize test cards
INSERT INTO card (card_number, type, cvv, expiry_date, card_limit, account_id) VALUES
('4532123456789012', 'DEBIT', '123', '2027-12-31', 50000.00, 1),
('5412345678901234', 'CREDIT', '456', '2026-08-31', 100000.00, 1),
('4111111111111111', 'DEBIT', '789', '2025-06-30', 25000.00, 3);