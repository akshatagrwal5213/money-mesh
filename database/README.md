# Database Schema Updates - Card Management Module

This directory contains SQL scripts for the Card Management System (Module 10).

## Files

1. **schema-updates.sql** - Creates the required tables (`card` and `card_transaction`)
2. **sample-data.sql** - Optional sample data (commented out for safety)

## Tables Created

### `card` table
- Stores card information (card number, type, network, limits, status, etc.)
- Linked to `account` table via foreign key
- Supports DEBIT, CREDIT, and PREPAID card types
- Networks: VISA, MASTERCARD, RUPAY

### `card_transaction` table
- Stores card transaction history
- Linked to `card` table via foreign key
- Tracks merchant, amount, category, location, and status

## How to Run

### Option 1: Automatic (Recommended)
Your application is configured with `spring.jpa.hibernate.ddl-auto=update`, so the tables will be **automatically created** when you restart the backend:

```bash
# Just restart the backend application
cd /Users/akshatsanjayagrwal/Desktop/bankingsystem
./mvnw spring-boot:run
```

### Option 2: Manual SQL Execution
If you prefer to create tables manually before restarting:

```bash
# Login to MySQL
mysql -u root -p

# Run the schema update script
source /Users/akshatsanjayagrwal/Desktop/bankingsystem/database/schema-updates.sql
```

Or using command line directly:
```bash
mysql -u root -p banking_system < database/schema-updates.sql
```

## Verify Tables

After running the scripts or restarting the application, verify the tables exist:

```sql
USE banking_system;

-- Show all tables
SHOW TABLES;

-- Describe card table structure
DESC card;

-- Describe card_transaction table structure
DESC card_transaction;

-- Check if tables have any data
SELECT COUNT(*) as card_count FROM card;
SELECT COUNT(*) as transaction_count FROM card_transaction;
```

## Important Notes

1. **Do NOT insert card data manually** - Use the Card Management UI instead
   - Ensures proper PIN hashing (BCrypt)
   - Generates unique card numbers with correct network prefixes
   - Creates proper audit trail and notifications

2. **Sample data is commented out** - The `sample-data.sql` file contains commented examples only

3. **Foreign Key Dependencies**:
   - `card` table requires existing records in `account` table
   - `card_transaction` table requires existing records in `card` table

4. **Indexes Created**:
   - `card`: account_id, card_number, status, card_type
   - `card_transaction`: card_id, transaction_date, status, reference_number

## Testing the Module

After tables are created:

1. Login to the application (http://localhost:5173)
2. Navigate to "Cards" menu
3. Click "Issue New Card"
4. Select an account, card type, and network
5. Set a 4-digit PIN
6. Card will be created with:
   - Automatically generated card number
   - Randomly generated CVV
   - 5-year expiry date
   - Default limits (₹50,000 daily, ₹5,00,000 monthly)

## Rollback (if needed)

To remove the tables:

```sql
USE banking_system;
DROP TABLE IF EXISTS card_transaction;
DROP TABLE IF EXISTS card;
```

## Schema Summary

### Card Table Columns
- id, account_id, card_number, card_holder_name
- card_type (DEBIT/CREDIT/PREPAID)
- card_network (VISA/MASTERCARD/RUPAY)
- expiry_date, cvv, pin (hashed)
- status (ACTIVE/BLOCKED/EXPIRED/CANCELLED)
- daily_limit, monthly_limit, daily_spent, monthly_spent
- last_reset_date, issued_at, blocked_at, block_reason
- card_limit (for credit cards)

### Card Transaction Table Columns
- id, card_id, merchant_name, amount
- transaction_type (PURCHASE/WITHDRAWAL/REFUND/PAYMENT)
- status, location, category
- reference_number (unique), transaction_date
- decline_reason, is_international, mcc_code
