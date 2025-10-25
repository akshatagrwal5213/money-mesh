-- Delete all data except for the admin user and their related records
-- Replace 'admin' with your actual admin username if different

SET FOREIGN_KEY_CHECKS = 0;

-- Get admin user id
SELECT id INTO @admin_user_id FROM app_user WHERE username = 'admin';
-- Get admin customer id
SELECT id INTO @admin_customer_id FROM customer WHERE user_id = @admin_user_id;

-- Delete from child tables first (adjust table/column names as needed)
DELETE FROM transaction WHERE account_id IN (SELECT id FROM account WHERE customer_id != @admin_customer_id);
DELETE FROM card WHERE account_id IN (SELECT id FROM account WHERE customer_id != @admin_customer_id);
DELETE FROM pending_transfer WHERE account_id IN (SELECT id FROM account WHERE customer_id != @admin_customer_id);
DELETE FROM account WHERE customer_id != @admin_customer_id;
DELETE FROM user_preferences WHERE customer_id != @admin_customer_id;
DELETE FROM customer WHERE id != @admin_customer_id;
DELETE FROM app_user WHERE id != @admin_user_id;

SET FOREIGN_KEY_CHECKS = 1;

-- Optional: Clean up orphaned records in other tables if needed
-- DELETE FROM ... WHERE ...

-- Done
