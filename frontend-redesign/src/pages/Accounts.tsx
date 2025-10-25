import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Accounts: React.FC = () => {
  const [accounts, setAccounts] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [accountType, setAccountType] = useState('SAVINGS')
  const [initialDeposit, setInitialDeposit] = useState('')
  const [nickname, setNickname] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { logout, checkUserAccounts } = useAuth()

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      setLoading(true)
      const res = await api.get('/accounts')
      console.log('[Accounts] API response:', res.data);
      // API returns array directly, not nested under 'accounts'
      const accountsArray = Array.isArray(res.data) ? res.data : (res.data.accounts || []);
      setAccounts(accountsArray)
      // Update auth context with account status
      checkUserAccounts()
    } catch (err: any) {
      console.error('[Accounts] Error loading accounts:', err);
      if (err.response?.status === 401) logout()
      else setError('Failed to load accounts')
    } finally {
      setLoading(false)
    }
  }

  const createAccount = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await api.post('/accounts', {
        accountType,
        initialDeposit: parseFloat(initialDeposit),
        nickname: nickname || undefined
      })
      setSuccess('Account created successfully!')
      setShowCreateForm(false)
      setAccountType('SAVINGS')
      setInitialDeposit('')
      setNickname('')
      loadAccounts()
      // Update navigation after creating first account
      checkUserAccounts()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create account')
    }
  }

  const updateNickname = async (accountId: number, newNickname: string) => {
    try {
      await api.put(`/accounts/${accountId}/nickname`, { nickname: newNickname })
      setSuccess('Nickname updated!')
      loadAccounts()
    } catch (err: any) {
      setError('Failed to update nickname')
    }
  }

  const closeAccount = async (accountId: number) => {
    if (!confirm('Are you sure you want to close this account?')) return

    try {
      await api.delete(`/accounts/${accountId}`)
      setSuccess('Account closed successfully')
      loadAccounts()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to close account')
    }
  }

  const getAccountTypeColor = (type: string) => {
    switch (type) {
      case 'SAVINGS': return '#28a745'
      case 'CHECKING': return '#007bff'
      case 'CREDIT': return '#dc3545'
      default: return '#6c757d'
    }
  }

  if (loading) return <div className="center">Loading accounts...</div>

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2>My Accounts</h2>
        <button 
          onClick={() => setShowCreateForm(!showCreateForm)}
          style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}
        >
          {showCreateForm ? 'Cancel' : '+ Create Account'}
        </button>
      </div>

      {error && <div style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      {showCreateForm && (
        <form onSubmit={createAccount} style={{ padding: 20, background: '#f8f9fa', borderRadius: 8, marginBottom: 24 }}>
          <h3>Create New Account</h3>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Account Type</label>
            <select 
              value={accountType} 
              onChange={e => setAccountType(e.target.value)}
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            >
              <option value="SAVINGS">Savings</option>
              <option value="CHECKING">Checking</option>
            </select>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Initial Deposit</label>
            <input 
              type="number" 
              step="0.01"
              value={initialDeposit}
              onChange={e => setInitialDeposit(e.target.value)}
              required
              min="0"
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Nickname (Optional)</label>
            <input 
              type="text" 
              value={nickname}
              onChange={e => setNickname(e.target.value)}
              placeholder="e.g., Emergency Fund"
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <button type="submit" style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}>
            Create Account
          </button>
        </form>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
        {accounts.map(account => (
          <div key={account.id} style={{ 
            padding: 20, 
            border: '1px solid #dee2e6', 
            borderRadius: 8,
            borderLeft: `4px solid ${getAccountTypeColor(account.accountType)}`
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: 12 }}>
              <div>
                <div style={{ fontSize: 12, color: '#6c757d', textTransform: 'uppercase', marginBottom: 4 }}>
                  {account.accountType}
                </div>
                <div style={{ fontSize: 14, fontFamily: 'monospace', color: '#495057' }}>
                  {account.accountNumber}
                </div>
                {account.nickname && (
                  <div style={{ fontSize: 16, fontWeight: 600, marginTop: 4 }}>
                    {account.nickname}
                  </div>
                )}
              </div>
            </div>
            
            <div style={{ fontSize: 28, fontWeight: 700, marginBottom: 16, color: '#212529' }}>
              ${account.balance.toFixed(2)}
            </div>

            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <button
                onClick={() => {
                  const newNickname = prompt('Enter new nickname:', account.nickname || '')
                  if (newNickname !== null) updateNickname(account.id, newNickname)
                }}
                style={{ padding: '6px 12px', fontSize: 12, background: '#007bff', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
              >
                Edit Nickname
              </button>
              <button
                onClick={() => closeAccount(account.id)}
                style={{ padding: '6px 12px', fontSize: 12, background: '#dc3545', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
              >
                Close Account
              </button>
            </div>
          </div>
        ))}
      </div>

      {accounts.length === 0 && !showCreateForm && (
        <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
          No accounts yet. Create your first account!
        </div>
      )}
    </div>
  )
}

export default Accounts
