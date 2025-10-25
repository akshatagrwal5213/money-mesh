import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Cards: React.FC = () => {
  const [cards, setCards] = useState<any[]>([])
  const [accounts, setAccounts] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [showIssueForm, setShowIssueForm] = useState(false)
  const [selectedCard, setSelectedCard] = useState<any>(null)
  const [showTransactions, setShowTransactions] = useState(false)
  const [transactions, setTransactions] = useState<any[]>([])
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { logout } = useAuth()

  // Form states
  const [accountId, setAccountId] = useState('')
  const [cardType, setCardType] = useState('DEBIT')
  const [cardLimit, setCardLimit] = useState('')

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      setLoading(true)
      const [cardsRes, accountsRes] = await Promise.all([
        api.get('/cards'),
        api.get('/accounts')
      ])
      setCards(cardsRes.data || [])
      setAccounts(Array.isArray(accountsRes.data) ? accountsRes.data : (accountsRes.data.accounts || []))
    } catch (err: any) {
      if (err.response?.status === 401) logout()
      else setError('Failed to load data')
    } finally {
      setLoading(false)
    }
  }

  const issueCard = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await api.post('/cards/issue', {
        accountId: parseInt(accountId),
        type: cardType,
        cardLimit: cardLimit ? parseFloat(cardLimit) : null
      })
      setSuccess('Card issued successfully!')
      setShowIssueForm(false)
      setAccountId('')
      setCardType('DEBIT')
      setCardLimit('')
      loadData()
    } catch (err: any) {
      setError(err.response?.data || 'Failed to issue card')
    }
  }

  const blockCard = async (cardId: number) => {
    try {
      await api.post(`/cards/${cardId}/block`, {}, { params: { reason: 'User requested' } })
      setSuccess('Card blocked successfully')
      loadData()
    } catch (err: any) {
      setError('Failed to block card')
    }
  }

  const unblockCard = async (cardId: number) => {
    try {
      await api.post(`/cards/${cardId}/unblock`)
      setSuccess('Card unblocked successfully')
      loadData()
    } catch (err: any) {
      setError('Failed to unblock card')
    }
  }

  const changePin = async (cardId: number) => {
    const oldPin = prompt('Enter old PIN:')
    const newPin = prompt('Enter new PIN (4 digits):')
    
    if (!oldPin || !newPin) return
    
    try {
      await api.post(`/cards/${cardId}/change-pin`, { oldPin, newPin })
      setSuccess('PIN changed successfully')
    } catch (err: any) {
      setError(err.response?.data || 'Failed to change PIN')
    }
  }

  const setLimit = async (cardId: number) => {
    const dailyLimit = prompt('Enter daily transaction limit:')
    const monthlyLimit = prompt('Enter monthly transaction limit:')
    
    if (!dailyLimit && !monthlyLimit) return
    
    try {
      await api.put(`/cards/${cardId}/limits`, {
        dailyLimit: dailyLimit ? parseFloat(dailyLimit) : null,
        monthlyLimit: monthlyLimit ? parseFloat(monthlyLimit) : null
      })
      setSuccess('Limits updated successfully')
      loadData()
    } catch (err: any) {
      setError('Failed to update limits')
    }
  }

  const viewTransactions = async (cardId: number) => {
    try {
      const res = await api.get(`/cards/${cardId}/transactions`)
      setTransactions(res.data || [])
      setShowTransactions(true)
    } catch (err: any) {
      setError('Failed to load transactions')
    }
  }

  const getCardTypeColor = (type: string) => {
    switch (type) {
      case 'DEBIT': return '#007bff'
      case 'CREDIT': return '#dc3545'
      default: return '#6c757d'
    }
  }

  if (loading) return <div className="center">Loading cards...</div>

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2>My Cards</h2>
        <button 
          onClick={() => setShowIssueForm(!showIssueForm)}
          style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}
        >
          {showIssueForm ? 'Cancel' : '+ Issue New Card'}
        </button>
      </div>

      {error && <div style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      {showIssueForm && (
        <form onSubmit={issueCard} style={{ padding: 20, background: '#f8f9fa', borderRadius: 8, marginBottom: 24 }}>
          <h3>Issue New Card</h3>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Account</label>
            <select 
              value={accountId} 
              onChange={e => setAccountId(e.target.value)}
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
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Card Type</label>
            <select 
              value={cardType} 
              onChange={e => setCardType(e.target.value)}
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            >
              <option value="DEBIT">Debit Card</option>
              <option value="CREDIT">Credit Card</option>
            </select>
          </div>
          {cardType === 'CREDIT' && (
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Credit Limit</label>
              <input 
                type="number" 
                step="0.01"
                value={cardLimit}
                onChange={e => setCardLimit(e.target.value)}
                placeholder="e.g., 5000"
                style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
              />
            </div>
          )}
          <button type="submit" style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}>
            Issue Card
          </button>
        </form>
      )}

      {showTransactions && (
        <div style={{ padding: 20, background: '#f8f9fa', borderRadius: 8, marginBottom: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
            <h3>Card Transactions</h3>
            <button onClick={() => setShowTransactions(false)} style={{ padding: '6px 12px', background: '#6c757d', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}>
              Close
            </button>
          </div>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#fff', textAlign: 'left' }}>
                <th style={{ padding: 8, borderBottom: '2px solid #dee2e6' }}>Date</th>
                <th style={{ padding: 8, borderBottom: '2px solid #dee2e6' }}>Merchant</th>
                <th style={{ padding: 8, borderBottom: '2px solid #dee2e6' }}>Amount</th>
                <th style={{ padding: 8, borderBottom: '2px solid #dee2e6' }}>Status</th>
              </tr>
            </thead>
            <tbody>
              {transactions.map((tx: any) => (
                <tr key={tx.id}>
                  <td style={{ padding: 8, borderBottom: '1px solid #dee2e6' }}>
                    {new Date(tx.timestamp).toLocaleDateString()}
                  </td>
                  <td style={{ padding: 8, borderBottom: '1px solid #dee2e6' }}>{tx.merchant}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #dee2e6' }}>${tx.amount.toFixed(2)}</td>
                  <td style={{ padding: 8, borderBottom: '1px solid #dee2e6' }}>{tx.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 16 }}>
        {cards.map(card => (
          <div key={card.id} style={{ 
            padding: 24, 
            background: `linear-gradient(135deg, ${getCardTypeColor(card.type)} 0%, ${getCardTypeColor(card.type)}dd 100%)`,
            color: 'white',
            borderRadius: 12,
            minHeight: 180,
            display: 'flex',
            flexDirection: 'column',
            justifyContent: 'space-between'
          }}>
            <div>
              <div style={{ fontSize: 12, opacity: 0.9, marginBottom: 4 }}>{card.type} CARD</div>
              <div style={{ fontSize: 20, fontFamily: 'monospace', letterSpacing: 2, marginTop: 12 }}>
                {card.maskedCardNumber || '**** **** **** ' + (card.cardNumber || '').slice(-4)}
              </div>
            </div>
            
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                <div>
                  <div style={{ fontSize: 10, opacity: 0.8 }}>EXPIRES</div>
                  <div style={{ fontSize: 14 }}>{card.expiryDate}</div>
                </div>
                {card.cardLimit && (
                  <div>
                    <div style={{ fontSize: 10, opacity: 0.8 }}>LIMIT</div>
                    <div style={{ fontSize: 14 }}>${card.cardLimit}</div>
                  </div>
                )}
              </div>

              <div style={{ 
                fontSize: 11, 
                padding: '4px 8px', 
                background: card.status === 'ACTIVE' ? 'rgba(40,167,69,0.3)' : 'rgba(220,53,69,0.3)', 
                borderRadius: 3,
                display: 'inline-block',
                marginBottom: 12
              }}>
                {card.status}
              </div>

              <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 12 }}>
                {card.status === 'ACTIVE' ? (
                  <button
                    onClick={() => blockCard(card.id)}
                    style={{ padding: '4px 10px', fontSize: 11, background: 'rgba(220,53,69,0.9)', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                  >
                    Block
                  </button>
                ) : (
                  <button
                    onClick={() => unblockCard(card.id)}
                    style={{ padding: '4px 10px', fontSize: 11, background: 'rgba(40,167,69,0.9)', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                  >
                    Unblock
                  </button>
                )}
                <button
                  onClick={() => changePin(card.id)}
                  style={{ padding: '4px 10px', fontSize: 11, background: 'rgba(255,255,255,0.2)', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                >
                  Change PIN
                </button>
                <button
                  onClick={() => setLimit(card.id)}
                  style={{ padding: '4px 10px', fontSize: 11, background: 'rgba(255,255,255,0.2)', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                >
                  Set Limits
                </button>
                <button
                  onClick={() => viewTransactions(card.id)}
                  style={{ padding: '4px 10px', fontSize: 11, background: 'rgba(255,255,255,0.2)', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                >
                  Transactions
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {cards.length === 0 && !showIssueForm && (
        <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
          No cards yet. Issue your first card!
        </div>
      )}
    </div>
  )
}

export default Cards
