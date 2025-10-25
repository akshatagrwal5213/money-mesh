import React from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import Dashboard from './pages/Dashboard'
import AdminDashboard from './pages/AdminDashboard'
import Accounts from './pages/Accounts'
import Transactions from './pages/Transactions'
import Cards from './pages/Cards'
import Loans from './pages/Loans'
import Transfers from './pages/Transfers'
import Notifications from './pages/Notifications'
import CustomersPage from './pages/CustomersPage'
import AdminLoanApprovals from './pages/AdminLoanApprovals'
import AdminCreateCustomerPage from './pages/AdminCreateCustomerPage'
import Layout from './components/Layout'
import SmartRedirect from './components/SmartRedirect'
import DashboardRouter from './components/DashboardRouter'
import { AuthProvider } from './contexts/AuthContext'
import { ThemeProvider } from './contexts/ThemeContext'
import { CustomMuiThemeProvider } from './contexts/MuiThemeProvider'
import BillPaymentsPage from './pages/BillPaymentsPage'
import PayBillPage from './pages/PayBillPage'
import UpiPaymentsPage from './pages/UpiPaymentsPage'
import QrCodePage from './pages/QrCodePage'
import PortfolioPage from './pages/PortfolioPage'
import FixedDepositsPage from './pages/FixedDepositsPage'
import MutualFundsPage from './pages/MutualFundsPage'
import RecurringDepositsPage from './pages/RecurringDepositsPage'
import SipPage from './pages/SipPage'
import AnalyticsDashboard from './pages/AnalyticsDashboard'
import BudgetsPage from './pages/BudgetsPage'
import GoalsPage from './pages/GoalsPage'
import SettingsPage from './pages/SettingsPage'
import InsurancePage from './pages/InsurancePage'
import ClaimsPage from './pages/ClaimsPage'
import TaxDashboardPage from './pages/TaxDashboardPage'
import TaxCalculatorPage from './pages/TaxCalculatorPage'
import WealthDashboardPage from './pages/WealthDashboardPage'
import RetirementPlannerPage from './pages/RetirementPlannerPage'
import LoanDashboard from './pages/LoanDashboard'
import RewardsDashboard from './pages/RewardsDashboard'
import RedemptionStore from './pages/RedemptionStore'
import EMICalendar from './components/EMICalendar'
import CreditDashboard from './pages/CreditDashboard'
import LoanEligibilityPage from './pages/LoanEligibilityPage'
import RegistrationPage from './pages/RegistrationPage'
import AddAccountPage from './pages/AddAccountPage'
import ProtectedRoute from './components/ProtectedRoute'

import './styles.css'

createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <ThemeProvider>
        <CustomMuiThemeProvider>
          <AuthProvider>
            <Layout>
              <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegistrationPage />} />
            
            {/* Unified Dashboard Route - works for both admin and customer */}
            <Route path="/dashboard" element={<ProtectedRoute><DashboardRouter /></ProtectedRoute>} />
            
            {/* Customer Routes */}
            <Route path="/add-account" element={<ProtectedRoute requireCustomer><AddAccountPage /></ProtectedRoute>} />
            <Route path="/accounts" element={<ProtectedRoute requireCustomer><Accounts /></ProtectedRoute>} />
            <Route path="/transactions" element={<ProtectedRoute requireCustomer><Transactions /></ProtectedRoute>} />
            <Route path="/transfers" element={<ProtectedRoute requireCustomer><Transfers /></ProtectedRoute>} />
            <Route path="/cards" element={<ProtectedRoute requireCustomer><Cards /></ProtectedRoute>} />
            <Route path="/loans" element={<ProtectedRoute requireCustomer><Loans /></ProtectedRoute>} />
            <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />
            <Route path="/bill-payments" element={<ProtectedRoute requireCustomer><BillPaymentsPage /></ProtectedRoute>} />
            <Route path="/pay-bill" element={<ProtectedRoute requireCustomer><PayBillPage /></ProtectedRoute>} />
            <Route path="/upi-payments" element={<ProtectedRoute requireCustomer><UpiPaymentsPage /></ProtectedRoute>} />
            <Route path="/qr-codes" element={<ProtectedRoute requireCustomer><QrCodePage /></ProtectedRoute>} />
            <Route path="/portfolio" element={<ProtectedRoute requireCustomer><PortfolioPage /></ProtectedRoute>} />
            <Route path="/fixed-deposits" element={<ProtectedRoute requireCustomer><FixedDepositsPage /></ProtectedRoute>} />
            <Route path="/recurring-deposits" element={<ProtectedRoute requireCustomer><RecurringDepositsPage /></ProtectedRoute>} />
            <Route path="/mutual-funds" element={<ProtectedRoute requireCustomer><MutualFundsPage /></ProtectedRoute>} />
            <Route path="/sip" element={<ProtectedRoute requireCustomer><SipPage /></ProtectedRoute>} />
            <Route path="/analytics" element={<ProtectedRoute requireCustomer><AnalyticsDashboard /></ProtectedRoute>} />
            <Route path="/budgets" element={<ProtectedRoute requireCustomer><BudgetsPage /></ProtectedRoute>} />
            <Route path="/goals" element={<ProtectedRoute requireCustomer><GoalsPage /></ProtectedRoute>} />
            <Route path="/insurance" element={<ProtectedRoute requireCustomer><InsurancePage /></ProtectedRoute>} />
            <Route path="/claims" element={<ProtectedRoute requireCustomer><ClaimsPage /></ProtectedRoute>} />
            <Route path="/tax-dashboard" element={<ProtectedRoute requireCustomer><TaxDashboardPage /></ProtectedRoute>} />
            <Route path="/tax-calculator" element={<ProtectedRoute requireCustomer><TaxCalculatorPage /></ProtectedRoute>} />
            <Route path="/wealth-dashboard" element={<ProtectedRoute requireCustomer><WealthDashboardPage /></ProtectedRoute>} />
            <Route path="/retirement-planner" element={<ProtectedRoute requireCustomer><RetirementPlannerPage /></ProtectedRoute>} />
            <Route path="/loan-dashboard" element={<ProtectedRoute requireCustomer><LoanDashboard /></ProtectedRoute>} />
            <Route path="/rewards-dashboard" element={<ProtectedRoute requireCustomer><RewardsDashboard /></ProtectedRoute>} />
            <Route path="/redemption-store" element={<ProtectedRoute requireCustomer><RedemptionStore /></ProtectedRoute>} />
            <Route path="/emi-calendar/:loanId" element={<ProtectedRoute requireCustomer><EMICalendar loanId={1} /></ProtectedRoute>} />
            <Route path="/credit-dashboard" element={<ProtectedRoute requireCustomer><CreditDashboard /></ProtectedRoute>} />
            <Route path="/loan-eligibility" element={<ProtectedRoute requireCustomer><LoanEligibilityPage /></ProtectedRoute>} />
            <Route path="/settings" element={<ProtectedRoute><SettingsPage /></ProtectedRoute>} />
            
            {/* Admin Routes */}
            <Route path="/admin/customers" element={<ProtectedRoute requireAdmin><CustomersPage /></ProtectedRoute>} />
            <Route path="/admin/create-customer" element={<ProtectedRoute requireAdmin><AdminCreateCustomerPage /></ProtectedRoute>} />
            <Route path="/admin/loan-approvals" element={<ProtectedRoute requireAdmin><AdminLoanApprovals /></ProtectedRoute>} />
            
            {/* Default Route - Smart redirect based on user role */}
            <Route path="/" element={<SmartRedirect />} />
          </Routes>
        </Layout>
      </AuthProvider>
    </CustomMuiThemeProvider>
    </ThemeProvider>
    </BrowserRouter>
  </React.StrictMode>
)
