import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Transfers: React.FC = () => {
  const [fromAccountId, setFromAccountId] = useState('')
  const [toAccountNumber, setToAccountNumber] = useState('')
  const [amount, setAmount] = useState('')
  const [description, setDescription] = useState('')
  const [requiresOtp, setRequiresOtp] = useState(false)
  const [otp, setOtp] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [accounts, setAccounts] = useState<any[]>([])
  const { logout } = useAuth()

  useEffect(() => {
    loadAccounts()
  }, [])

  const loadAccounts = async () => {
    try {
      const res = await api.get('/accounts')
      setAccounts(Array.isArray(res.data) ? res.data : (res.data.accounts || []))
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    }
  }

  const checkOtpRequirement = async (amt: number) => {
    try {
      const res = await api.post('/transfers/check-otp-requirement', { amount: amt })
      setRequiresOtp(res.data.requiresOtp)
    } catch (err) {
      console.error('Error checking OTP requirement:', err)
    }
  }

  const handleAmountChange = (value: string) => {
    setAmount(value)
    const numValue = parseFloat(value)
    if (!isNaN(numValue)) {
      checkOtpRequirement(numValue)
    }
  }

  const requestOtp = async () => {
    try {
      const res = await api.post('/transfers/request-otp')
      setSuccess('OTP sent! Check console for OTP code.')
      setError('')
    } catch (err: any) {
      setError('Failed to send OTP')
    }
  }

  const handleTransfer = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setSuccess('')

    try {
      const payload: any = {
        fromAccountId: parseInt(fromAccountId),
        toAccountNumber,
        amount: parseFloat(amount),
        description
      }
      
      if (requiresOtp && otp) {
        payload.otp = otp
      }

      const res = await api.post('/transfers', payload)
      setSuccess(res.data.message || 'Transfer completed successfully!')
      
      // Reset form
      setFromAccountId('')
      setToAccountNumber('')
      setAmount('')
      setDescription('')
      setOtp('')
      setRequiresOtp(false)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Transfer failed')
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  // If no accounts, show message
  if (accounts.length === 0) {
    return (
      <div className="container">
        <h2>Transfer Money</h2>
        <div style={{ 
          padding: '40px 20px', 
          textAlign: 'center',
          background: '#f5f5f5',
          borderRadius: 8,
          border: '1px solid #ddd'
        }}>
          <svg 
            style={{ width: 64, height: 64, marginBottom: 16, color: '#999' }}
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24"
          >
            <path 
              strokeLinecap="round" 
              strokeLinejoin="round" 
              strokeWidth={2} 
              d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" 
            />
          </svg>
          <h3 style={{ color: '#666', marginBottom: 8 }}>No Accounts Available</h3>
          <p style={{ color: '#999', marginBottom: 16 }}>
            You need to create an account first before you can make transfers.
          </p>
          <a 
            href="/accounts" 
            style={{ 
              display: 'inline-block',
              padding: '10px 20px', 
              background: '#1976d2', 
              color: 'white', 
              textDecoration: 'none',
              borderRadius: 4,
              fontWeight: 600
            }}
          >
            Go to Accounts
          </a>
        </div>
      </div>
    )
  }

  return (
    <div className="container">
      <h2>Transfer Money</h2>
      
      {error && <div className="error" style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div className="success" style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      <form onSubmit={handleTransfer} style={{ maxWidth: 500 }}>
        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>From Account</label>
          <select 
            value={fromAccountId} 
            onChange={e => setFromAccountId(e.target.value)}
            required
            style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
          >
            <option value="">Select Account</option>
            {accounts.map(acc => (
              <option key={acc.id} value={acc.id}>
                {acc.accountNumber} - {acc.accountType} (${acc.balance})
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>To Account Number</label>
          <input 
            type="text" 
            value={toAccountNumber}
            onChange={e => setToAccountNumber(e.target.value)}
            placeholder="Enter recipient account number"
            required
            style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
          />
        </div>

        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Amount</label>
          <input 
            type="number" 
            step="0.01"
            value={amount}
            onChange={e => handleAmountChange(e.target.value)}
            placeholder="0.00"
            required
            style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
          />
          {requiresOtp && (
            <small style={{ color: '#f80' }}>⚠️ Transfers over $10,000 require OTP verification</small>
          )}
        </div>

        <div style={{ marginBottom: 16 }}>
          <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Description (Optional)</label>
          <input 
            type="text" 
            value={description}
            onChange={e => setDescription(e.target.value)}
            placeholder="Payment description"
            style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
          />
        </div>

        {requiresOtp && (
          <div style={{ marginBottom: 16, padding: 12, background: '#fef8e0', borderRadius: 4 }}>
            <div style={{ marginBottom: 8 }}>
              <button 
                type="button" 
                onClick={requestOtp}
                style={{ padding: '6px 12px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
              >
                Request OTP
              </button>
            </div>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Enter OTP</label>
            <input 
              type="text" 
              value={otp}
              onChange={e => setOtp(e.target.value)}
              placeholder="6-digit OTP"
              required={requiresOtp}
              maxLength={6}
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
        )}

        <button 
          type="submit" 
          disabled={loading || (requiresOtp && !otp)}
          style={{ 
            padding: '10px 24px', 
            background: loading ? '#999' : '#28a745', 
            color: 'white', 
            border: 'none', 
            borderRadius: 4, 
            cursor: loading ? 'not-allowed' : 'pointer',
            fontWeight: 600
          }}
        >
          {loading ? 'Processing...' : 'Transfer Money'}
        </button>
      </form>
    </div>
  )
}

export default Transfers
