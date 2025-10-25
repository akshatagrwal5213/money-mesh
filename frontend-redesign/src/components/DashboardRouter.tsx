import React from 'react'
import { useAuth } from '../contexts/AuthContext'
import Dashboard from '../pages/Dashboard'
import AdminDashboard from '../pages/AdminDashboard'

/**
 * DashboardRouter: Routes to appropriate dashboard based on user role
 * - Admins → AdminDashboard
 * - Customers → Customer Dashboard
 */
const DashboardRouter: React.FC = () => {
  const { isAdmin } = useAuth()

  if (isAdmin) {
    return <AdminDashboard />
  } else {
    return <Dashboard />
  }
}

export default DashboardRouter
