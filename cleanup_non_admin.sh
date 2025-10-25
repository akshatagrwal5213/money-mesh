#!/usr/bin/env bash
set -euo pipefail

# Interactive cleanup script: deletes all non-admin users/customers/accounts while keeping admin
# Usage: ./cleanup_non_admin.sh

DEFAULT_ADMIN_USER="admin"

read -rp "MySQL host (default: localhost): " DB_HOST
DB_HOST=${DB_HOST:-localhost}
read -rp "MySQL port (default: 3306): " DB_PORT
DB_PORT=${DB_PORT:-3306}
read -rp "Database name: " DB_NAME
if [ -z "$DB_NAME" ]; then
  echo "Database name is required. Exiting."
  exit 1
fi
read -rp "MySQL user (default: root): " DB_USER
DB_USER=${DB_USER:-root}
read -rsp "MySQL password: " DB_PASS
echo
read -rp "Admin username to preserve (default: ${DEFAULT_ADMIN_USER}): " ADMIN_USER
ADMIN_USER=${ADMIN_USER:-$DEFAULT_ADMIN_USER}

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="./db_backups_$TIMESTAMP"
mkdir -p "$BACKUP_DIR"

echo "Backing up relevant tables to $BACKUP_DIR..."
# Adjust table list as per schema
TABLES=(app_user customer account transaction card pending_transfer user_preferences device_sessions audit_logs)
for t in "${TABLES[@]}"; do
  echo "Dumping $t..."
  mysqldump -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" "$t" > "$BACKUP_DIR/${t}.sql"
done

# Build the SQL to run
SQL_FILE="$BACKUP_DIR/cleanup_non_admin_generated.sql"
cat > "$SQL_FILE" <<SQL
SET FOREIGN_KEY_CHECKS = 0;

-- get admin user id and customer id
SELECT id INTO @admin_user_id FROM app_user WHERE username = '${ADMIN_USER}';
SELECT id INTO @admin_customer_id FROM customer WHERE user_id = @admin_user_id;

-- delete child records linked to non-admin customers
DELETE FROM transaction WHERE account_id IN (SELECT id FROM account WHERE customer_id IS NOT NULL AND customer_id != @admin_customer_id);
DELETE FROM card WHERE account_id IN (SELECT id FROM account WHERE customer_id IS NOT NULL AND customer_id != @admin_customer_id);
DELETE FROM pending_transfer WHERE account_id IN (SELECT id FROM account WHERE customer_id IS NOT NULL AND customer_id != @admin_customer_id);

-- accounts
DELETE FROM account WHERE customer_id IS NOT NULL AND customer_id != @admin_customer_id;

-- user preferences, device sessions, audit logs for non-admin customers/users
DELETE FROM user_preferences WHERE customer_id IS NOT NULL AND customer_id != @admin_customer_id;
DELETE FROM device_sessions WHERE user_id IS NOT NULL AND user_id != @admin_user_id;
DELETE FROM audit_logs WHERE user_id IS NOT NULL AND user_id != @admin_user_id;

-- customers and users (keep admin)
DELETE FROM customer WHERE id IS NOT NULL AND id != @admin_customer_id;
DELETE FROM app_user WHERE id IS NOT NULL AND id != @admin_user_id;

SET FOREIGN_KEY_CHECKS = 1;
SQL

cat "$SQL_FILE"

echo
read -rp "Proceed with executing the above SQL against $DB_NAME on $DB_HOST:$DB_PORT? Type 'YES' to continue: " CONFIRM
if [ "$CONFIRM" != "YES" ]; then
  echo "Aborted by user. No changes made."
  exit 0
fi

# Execute the SQL
mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$SQL_FILE"

echo "Cleanup complete. Backups are saved in $BACKUP_DIR"

echo "You may want to restart the backend server now."

exit 0
