import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const AdminLoanApprovals: React.FC = () => {
  const [applications, setApplications] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { logout } = useAuth()

  useEffect(() => {
    loadPendingApplications()
  }, [])

  const loadPendingApplications = async () => {
    try {
      setLoading(true)
      const res = await api.get('/loans/applications/pending')
      setApplications(res.data || [])
    } catch (err: any) {
      if (err.response?.status === 401) logout()
      else setError('Failed to load applications')
    } finally {
      setLoading(false)
    }
  }

  const approveLoan = async (id: number) => {
    try {
      await api.post(`/loans/applications/${id}/approve`)
      setSuccess(`Application #${id} approved successfully!`)
      loadPendingApplications()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to approve loan')
    }
  }

  const rejectLoan = async (id: number) => {
    const remarks = prompt('Enter reason for rejection:')
    if (remarks === null) return

    try {
      await api.post(`/loans/applications/${id}/reject`, { remarks })
      setSuccess(`Application #${id} rejected.`)
      loadPendingApplications()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to reject loan')
    }
  }

  if (loading) return <div className="center">Loading pending applications...</div>

  return (
    <div className="container">
      <h2>Admin: Loan Approvals</h2>

      {error && <div style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      {applications.length === 0 ? (
        <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
          No pending loan applications.
        </div>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ background: '#f8f9fa', textAlign: 'left' }}>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>ID</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>User</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Type</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Amount</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Tenure</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Income</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Date</th>
              <th style={{ padding: 12, borderBottom: '2px solid #dee2e6' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {applications.map((app: any) => (
              <tr key={app.id}>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{app.id}</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{app.user?.username || 'N/A'}</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{app.loanType}</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>${app.amount.toFixed(2)}</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>{app.tenureMonths} months</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>${app.monthlyIncome.toFixed(2)}</td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6' }}>
                  {new Date(app.applicationDate).toLocaleDateString()}
                </td>
                <td style={{ padding: 12, borderBottom: '1px solid #dee2e6', display: 'flex', gap: 8 }}>
                  <button
                    onClick={() => approveLoan(app.id)}
                    style={{ padding: '6px 12px', background: '#28a745', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                  >
                    Approve
                  </button>
                  <button
                    onClick={() => rejectLoan(app.id)}
                    style={{ padding: '6px 12px', background: '#dc3545', color: 'white', border: 'none', borderRadius: 3, cursor: 'pointer' }}
                  >
                    Reject
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default AdminLoanApprovals
