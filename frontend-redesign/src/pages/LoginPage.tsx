import React, { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'
import { useNavigate, useLocation } from 'react-router-dom'

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('password')
  const [error, setError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  // Check for success message from registration
  React.useEffect(() => {
    if (location.state?.message) {
      setSuccessMessage(location.state.message)
      if (location.state?.username) {
        setUsername(location.state.username)
      }
    }
  }, [location.state])

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await login(username, password)
      // Redirect to dashboard after successful login
      window.location.href = '/dashboard'
    } catch (err: any) {
      setError(err.response?.data?.message || 'Login failed')
    }
  }

  return (
    <div className="center">
      <form onSubmit={handleLogin} className="card">
        <h2>MoneyMesh Login</h2>
        {successMessage && <div style={{ padding: '12px', backgroundColor: '#d4edda', color: '#155724', borderRadius: '4px', marginBottom: '16px' }}>{successMessage}</div>}
        {error && <div className="error">{error}</div>}
        <input value={username} onChange={e => setUsername(e.target.value)} placeholder="Username" />
        <input value={password} onChange={e => setPassword(e.target.value)} placeholder="Password" type="password" />
        <button type="submit">Login</button>
        <div style={{ marginTop: '20px', textAlign: 'center', paddingTop: '20px', borderTop: '1px solid #e0e0e0' }}>
          <p style={{ color: '#666', marginBottom: '10px' }}>Don't have an account?</p>
          <button 
            type="button" 
            onClick={() => navigate('/register')}
            style={{ 
              backgroundColor: '#4caf50', 
              color: 'white',
              border: 'none',
              padding: '10px 24px',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: 'bold'
            }}
          >
            Create New Account
          </button>
        </div>
      </form>
    </div>
  )
}

export default LoginPage
