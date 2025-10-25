import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const AdminCustomersPage: React.FC = () => {
  const navigate = useNavigate()
  const [customers, setCustomers] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { logout } = useAuth()

  // Form state
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [showCreateForm, setShowCreateForm] = useState(false)

  useEffect(() => {
    loadCustomers()
  }, [])

  const loadCustomers = async () => {
    try {
      setLoading(true)
      const res = await api.get('/customers')
      setCustomers(res.data || [])
    } catch (err: any) {
      if (err.response?.status === 401) logout()
      else setError('Failed to load customers')
    } finally {
      setLoading(false)
    }
  }

  const createCustomer = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await api.post('/customers', { name, email, phone })
      setSuccess('Customer created successfully!')
      setShowCreateForm(false)
      setName('')
      setEmail('')
      setPhone('')
      loadCustomers()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create customer')
    }
  }

  if (loading) return <div className="center">Loading customers...</div>

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2>Admin: Manage Customers</h2>
        <div style={{ display: 'flex', gap: 10 }}>
          <button 
            onClick={() => navigate('/admin/create-customer')}
            style={{ 
              padding: '10px 20px', 
              background: '#28a745', 
              color: 'white', 
              border: 'none', 
              borderRadius: 4, 
              cursor: 'pointer', 
              fontWeight: 600,
              display: 'flex',
              alignItems: 'center',
              gap: 8
            }}
          >
            <span style={{ fontSize: '18px' }}>+</span>
            Create Customer & Account
          </button>
          <button 
            onClick={() => setShowCreateForm(!showCreateForm)}
            style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}
          >
            {showCreateForm ? 'Cancel' : '+ Create Customer Only'}
          </button>
        </div>
      </div>

      {error && <div style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      {showCreateForm && (
        <form onSubmit={createCustomer} style={{ padding: 20, background: '#f8f9fa', borderRadius: 8, marginBottom: 24 }}>
          <h3>Create New Customer</h3>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Full Name</label>
            <input 
              type="text" 
              value={name}
              onChange={e => setName(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Email</label>
            <input 
              type="email" 
              value={email}
              onChange={e => setEmail(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Phone</label>
            <input 
              type="text" 
              value={phone}
              onChange={e => setPhone(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <button type="submit" style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}>
            Create Customer
          </button>
        </form>
      )}

      <table style={{ width: '100%', borderCollapse: 'collapse' }}>
        <thead>
          <tr style={{ background: '#f8f9fa', textAlign: 'left' }}>
            <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>ID</th>
            <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Name</th>
            <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Email</th>
            <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Phone</th>
            <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>User ID</th>
          </tr>
        </thead>
        <tbody>
          {customers.map((c: any) => (
            <tr key={c.id}>
              <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{c.id}</td>
              <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{c.name}</td>
              <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{c.email}</td>
              <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{c.phone}</td>
              <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{c.user?.id || 'N/A'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default AdminCustomersPage
