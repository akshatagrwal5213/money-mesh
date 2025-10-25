import React, { createContext, useContext, useState, useEffect } from 'react'
import api from '../services/api'

interface User { id: number; username: string; roles?: string[] }

interface AuthContextType {
  user: User | null
  login: (username: string, password: string) => Promise<void>
  logout: () => void
  isAuthenticated: boolean
  isAdmin: boolean
  hasAccounts: boolean
  checkUserAccounts: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    try { return JSON.parse(localStorage.getItem('user') || 'null') } catch { return null }
  })
  const [hasAccounts, setHasAccounts] = useState<boolean>(() => {
    try { return JSON.parse(localStorage.getItem('hasAccounts') || 'false') } catch { return false }
  })

  const checkUserAccounts = async () => {
    try {
      if (!user || user.roles?.includes('ADMIN')) {
        console.log('[AuthContext] User is admin or null, setting hasAccounts to true')
        setHasAccounts(true)
        return
      }
      console.log('[AuthContext] Checking user accounts...')
      const response = await api.get('/accounts')
      console.log('[AuthContext] Accounts response:', response.data)
      const accountsExist = response.data && Array.isArray(response.data) && response.data.length > 0
      console.log('[AuthContext] Accounts exist:', accountsExist)
      setHasAccounts(accountsExist)
      localStorage.setItem('hasAccounts', JSON.stringify(accountsExist))
    } catch (error) {
      console.error('[AuthContext] Failed to check user accounts:', error)
      setHasAccounts(false)
      localStorage.setItem('hasAccounts', 'false')
    }
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token && user && !user.roles?.includes('ADMIN')) {
      checkUserAccounts()
    }
  }, [user])

  const login = async (username: string, password: string) => {
    const res = await api.post('/auth/login', { username, password })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    localStorage.setItem('username', username) // Store username for verification prompts
    setUser(res.data.user)
    // Check accounts after login
    setTimeout(() => checkUserAccounts(), 500)
  }

  const logout = () => {
    console.log('[AuthContext] Logout called - clearing localStorage and redirecting');
    
    // Clear localStorage first
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('username')
    localStorage.removeItem('hasAccounts')
    
    // Update React state
    setUser(null)
    setHasAccounts(false)
    
    // Force immediate redirect using replace (doesn't add to history)
    console.log('[AuthContext] Current path:', window.location.pathname);
    console.log('[AuthContext] Redirecting to /login');
    
    // Use a small delay to ensure state is updated
    setTimeout(() => {
      window.location.replace('/login');
    }, 50);
  }

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      isAuthenticated: !!user, 
      isAdmin: !!user?.roles?.includes('ADMIN'),
      hasAccounts,
      checkUserAccounts
    }}>
      {children}
    </AuthContext.Provider>
  )
}
