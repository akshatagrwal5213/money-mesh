import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Transactions: React.FC = () => {
  const [transactions, setTransactions] = useState<any[]>([])
  const [accounts, setAccounts] = useState<any[]>([])
  const [selectedAccount, setSelectedAccount] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)
  const { logout } = useAuth()

  useEffect(() => {
    loadAccounts()
  }, [])

  useEffect(() => {
    if (selectedAccount) {
      loadTransactions()
    }
  }, [selectedAccount, page])

  const loadAccounts = async () => {
    try {
      const res = await api.get('/accounts')
      const accts = Array.isArray(res.data) ? res.data : (res.data.accounts || [])
      setAccounts(accts)
      if (accts.length > 0) {
        setSelectedAccount(accts[0].accountNumber)
      }
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    }
  }

  const loadTransactions = async () => {
    if (!selectedAccount) return
    
    try {
      setLoading(true)
      const res = await api.get(`/accounts/${selectedAccount}/transactions?page=${page}&size=20&sort=timestamp,desc`)
      setTransactions(res.data.content || [])
      setTotalPages(res.data.totalPages || 0)
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  const getTransactionColor = (amount: number) => {
    return amount > 0 ? '#28a745' : '#dc3545'
  }

  return (
    <div className="container">
      <h2>Transaction History</h2>

      <div style={{ marginBottom: 20 }}>
        <label style={{ display: 'block', marginBottom: 8, fontWeight: 600 }}>Select Account</label>
        <select 
          value={selectedAccount} 
          onChange={e => { setSelectedAccount(e.target.value); setPage(0); }}
          style={{ padding: 8, borderRadius: 4, border: '1px solid #ccc', minWidth: 300 }}
        >
          {accounts.map(acc => (
            <option key={acc.accountNumber} value={acc.accountNumber}>
              {acc.accountNumber} - {acc.accountType} (${acc.balance.toFixed(2)})
            </option>
          ))}
        </select>
      </div>

      {loading ? (
        <div>Loading transactions...</div>
      ) : transactions.length === 0 ? (
        <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
          No transactions found
        </div>
      ) : (
        <>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#f8f9fa', textAlign: 'left' }}>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Date & Time</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Type</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Description</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6', textAlign: 'right' }}>Amount</th>
                <th style={{ padding: 12, borderBottom: '2px solid #dee2e6', textAlign: 'right' }}>Balance</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((tx: any) => (
                <tr key={tx.id}>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>
                    {new Date(tx.timestamp).toLocaleString()}
                  </td>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>
                    <span style={{ 
                      padding: '4px 8px', 
                      background: '#e9ecef', 
                      borderRadius: 3, 
                      fontSize: 12 
                    }}>
                      {tx.type}
                    </span>
                  </td>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>
                    {tx.description || '-'}
                  </td>
                  <td style={{ 
                    padding: 12, 
                    borderBottom: '1px solid #dee2e6', 
                    textAlign: 'right',
                    color: getTransactionColor(tx.amount),
                    fontWeight: 600
                  }}>
                    {tx.amount > 0 ? '+' : ''}${tx.amount.toFixed(2)}
                  </td>
                  <td style={{ padding: 12, borderBottom: '1px solid #dee2e6', textAlign: 'right' }}>
                    ${tx.balanceAfter.toFixed(2)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {totalPages > 1 && (
            <div style={{ display: 'flex', justifyContent: 'center', gap: 8, marginTop: 20 }}>
              <button
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                style={{ 
                  padding: '8px 16px', 
                  background: page === 0 ? '#e9ecef' : '#007bff', 
                  color: page === 0 ? '#6c757d' : 'white',
                  border: 'none', 
                  borderRadius: 4, 
                  cursor: page === 0 ? 'not-allowed' : 'pointer' 
                }}
              >
                Previous
              </button>
              <span style={{ padding: '8px 16px', background: '#f8f9fa', borderRadius: 4 }}>
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                style={{ 
                  padding: '8px 16px', 
                  background: page >= totalPages - 1 ? '#e9ecef' : '#007bff', 
                  color: page >= totalPages - 1 ? '#6c757d' : 'white',
                  border: 'none', 
                  borderRadius: 4, 
                  cursor: page >= totalPages - 1 ? 'not-allowed' : 'pointer' 
                }}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default Transactions
