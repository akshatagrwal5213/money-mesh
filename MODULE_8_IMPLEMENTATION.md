# Module 8: Insurance & Protection Management - Implementation Guide

## Overview
Module 8 implements a comprehensive Insurance and Protection Management system with policy lifecycle management, premium payments, claims processing, and admin approval workflows.

**Implementation Date**: January 2025  
**Total Files Created**: 19 (16 backend + 3 frontend)  
**Database Tables Added**: 3 (insurance_policies, insurance_claims, insurance_premium_payments)  
**API Endpoints**: 14 REST endpoints  
**Compilation Status**: ✅ Backend SUCCESS (221 files, 0 errors) | ✅ Frontend SUCCESS (0 TypeScript errors in Module 8 files)

---

## 🏗️ Architecture

### Insurance Types Supported (10)
1. **LIFE** - Life insurance coverage
2. **HEALTH** - Medical insurance
3. **TERM** - Term life insurance
4. **AUTO** - Vehicle insurance
5. **HOME** - Home/property insurance
6. **TRAVEL** - Travel insurance
7. **ACCIDENT** - Personal accident insurance
8. **CRITICAL_ILLNESS** - Critical illness coverage
9. **DISABILITY** - Disability insurance
10. **LOAN_PROTECTION** - Loan protection insurance

### Policy Lifecycle States (8)
- `PENDING_APPROVAL` - New application awaiting review
- `ACTIVE` - Policy is active and in force
- `PENDING_PAYMENT` - Payment due for activation/renewal
- `LAPSED` - Policy lapsed due to non-payment
- `EXPIRED` - Policy term ended
- `CANCELLED` - Policy cancelled by user
- `REJECTED` - Application rejected by admin
- `MATURED` - Policy completed full term

### Claim Processing States (7)
- `SUBMITTED` - Claim filed, awaiting review
- `UNDER_REVIEW` - Claim being assessed
- `PENDING_DOCUMENTS` - Additional documents required
- `APPROVED` - Claim approved
- `REJECTED` - Claim denied
- `PAID` - Claim amount paid
- `CLOSED` - Claim processing completed

### Premium Payment Frequencies (5)
- `MONTHLY` - Monthly payments
- `QUARTERLY` - Quarterly payments
- `HALF_YEARLY` - Half-yearly payments
- `YEARLY` - Annual payments
- `ONE_TIME` - Single premium payment

---

## 📊 Database Schema

### Table: insurance_policies
```sql
CREATE TABLE insurance_policies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    insurance_type VARCHAR(50) NOT NULL,
    coverage_amount DECIMAL(19,2) NOT NULL,
    premium_amount DECIMAL(19,2) NOT NULL,
    premium_frequency VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    next_premium_due_date DATE,
    term_years INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    nominee_name VARCHAR(100),
    nominee_relationship VARCHAR(50),
    nominee_contact VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
```

### Table: insurance_claims
```sql
CREATE TABLE insurance_claims (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    policy_id BIGINT NOT NULL,
    claim_amount DECIMAL(19,2) NOT NULL,
    incident_date DATE NOT NULL,
    claim_date DATE NOT NULL,
    description TEXT,
    hospital_name VARCHAR(200),
    doctor_name VARCHAR(100),
    documents_submitted TEXT,
    status VARCHAR(30) NOT NULL,
    approved_amount DECIMAL(19,2),
    rejection_reason TEXT,
    submitted_date TIMESTAMP,
    reviewed_date TIMESTAMP,
    approved_date TIMESTAMP,
    paid_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(id)
);
```

### Table: insurance_premium_payments
```sql
CREATE TABLE insurance_premium_payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_reference VARCHAR(50) UNIQUE NOT NULL,
    policy_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_date DATE NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    account_id BIGINT,
    transaction_id VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(id),
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);
```

---

## 🎯 Premium Calculation Algorithm

### Base Rates by Insurance Type
```java
LIFE: 5.0%
HEALTH: 8.0%
TERM: 3.0%
AUTO: 6.0%
HOME: 4.0%
TRAVEL: 10.0%
ACCIDENT: 5.0%
CRITICAL_ILLNESS: 12.0%
DISABILITY: 7.0%
LOAN_PROTECTION: 4.0%
```

### Calculation Formula
```
Annual Premium = (Coverage Amount × Base Rate) / 100

Adjusted Premium = switch(Frequency) {
    MONTHLY: Annual Premium / 12
    QUARTERLY: Annual Premium / 4
    HALF_YEARLY: Annual Premium / 2
    YEARLY: Annual Premium
    ONE_TIME: Annual Premium × Term Years
}
```

### Example Calculations
- **Life Insurance**: Coverage ₹50,00,000, 20 years, Monthly
  - Annual: 50,00,000 × 5% = ₹2,50,000
  - Monthly: ₹2,50,000 / 12 = ₹20,833.33

- **Health Insurance**: Coverage ₹10,00,000, 5 years, Yearly
  - Annual: 10,00,000 × 8% = ₹80,000

- **Term Insurance**: Coverage ₹1,00,00,000, 30 years, Quarterly
  - Annual: 1,00,00,000 × 3% = ₹3,00,000
  - Quarterly: ₹3,00,000 / 4 = ₹75,000

---

## 🔌 API Endpoints

### Customer Endpoints (9)

#### 1. Apply for Insurance Policy
```
POST /api/insurance/apply
Authorization: Bearer <token>

Request Body:
{
  "insuranceType": "HEALTH",
  "coverageAmount": 1000000,
  "termYears": 5,
  "premiumFrequency": "YEARLY",
  "nomineeName": "Jane Doe",
  "nomineeRelationship": "Spouse",
  "nomineeContact": "+919876543210"
}

Response:
{
  "policyNumber": "POL-2025-001234",
  "insuranceType": "HEALTH",
  "coverageAmount": 1000000,
  "premiumAmount": 80000,
  "premiumFrequency": "YEARLY",
  "startDate": "2025-01-15",
  "endDate": "2030-01-15",
  "status": "PENDING_APPROVAL"
}
```

#### 2. Get All User Policies
```
GET /api/insurance/policies
Authorization: Bearer <token>

Response: List<PolicyDetailsResponse>
```

#### 3. Get Policy by ID
```
GET /api/insurance/policies/{id}
Authorization: Bearer <token>

Response: PolicyDetailsResponse
```

#### 4. Pay Premium
```
POST /api/insurance/premium/pay
Authorization: Bearer <token>

Request Body:
{
  "policyId": 123,
  "amount": 80000,
  "paymentMethod": "ACCOUNT_DEBIT",
  "accountId": 456
}

Payment Methods: ACCOUNT_DEBIT | CARD | UPI | NETBANKING

Response: InsurancePremiumPayment
```

#### 5. Get Premium Payment History
```
GET /api/insurance/premium/history/{policyId}
Authorization: Bearer <token>

Response: List<InsurancePremiumPayment>
```

#### 6. File Insurance Claim
```
POST /api/insurance/claims/file
Authorization: Bearer <token>

Request Body:
{
  "policyId": 123,
  "claimAmount": 50000,
  "incidentDate": "2025-01-10",
  "description": "Medical treatment for illness",
  "hospitalName": "City Hospital",
  "doctorName": "Dr. Smith",
  "documentsSubmitted": "Medical bills, prescription, discharge summary"
}

Response: ClaimDetailsResponse
```

#### 7. Get All User Claims
```
GET /api/insurance/claims
Authorization: Bearer <token>

Response: List<ClaimDetailsResponse>
```

#### 8. Get Claims by Policy
```
GET /api/insurance/claims/policy/{policyId}
Authorization: Bearer <token>

Response: List<ClaimDetailsResponse>
```

#### 9. Cancel Policy
```
POST /api/insurance/policies/{id}/cancel
Authorization: Bearer <token>

Request Body:
{
  "reason": "No longer needed"
}

Response: PolicyDetailsResponse (status: CANCELLED)
```

---

### Admin Endpoints (5)

#### 1. Approve Policy Application
```
POST /api/insurance/admin/policies/{id}/approve
Authorization: Bearer <admin-token>

Response: PolicyDetailsResponse (status: ACTIVE)
```

#### 2. Reject Policy Application
```
POST /api/insurance/admin/policies/{id}/reject
Authorization: Bearer <admin-token>

Request Body:
{
  "rejectionReason": "Incomplete documentation"
}

Response: PolicyDetailsResponse (status: REJECTED)
```

#### 3. Approve Claim
```
POST /api/insurance/admin/claims/{id}/approve
Authorization: Bearer <admin-token>

Request Body:
{
  "approvedAmount": 45000
}

Response: ClaimDetailsResponse (status: APPROVED)
```

#### 4. Reject Claim
```
POST /api/insurance/admin/claims/{id}/reject
Authorization: Bearer <admin-token>

Request Body:
{
  "rejectionReason": "Pre-existing condition not covered"
}

Response: ClaimDetailsResponse (status: REJECTED)
```

#### 5. Pay Approved Claim
```
POST /api/insurance/admin/claims/{id}/pay
Authorization: Bearer <admin-token>

Request Body:
{
  "accountId": 789
}

Response: ClaimDetailsResponse (status: PAID, account credited)
```

---

## 💻 Frontend Components

### 1. InsurancePage.tsx (700+ lines)
**Features:**
- Summary dashboard (active policies, pending, total coverage)
- Active policies grid with policy cards
- Pending applications section
- Inactive policies (cancelled/expired/rejected)
- Apply for policy modal with comprehensive form
- Pay premium modal with multiple payment methods
- Premium payment history modal
- Cancel policy functionality with reason input
- Status badges and visual indicators
- Due date warnings for upcoming premiums

**Key State:**
```typescript
- policies: PolicyDetailsResponse[]
- loading: boolean
- showApplyModal, showPayModal, showHistoryModal
- newApplication: PolicyApplicationRequest
- premiumPayment: PremiumPaymentRequest
- premiumHistory: InsurancePremiumPayment[]
```

### 2. ClaimsPage.tsx (650+ lines)
**Features:**
- Summary cards (pending, approved, total claimed)
- Pending claims section with status tracking
- Approved/paid claims section
- Closed claims list (rejected/closed)
- File claim modal with comprehensive form
- Claim details modal with timeline visualization
- Status progression tracker
- Color-coded status badges and icons
- Empty state with helpful messaging

**Timeline Visualization:**
```
📝 Submitted → 🔍 Under Review → ✅ Approved → 💰 Payment Made
```

**Key State:**
```typescript
- claims: ClaimDetailsResponse[]
- policies: PolicyDetailsResponse[]
- loading: boolean
- showFileClaimModal, showClaimDetailsModal
- newClaim: ClaimRequest
- selectedClaim: ClaimDetailsResponse | null
```

### 3. Shared Styling (ClaimsPage.css & InsurancePage.css)
**Design Elements:**
- Gradient summary cards with hover effects
- Responsive grid layouts (350px minimum columns)
- Modal overlays with slide-up animations
- Status badges with color coding:
  - Green: Active/Approved/Paid
  - Yellow: Pending/Under Review
  - Red: Rejected/Cancelled
  - Cyan: Matured
- Form styling with focus states (#667eea)
- Mobile responsive (768px breakpoint)
- Timeline visualization with progress markers
- Empty state designs

---

## 🔐 Security & Validation

### Backend Validation
1. **Policy Application:**
   - Coverage amount > 0
   - Term years between 1-50
   - Valid insurance type and frequency
   - Authenticated customer required

2. **Premium Payment:**
   - Policy must be ACTIVE or PENDING_PAYMENT
   - Payment amount matches premium
   - Account balance sufficient (for ACCOUNT_DEBIT)
   - Policy belongs to authenticated user

3. **Claim Filing:**
   - Policy must be ACTIVE
   - Claim amount ≤ coverage amount
   - Incident date ≤ current date
   - No duplicate pending claims

4. **Admin Operations:**
   - ADMIN role required
   - Policy/claim ownership validated
   - State transitions validated

### Frontend Validation
1. **Required Fields:**
   - All form fields marked as required
   - Dropdown selections validated
   - Date constraints (max: today for incident dates)

2. **Amount Validation:**
   - Positive numbers only
   - Maximum limits respected
   - Currency formatting

3. **User Experience:**
   - Loading states during API calls
   - Success/error messages
   - Empty state handling
   - Disabled buttons during processing

---

## 🚀 Deployment Instructions

### 1. Database Setup
```sql
-- Tables auto-created by JPA on application start
-- Verify tables exist:
SHOW TABLES LIKE 'insurance%';

-- Expected tables:
-- insurance_policies
-- insurance_claims
-- insurance_premium_payments
```

### 2. Backend Deployment
```bash
cd /Users/akshatsanjayagrwal/Desktop/bankingsystem
./mvnw clean package -DskipTests
java -jar target/banking-system-0.0.1-SNAPSHOT.jar

# Or run directly:
./mvnw spring-boot:run
```

### 3. Frontend Deployment
```bash
cd frontend-redesign
npm install
npm run build
npm run preview  # Test production build
# Or deploy dist/ folder to static hosting
```

### 4. Verification Checklist
- ✅ Backend running on http://localhost:8080
- ✅ Frontend running on http://localhost:5173 (dev) or 4173 (preview)
- ✅ Database tables created (3 new tables)
- ✅ Login successful
- ✅ Navigate to /insurance and /claims routes
- ✅ Test policy application
- ✅ Test premium payment
- ✅ Test claim filing
- ✅ Test admin approvals (with admin account)

---

## 📈 Testing Scenarios

### Scenario 1: Complete Policy Lifecycle
1. User applies for Health Insurance (₹10,00,000, 5 years, Yearly)
2. Admin approves policy → Status: ACTIVE
3. User pays first premium (₹80,000) via ACCOUNT_DEBIT
4. System updates next_premium_due_date to 1 year later
5. User files claim (₹50,000 for medical treatment)
6. Admin reviews and approves claim (₹45,000)
7. Admin pays claim → Amount credited to user account

### Scenario 2: Policy Rejection
1. User applies for Term Insurance
2. Admin rejects with reason
3. User sees policy in "Inactive" section with REJECTED status
4. User cannot pay premium or file claims

### Scenario 3: Claim Processing
1. User has active Health policy
2. Files claim with hospital/doctor details
3. Admin requests additional documents → Status: PENDING_DOCUMENTS
4. User resubmits claim
5. Admin approves reduced amount
6. Payment processed to user account

### Scenario 4: Premium Payments
1. User has 3 active policies with different frequencies
2. Dashboard shows next due dates
3. User pays premium for monthly policy
4. System calculates next month's due date
5. Premium history updated

---

## 🐛 Known Issues & Resolutions

### Issue 1: Customer Repository Method
**Problem:** `findByUsername()` not defined in CustomerRepository  
**Solution:** Changed all 11 occurrences to `findByUser_Username()` to match existing interface  
**Files Affected:** InsuranceService.java

### Issue 2: Account Balance Type
**Problem:** Account.balance is primitive double, not BigDecimal  
**Solution:** Changed comparison logic from `.compareTo()` to `<` operator  
**Files Affected:** InsuranceService.java (2 occurrences)

### Issue 3: Customer Name Field
**Problem:** Customer has `name` field, not `fullName`  
**Solution:** Changed `.getFullName()` to `.getName()`  
**Files Affected:** InsuranceService.java

### Issue 4: Frontend Build Error (Pre-existing)
**Problem:** FixedDepositsPage imports non-existent `getAccountsByUser`  
**Status:** Pre-existing issue in Module 6, not related to Module 8  
**Impact:** Module 8 files compile without errors  
**Resolution:** Fix accountService.ts exports (separate task)

---

## 📚 Business Rules

1. **Policy Activation:**
   - Requires admin approval
   - First premium must be paid within 30 days
   - Policy becomes ACTIVE only after approval + payment

2. **Premium Calculation:**
   - Based on coverage amount and insurance type
   - Adjusted for payment frequency
   - Cannot be modified after policy creation

3. **Claim Limits:**
   - Total claims cannot exceed coverage amount
   - Multiple claims allowed per policy
   - Claim amount can be reduced during approval

4. **Policy Cancellation:**
   - User can cancel anytime
   - No refund processed (business rule)
   - Cannot file new claims after cancellation

5. **Premium Due Dates:**
   - Calculated based on frequency
   - Grace period: 30 days
   - Policy LAPSES after grace period

---

## 🔮 Future Enhancements

1. **Surrender Value Calculation:**
   - Calculate policy surrender value
   - Process surrender requests
   - Partial withdrawals

2. **Riders & Add-ons:**
   - Critical illness rider
   - Accidental death benefit
   - Waiver of premium

3. **Health Checkups:**
   - Integration with diagnostic centers
   - Free health checkup scheduling
   - Medical report uploads

4. **Cashless Claim Processing:**
   - Hospital network integration
   - Pre-authorization requests
   - Direct settlement with hospitals

5. **Premium Reminders:**
   - Email/SMS notifications
   - Auto-debit enrollment
   - Missed payment alerts

6. **Policy Comparison:**
   - Compare different insurance types
   - Premium calculator
   - Coverage recommendations

7. **Documents Management:**
   - Upload policy documents
   - Claim document tracking
   - Digital policy certificate

8. **Analytics Dashboard:**
   - Claims ratio
   - Premium collection trends
   - Policy type distribution

---

## 👥 Team & Credits

**Module Designer:** AI Assistant (GitHub Copilot)  
**Developer:** Akshat Sanjay Agrawal  
**Technology Stack:** Spring Boot 3.5.6, React 18.2.0, TypeScript 5.2.2, MySQL 8.x  
**Completion Date:** January 2025

---

## 📞 Support & Documentation

**Backend API Documentation:** http://localhost:8080/swagger-ui.html (if Swagger configured)  
**Frontend Dev Server:** http://localhost:5173  
**Database:** MySQL on localhost:3306  
**Repository Structure:**
```
bankingsystem/
├── src/main/java/com/bank/bankingsystem/
│   ├── model/ (InsurancePolicy, InsuranceClaim, InsurancePremiumPayment)
│   ├── repository/ (3 repositories)
│   ├── service/ (InsuranceService)
│   ├── controller/ (InsuranceController)
│   └── dto/ (6 DTOs)
└── frontend-redesign/src/
    ├── services/module8Service.ts
    ├── pages/InsurancePage.tsx
    ├── pages/ClaimsPage.tsx
    └── pages/*.css
```

---

## ✅ Completion Checklist

- [x] 4 Enums created (InsuranceType, PolicyStatus, ClaimStatus, PremiumFrequency)
- [x] 3 Entities created with JPA annotations
- [x] 6 DTOs created for request/response
- [x] 3 Repositories with 20+ custom queries
- [x] InsuranceService with 15+ methods and premium algorithm
- [x] InsuranceController with 14 REST endpoints
- [x] Backend compilation: BUILD SUCCESS (221 files)
- [x] Frontend service layer (module8Service.ts)
- [x] InsurancePage component (700+ lines)
- [x] ClaimsPage component (650+ lines)
- [x] CSS styling (1200+ lines combined)
- [x] Routing integration (main.tsx)
- [x] Navigation links (Layout.tsx)
- [x] Zero TypeScript errors in Module 8 files
- [x] Documentation complete

**Status: Module 8 Implementation COMPLETE** ✅

---

*End of Module 8 Documentation*
