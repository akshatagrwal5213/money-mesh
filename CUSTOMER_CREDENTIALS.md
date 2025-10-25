# 🔑 Banking System - Customer Credentials

## Quick Reference Guide

---

## 👥 All Login Credentials

### Admin Account
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Administrator
- **Purpose:** System administration and management

---

### Customer Accounts

#### 1. Akshat Agrawal - Profile Only
- **Username:** `akshat_agrawal`
- **Password:** `admin123`
- **Email:** akshat.agrawal@email.com
- **Phone:** 9876543210
- **Accounts:** 0 (No bank accounts)
- **Balance:** ₹0
- **Purpose:** Test customer profile without accounts, limited navigation

---

#### 2. Kushagra Tripathi - Single Account Holder
- **Username:** `kushagra_tripathi`
- **Password:** `admin123`
- **Email:** kushagra.tripathi@email.com
- **Phone:** 9876543211
- **Accounts:** 1
  - **Savings Account:** ACC1001234567
  - **Balance:** ₹75,500.00
  - **Transactions:** 76 transactions (6 months of activity)
- **Purpose:** Test single account features, transaction history, budgeting

---

#### 3. Manik Sehrawat - Premium Account Holder
- **Username:** `manik_sehrawat`
- **Password:** `admin123`
- **Email:** manik.sehrawat@email.com
- **Phone:** 9876543212
- **Accounts:** 2
  - **Savings Account:** ACC2001234567 - ₹125,750.00 (37 transactions)
  - **Current Account:** ACC2001234568 - ₹8,500.00 (32 transactions)
- **Total Balance:** ₹134,250.00
- **Additional Features:**
  - 1 Fixed Deposit (₹30,000 @ 6.5%)
  - 4 Financial Goals
  - 5 Budget Categories
- **Purpose:** Test multi-account features, transfers, investments, goals, budgets

---

## 🚀 Quick Start

### 1. Start the Application
```bash
cd /Users/akshatsanjayagrwal/Desktop/bankingsystem
./start-all.sh
```

### 2. Access URLs
- **Frontend:** http://localhost:5175
- **Backend API:** http://localhost:8080

### 3. Test Each Customer

**Akshat (No Account):**
- Login → See limited navigation → Create first account

**Kushagra (Single Account):**
- Login → View account → Check 76 transactions → Test analytics

**Manik (Premium):**
- Login → View 2 accounts → Test transfers → Check FD, goals, budgets

---

## 📊 Account Summary

| Customer | Username | Accounts | Total Balance | Transactions |
|----------|----------|----------|---------------|--------------|
| Akshat Agrawal | `akshat_agrawal` | 0 | ₹0 | 0 |
| Kushagra Tripathi | `kushagra_tripathi` | 1 | ₹75,500 | 76 |
| Manik Sehrawat | `manik_sehrawat` | 2 | ₹134,250 | 69 |

---

## 💡 Key Testing Points

✅ **Akshat Agrawal:**
- Limited navigation (only Banking + Insurance visible)
- Dashboard shows "Create First Account" message
- Cannot access investments, loans, analytics without account

✅ **Kushagra Tripathi:**
- Full navigation menu available
- Rich transaction history (salaries, rent, groceries, utilities)
- Can test budgets and spending analytics
- Single account dashboard view

✅ **Manik Sehrawat:**
- Multi-account dashboard with switching
- Inter-account transfers
- Fixed deposit management
- Financial goal tracking (Dream Home, Emergency Fund, Retirement, Vacation)
- Budget monitoring (Housing, Food, Transportation, Utilities, Insurance)
- Premium banking features

---

## 🔒 Security Note

All customer passwords are `admin123` (BCrypt hashed in database)

**Hash:** `$2a$10$AucBBKk3DpsqEWAMa.0ZB.y4BBNcRBW6yICxKZ1f32Zgy0syBM1jK`

---

*Last Updated: October 20, 2025*
*Currency: Indian Rupees (₹)*
