import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

interface Notification {
  id: number
  title: string
  message: string
  type: string
  priority: string
  isRead: boolean
  createdAt: string
}

const Notifications: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>([])
  const [filter, setFilter] = useState<string>('all')
  const [loading, setLoading] = useState(true)
  const { logout } = useAuth()

  useEffect(() => {
    loadNotifications()
  }, [filter])

  const loadNotifications = async () => {
    try {
      setLoading(true)
      const endpoint = filter === 'unread' ? '/notifications/unread' : '/notifications'
      const res = await api.get(endpoint)
      setNotifications(res.data)
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  const markAsRead = async (id: number) => {
    try {
      await api.post(`/notifications/${id}/read`)
      setNotifications(notifications.map(n => n.id === id ? { ...n, isRead: true } : n))
    } catch (err) {
      console.error('Error marking as read:', err)
    }
  }

  const markAllAsRead = async () => {
    try {
      await api.post('/notifications/read-all')
      setNotifications(notifications.map(n => ({ ...n, isRead: true })))
    } catch (err) {
      console.error('Error marking all as read:', err)
    }
  }

  const deleteNotification = async (id: number) => {
    try {
      await api.delete(`/notifications/${id}`)
      setNotifications(notifications.filter(n => n.id !== id))
    } catch (err) {
      console.error('Error deleting notification:', err)
    }
  }

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return '#dc3545'
      case 'MEDIUM': return '#ffc107'
      case 'LOW': return '#28a745'
      default: return '#6c757d'
    }
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2>Notifications</h2>
        <div>
          <button 
            onClick={markAllAsRead}
            style={{ padding: '8px 16px', marginRight: 8, background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
          >
            Mark All Read
          </button>
          <select 
            value={filter} 
            onChange={e => setFilter(e.target.value)}
            style={{ padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
          >
            <option value="all">All Notifications</option>
            <option value="unread">Unread Only</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div>Loading notifications...</div>
      ) : notifications.length === 0 ? (
        <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
          No notifications to display
        </div>
      ) : (
        <div>
          {notifications.map(notif => (
            <div 
              key={notif.id}
              style={{
                padding: 16,
                marginBottom: 12,
                background: notif.isRead ? '#f8f9fa' : '#fff',
                border: '1px solid #dee2e6',
                borderRadius: 4,
                borderLeft: `4px solid ${getPriorityColor(notif.priority)}`
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                    <h4 style={{ margin: 0, fontWeight: 600 }}>{notif.title}</h4>
                    {!notif.isRead && (
                      <span style={{ 
                        fontSize: 10, 
                        padding: '2px 6px', 
                        background: '#007bff', 
                        color: 'white', 
                        borderRadius: 10 
                      }}>
                        NEW
                      </span>
                    )}
                    <span style={{ 
                      fontSize: 11, 
                      padding: '2px 6px', 
                      background: '#e9ecef', 
                      borderRadius: 3 
                    }}>
                      {notif.type}
                    </span>
                  </div>
                  <p style={{ margin: '8px 0', color: '#495057' }}>{notif.message}</p>
                  <small style={{ color: '#6c757d' }}>
                    {new Date(notif.createdAt).toLocaleString()}
                  </small>
                </div>
                <div style={{ display: 'flex', gap: 8 }}>
                  {!notif.isRead && (
                    <button
                      onClick={() => markAsRead(notif.id)}
                      style={{ 
                        padding: '4px 8px', 
                        fontSize: 12, 
                        background: '#28a745', 
                        color: 'white', 
                        border: 'none', 
                        borderRadius: 3, 
                        cursor: 'pointer' 
                      }}
                    >
                      Mark Read
                    </button>
                  )}
                  <button
                    onClick={() => deleteNotification(notif.id)}
                    style={{ 
                      padding: '4px 8px', 
                      fontSize: 12, 
                      background: '#dc3545', 
                      color: 'white', 
                      border: 'none', 
                      borderRadius: 3, 
                      cursor: 'pointer' 
                    }}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Notifications
