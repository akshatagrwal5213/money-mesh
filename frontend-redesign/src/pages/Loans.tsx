import React, { useState, useEffect } from 'react'
import api from '../services/api'
import { useAuth } from '../contexts/AuthContext'

const Loans: React.FC = () => {
  const [loans, setLoans] = useState<any[]>([])
  const [applications, setApplications] = useState<any[]>([])
  const [accounts, setAccounts] = useState<any[]>([])
  const [activeTab, setActiveTab] = useState<'calculator' | 'apply' | 'loans' | 'applications'>('calculator')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const { logout, isAdmin } = useAuth()

  // Calculator states
  const [principal, setPrincipal] = useState('')
  const [interestRate, setInterestRate] = useState('')
  const [tenureMonths, setTenureMonths] = useState('')
  const [emiResult, setEmiResult] = useState<any>(null)

  // Application states
  const [loanType, setLoanType] = useState('PERSONAL')
  const [loanAmount, setLoanAmount] = useState('')
  const [loanTenure, setLoanTenure] = useState('')
  const [loanPurpose, setLoanPurpose] = useState('')
  const [monthlyIncome, setMonthlyIncome] = useState('')
  const [employmentType, setEmploymentType] = useState('SALARIED')

  useEffect(() => {
    if (activeTab === 'loans') loadLoans()
    if (activeTab === 'applications') loadApplications()
    if (activeTab === 'apply') loadAccounts()
  }, [activeTab])

  const loadLoans = async () => {
    try {
      setLoading(true)
      const res = await api.get('/loans')
      setLoans(res.data || [])
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  const loadApplications = async () => {
    try {
      setLoading(true)
      const res = await api.get('/loans/applications')
      setApplications(res.data || [])
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    } finally {
      setLoading(false)
    }
  }

  const loadAccounts = async () => {
    try {
      const res = await api.get('/accounts')
      setAccounts(Array.isArray(res.data) ? res.data : (res.data.accounts || []))
    } catch (err: any) {
      if (err.response?.status === 401) logout()
    }
  }

  const calculateEMI = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    try {
      const res = await api.post('/loans/calculate-emi', {
        principal: parseFloat(principal),
        interestRate: parseFloat(interestRate),
        tenureMonths: parseInt(tenureMonths)
      })
      setEmiResult(res.data)
    } catch (err: any) {
      setError('Failed to calculate EMI')
    }
  }

  const applyForLoan = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      await api.post('/loans/apply', {
        loanType,
        amount: parseFloat(loanAmount),
        tenureMonths: parseInt(loanTenure),
        purpose: loanPurpose,
        monthlyIncome: parseFloat(monthlyIncome),
        employmentType
      })
      setSuccess('Loan application submitted successfully!')
      setLoanAmount('')
      setLoanTenure('')
      setLoanPurpose('')
      setMonthlyIncome('')
      setActiveTab('applications')
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to submit application')
    }
  }

  const makePayment = async (loanId: number) => {
    if (!accounts.length) {
      setError('No accounts available for payment')
      return
    }

    const accountId = prompt(`Enter account ID from: ${accounts.map(a => `${a.id}(${a.accountNumber})`).join(', ')}`)
    if (!accountId) return

    try {
      await api.post(`/loans/${loanId}/pay`, { accountId: parseInt(accountId) })
      setSuccess('Payment successful!')
      loadLoans()
    } catch (err: any) {
      setError(err.response?.data?.message || 'Payment failed')
    }
  }

  return (
    <div className="container">
      <h2>Loans</h2>

      <div style={{ display: 'flex', gap: 8, marginBottom: 20, borderBottom: '2px solid #dee2e6' }}>
        {['calculator', 'apply', 'loans', 'applications'].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab as any)}
            style={{
              padding: '10px 20px',
              background: activeTab === tab ? '#007bff' : 'transparent',
              color: activeTab === tab ? 'white' : '#007bff',
              border: 'none',
              borderBottom: activeTab === tab ? '2px solid #007bff' : 'none',
              cursor: 'pointer',
              fontWeight: 600,
              textTransform: 'capitalize'
            }}
          >
            {tab === 'calculator' ? 'EMI Calculator' : tab}
          </button>
        ))}
      </div>

      {error && <div style={{ padding: 12, marginBottom: 16, background: '#fee', color: '#c00', borderRadius: 4 }}>{error}</div>}
      {success && <div style={{ padding: 12, marginBottom: 16, background: '#efe', color: '#080', borderRadius: 4 }}>{success}</div>}

      {activeTab === 'calculator' && (
        <div style={{ maxWidth: 500 }}>
          <h3>EMI Calculator</h3>
          <form onSubmit={calculateEMI}>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Loan Amount</label>
              <input 
                type="number" 
                step="0.01"
                value={principal}
                onChange={e => setPrincipal(e.target.value)}
                required
                style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
              />
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Interest Rate (%)</label>
              <input 
                type="number" 
                step="0.01"
                value={interestRate}
                onChange={e => setInterestRate(e.target.value)}
                required
                style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
              />
            </div>
            <div style={{ marginBottom: 16 }}>
              <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Tenure (Months)</label>
              <input 
                type="number"
                value={tenureMonths}
                onChange={e => setTenureMonths(e.target.value)}
                required
                style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
              />
            </div>
            <button type="submit" style={{ padding: '10px 20px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}>
              Calculate EMI
            </button>
          </form>

          {emiResult && (
            <div style={{ marginTop: 24, padding: 20, background: '#f8f9fa', borderRadius: 8 }}>
              <h4>EMI Details</h4>
              <div style={{ fontSize: 32, fontWeight: 700, color: '#007bff', marginBottom: 16 }}>
                ${emiResult.emi.toFixed(2)}/month
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div>
                  <div style={{ fontSize: 12, color: '#6c757d' }}>Total Interest</div>
                  <div style={{ fontSize: 20, fontWeight: 600 }}>${emiResult.totalInterest.toFixed(2)}</div>
                </div>
                <div>
                  <div style={{ fontSize: 12, color: '#6c757d' }}>Total Payment</div>
                  <div style={{ fontSize: 20, fontWeight: 600 }}>${emiResult.totalPayment.toFixed(2)}</div>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {activeTab === 'apply' && (
        <form onSubmit={applyForLoan} style={{ maxWidth: 600 }}>
          <h3>Apply for Loan</h3>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Loan Type</label>
            <select 
              value={loanType} 
              onChange={e => setLoanType(e.target.value)}
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            >
              <option value="PERSONAL">Personal Loan</option>
              <option value="HOME">Home Loan</option>
              <option value="CAR">Car Loan</option>
              <option value="EDUCATION">Education Loan</option>
            </select>
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Loan Amount</label>
            <input 
              type="number" 
              step="0.01"
              value={loanAmount}
              onChange={e => setLoanAmount(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Tenure (Months)</label>
            <input 
              type="number"
              value={loanTenure}
              onChange={e => setLoanTenure(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Purpose</label>
            <input 
              type="text"
              value={loanPurpose}
              onChange={e => setLoanPurpose(e.target.value)}
              required
              placeholder="e.g., Home renovation"
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Monthly Income</label>
            <input 
              type="number" 
              step="0.01"
              value={monthlyIncome}
              onChange={e => setMonthlyIncome(e.target.value)}
              required
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            />
          </div>
          <div style={{ marginBottom: 16 }}>
            <label style={{ display: 'block', marginBottom: 4, fontWeight: 600 }}>Employment Type</label>
            <select 
              value={employmentType} 
              onChange={e => setEmploymentType(e.target.value)}
              style={{ width: '100%', padding: 8, borderRadius: 4, border: '1px solid #ccc' }}
            >
              <option value="SALARIED">Salaried</option>
              <option value="SELF_EMPLOYED">Self Employed</option>
              <option value="BUSINESS">Business</option>
            </select>
          </div>
          <button type="submit" style={{ padding: '10px 20px', background: '#28a745', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 600 }}>
            Submit Application
          </button>
        </form>
      )}

      {activeTab === 'loans' && (
        <div>
          <h3>My Loans</h3>
          {loading ? <div>Loading...</div> : loans.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>No active loans</div>
          ) : (
            <div style={{ display: 'grid', gap: 16 }}>
              {loans.map(loan => (
                <div key={loan.id} style={{ padding: 20, border: '1px solid #dee2e6', borderRadius: 8 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                    <div>
                      <div style={{ fontSize: 18, fontWeight: 600 }}>{loan.loanType} Loan</div>
                      <div style={{ fontSize: 12, color: '#6c757d' }}>Account: {loan.accountNumber}</div>
                    </div>
                    <div style={{ 
                      padding: '4px 12px', 
                      background: loan.status === 'ACTIVE' ? '#28a745' : '#6c757d', 
                      color: 'white', 
                      borderRadius: 4, 
                      fontSize: 12,
                      height: 'fit-content'
                    }}>
                      {loan.status}
                    </div>
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginBottom: 16 }}>
                    <div>
                      <div style={{ fontSize: 11, color: '#6c757d' }}>Principal</div>
                      <div style={{ fontSize: 18, fontWeight: 600 }}>${loan.principalAmount.toFixed(2)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: 11, color: '#6c757d' }}>Outstanding</div>
                      <div style={{ fontSize: 18, fontWeight: 600 }}>${loan.outstandingAmount.toFixed(2)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: 11, color: '#6c757d' }}>EMI</div>
                      <div style={{ fontSize: 18, fontWeight: 600 }}>${loan.emiAmount.toFixed(2)}</div>
                    </div>
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <div style={{ fontSize: 11, color: '#6c757d', marginBottom: 4 }}>
                      Next Payment: {new Date(loan.nextPaymentDate).toLocaleDateString()}
                    </div>
                    <div style={{ background: '#e9ecef', borderRadius: 4, height: 8, overflow: 'hidden' }}>
                      <div style={{ 
                        background: '#007bff', 
                        height: '100%', 
                        width: `${((loan.principalAmount - loan.outstandingAmount) / loan.principalAmount * 100)}%` 
                      }}></div>
                    </div>
                    <div style={{ fontSize: 11, color: '#6c757d', marginTop: 4 }}>
                      {((loan.principalAmount - loan.outstandingAmount) / loan.principalAmount * 100).toFixed(1)}% repaid
                    </div>
                  </div>
                  <button
                    onClick={() => makePayment(loan.id)}
                    style={{ padding: '8px 16px', background: '#007bff', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer' }}
                  >
                    Make Payment
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'applications' && (
        <div>
          <h3>My Applications</h3>
          {loading ? <div>Loading...</div> : applications.length === 0 ? (
            <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>No applications</div>
          ) : (
            <div style={{ display: 'grid', gap: 16 }}>
              {applications.map(app => (
                <div key={app.id} style={{ padding: 20, border: '1px solid #dee2e6', borderRadius: 8 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ fontSize: 18, fontWeight: 600 }}>{app.loanType} Loan</div>
                      <div style={{ fontSize: 14, color: '#6c757d', marginTop: 4 }}>
                        ${app.amount.toFixed(2)} for {app.tenureMonths} months
                      </div>
                      <div style={{ fontSize: 12, color: '#6c757d', marginTop: 8 }}>
                        Applied: {new Date(app.applicationDate).toLocaleDateString()}
                      </div>
                    </div>
                    <div style={{ 
                      padding: '4px 12px', 
                      background: app.status === 'PENDING' ? '#ffc107' : app.status === 'APPROVED' ? '#28a745' : '#dc3545',
                      color: 'white', 
                      borderRadius: 4, 
                      fontSize: 12,
                      height: 'fit-content'
                    }}>
                      {app.status}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default Loans
