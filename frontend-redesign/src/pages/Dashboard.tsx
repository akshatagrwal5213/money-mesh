import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Dashboard: React.FC = () => {
  const [accounts, setAccounts] = useState<any[]>([])
  const [transactions, setTransactions] = useState<any[]>([])
  const [unreadCount, setUnreadCount] = useState(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { user, logout, checkUserAccounts } = useAuth()

  useEffect(() => {
    loadDashboardData()
  }, [])

  const loadDashboardData = async () => {
    try {
      console.log('[Dashboard] Loading dashboard data...');
      console.log('[Dashboard] Token:', localStorage.getItem('token'));
      const [accountsRes, notifRes] = await Promise.all([
        api.get('/accounts'),
        api.get('/notifications/unread/count').catch(() => ({ data: { count: 0 } }))
      ])
      
      console.log('[Dashboard] Accounts response:', accountsRes);
      console.log('[Dashboard] Accounts data:', accountsRes.data);
      console.log('[Dashboard] Is array?', Array.isArray(accountsRes.data));
      // API returns array directly, not nested under 'accounts'
      const accountsArray = Array.isArray(accountsRes.data) ? accountsRes.data : (accountsRes.data.accounts || []);
      setAccounts(accountsArray);
      setUnreadCount(notifRes.data.count || 0)

      // Update auth context with account status
      checkUserAccounts()

      // Get recent transactions for first account
      if (accountsArray.length > 0) {
        const firstAccount = accountsArray[0]
        console.log('[Dashboard] Loading transactions for account:', firstAccount.accountNumber);
        const txRes = await api.get(`/accounts/${firstAccount.accountNumber}/transactions?size=5`)
        console.log('[Dashboard] Transactions response:', txRes.data);
        setTransactions(txRes.data.content || txRes.data || [])
      }
    } catch (err: any) {
      console.error('[Dashboard] Error loading dashboard:', err);
      setError(err?.message || 'Failed to load dashboard.');
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  const totalBalance = accounts.reduce((sum, acc) => sum + (acc.balance || 0), 0)

  if (loading) return <div className="center">Loading dashboard...</div>
  if (error) return (
    <div className="center" style={{ color: 'red', padding: 32 }}>
      <h2>Dashboard Error</h2>
      <p>{error}</p>
      <p>Please check your browser console for details and contact support if this persists.</p>
    </div>
  )

  return (
    <div className="container">
      <h2>Welcome, {user?.username}!</h2>
      
      {/* Debug info - remove after testing */}
      <div style={{ 
        padding: '8px 12px', 
        background: '#f0f0f0', 
        borderRadius: '4px', 
        marginBottom: '12px',
        fontSize: '12px',
        fontFamily: 'monospace'
      }}>
        🔍 Debug: Accounts loaded: {accounts.length} | Total Balance: ₹{totalBalance.toLocaleString()}
      </div>
      
      {/* No Accounts State */}
      {accounts.length === 0 ? (
        <div style={{ 
          padding: 40, 
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', 
          color: 'white', 
          borderRadius: 12, 
          textAlign: 'center',
          marginBottom: 24
        }}>
          <div style={{ fontSize: 48, marginBottom: 16 }}>🏦</div>
          <h3 style={{ marginBottom: 12, fontSize: 24 }}>Create Your First Account!</h3>
          <p style={{ marginBottom: 24, opacity: 0.95, fontSize: 16 }}>
            You haven't created a bank account yet. Start your banking journey by creating your first account now.
          </p>
          <div style={{ display: 'flex', gap: 12, justifyContent: 'center', flexWrap: 'wrap' }}>
            <a 
              href="/add-account" 
              style={{ 
                padding: '12px 32px', 
                background: 'white', 
                color: '#667eea', 
                borderRadius: 6, 
                textDecoration: 'none',
                fontWeight: 'bold',
                fontSize: 16
              }}
            >
              Create Account Now
            </a>
          </div>
          <div style={{ marginTop: 32, padding: 24, background: 'rgba(255,255,255,0.1)', borderRadius: 8 }}>
            <p style={{ fontSize: 14, opacity: 0.9, marginBottom: 8 }}>
              ℹ️ Note: Many features like investments, payments, loans, and calculators will become available after you create an account.
            </p>
          </div>
        </div>
      ) : (
        <>
          {/* Summary Cards */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16, marginBottom: 24 }}>
            <div style={{ padding: 20, background: '#007bff', color: 'white', borderRadius: 8 }}>
              <div style={{ fontSize: 14, opacity: 0.9 }}>Total Balance</div>
              <div style={{ fontSize: 28, fontWeight: 700, marginTop: 8 }}>${totalBalance.toFixed(2)}</div>
            </div>
            <div style={{ padding: 20, background: '#28a745', color: 'white', borderRadius: 8 }}>
              <div style={{ fontSize: 14, opacity: 0.9 }}>Accounts</div>
              <div style={{ fontSize: 28, fontWeight: 700, marginTop: 8 }}>{accounts.length}</div>
            </div>
            <div style={{ padding: 20, background: '#ffc107', color: 'white', borderRadius: 8 }}>
              <div style={{ fontSize: 14, opacity: 0.9 }}>Notifications</div>
              <div style={{ fontSize: 28, fontWeight: 700, marginTop: 8 }}>{unreadCount}</div>
            </div>
          </div>
        </>
      )}
      
      {/* Quick Actions - Show appropriate options based on account status */}
      <div style={{ marginBottom: 24 }}>
        <h3>Quick Actions</h3>
        <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
          {accounts.length === 0 ? (
            <>
              <a href="/add-account" style={{ padding: '12px 24px', background: '#007bff', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 600, fontSize: '15px' }}>Create Your First Account</a>
              <a href="/settings" style={{ padding: '12px 24px', background: '#6c757d', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 600, fontSize: '15px' }}>Profile Settings</a>
            </>
          ) : (
            <>
              <a href="/transfers" style={{ padding: '10px 20px', background: '#007bff', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 500 }}>Transfer Money</a>
              <a href="/accounts" style={{ padding: '10px 20px', background: '#28a745', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 500 }}>View Accounts</a>
              <a href="/cards" style={{ padding: '10px 20px', background: '#6f42c1', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 500 }}>Manage Cards</a>
              <a href="/loans" style={{ padding: '10px 20px', background: '#fd7e14', color: 'white', borderRadius: 4, textDecoration: 'none', fontWeight: 500 }}>Apply for Loan</a>
            </>
          )}
        </div>
      </div>

      {/* Recent Transactions */}
      {transactions.length > 0 && (
        <div>
          <h3>Recent Transactions</h3>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#f8f9fa', textAlign: 'left' }}>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Date</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Type</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Amount</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Balance</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((tx: any) => (
                <tr key={tx.id}>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>
                    {new Date(tx.timestamp).toLocaleDateString()}
                  </td>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{tx.type}</td>
                  <td style={{ 
                    padding: 12, 
                    borderBottom: '1px solid #dee2e6',
                    color: tx.amount > 0 ? '#28a745' : '#dc3545',
                    fontWeight: 600
                  }}>
                    {tx.amount > 0 ? '+' : ''}${tx.amount.toFixed(2)}
                  </td>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>${tx.balanceAfter.toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

export default Dashboard
