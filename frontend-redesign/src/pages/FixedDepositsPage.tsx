import React, { useEffect, useState } from 'react';
import { createFixedDeposit, getMyFixedDeposits, closeFixedDeposit, FixedDeposit, FixedDepositRequest, MaturityAction, InvestmentStatus } from '../services/investmentService';
import { accountService } from '../services/accountService';

const FixedDepositsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'create' | 'view'>('view');
  const [fixedDeposits, setFixedDeposits] = useState<FixedDeposit[]>([]);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState<FixedDepositRequest>({
    accountNumber: '',
    principalAmount: 0,
    tenureMonths: 12,
    maturityAction: MaturityAction.CREDIT_TO_ACCOUNT,
    autoRenew: false
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [fdsData, accountsData] = await Promise.all([
        getMyFixedDeposits(),
        accountService.getAccounts()
      ]);
      setFixedDeposits(fdsData);
      setAccounts(accountsData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateFD = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      await createFixedDeposit(formData);
      setSuccess('Fixed Deposit created successfully!');
      setFormData({
        accountNumber: '',
        principalAmount: 0,
        tenureMonths: 12,
        maturityAction: MaturityAction.CREDIT_TO_ACCOUNT,
        autoRenew: false
      });
      await loadData();
      setTimeout(() => setActiveTab('view'), 2000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to create Fixed Deposit');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseFD = async (fdNumber: string) => {
    if (!confirm('Are you sure you want to close this Fixed Deposit? This action cannot be undone.')) return;
    
    try {
      setLoading(true);
      await closeFixedDeposit(fdNumber);
      setSuccess('Fixed Deposit closed successfully!');
      await loadData();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to close Fixed Deposit');
    } finally {
      setLoading(false);
    }
  };

  const getInterestRate = (months: number) => {
    if (months <= 6) return '5.5%';
    if (months <= 12) return '6.0%';
    if (months <= 24) return '6.5%';
    if (months <= 36) return '7.0%';
    return '7.5%';
  };

  const formatCurrency = (amount: number) => {
    return `₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;
  };

  const getStatusColor = (status: InvestmentStatus) => {
    switch (status) {
      case InvestmentStatus.ACTIVE: return '#22c55e';
      case InvestmentStatus.MATURED: return '#3b82f6';
      case InvestmentStatus.CLOSED: return '#64748b';
      default: return '#64748b';
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '24px' }}>Fixed Deposits</h1>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '24px', borderBottom: '2px solid #e2e8f0' }}>
        <button
          onClick={() => setActiveTab('view')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'view' ? '#3b82f6' : 'transparent',
            color: activeTab === 'view' ? 'white' : '#64748b',
            border: 'none',
            borderRadius: '8px 8px 0 0',
            cursor: 'pointer',
            fontWeight: '500'
          }}
        >
          My Fixed Deposits
        </button>
        <button
          onClick={() => setActiveTab('create')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'create' ? '#3b82f6' : 'transparent',
            color: activeTab === 'create' ? 'white' : '#64748b',
            border: 'none',
            borderRadius: '8px 8px 0 0',
            cursor: 'pointer',
            fontWeight: '500'
          }}
        >
          Create Fixed Deposit
        </button>
      </div>

      {error && <div style={{ padding: '12px', background: '#fee2e2', color: '#dc2626', borderRadius: '8px', marginBottom: '16px' }}>{error}</div>}
      {success && <div style={{ padding: '12px', background: '#d1fae5', color: '#059669', borderRadius: '8px', marginBottom: '16px' }}>{success}</div>}

      {/* Create FD Tab */}
      {activeTab === 'create' && (
        <div style={{ background: 'white', borderRadius: '12px', padding: '24px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
          <h2 style={{ marginBottom: '20px' }}>Create New Fixed Deposit</h2>
          <form onSubmit={handleCreateFD} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>Select Account</label>
              <select
                value={formData.accountNumber}
                onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
                required
                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d1d5db' }}
              >
                <option value="">Choose Account</option>
                {accounts.map((account) => (
                  <option key={account.accountNumber} value={account.accountNumber}>
                    {account.accountNumber} - {account.accountType} (Balance: ₹{account.balance.toLocaleString('en-IN')})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>Principal Amount (₹)</label>
              <input
                type="number"
                value={formData.principalAmount || ''}
                onChange={(e) => setFormData({ ...formData, principalAmount: parseFloat(e.target.value) })}
                required
                min="1000"
                step="100"
                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d1d5db' }}
                placeholder="Minimum ₹1,000"
              />
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>
                Tenure (Months) - Interest Rate: {getInterestRate(formData.tenureMonths)}
              </label>
              <input
                type="range"
                value={formData.tenureMonths}
                onChange={(e) => setFormData({ ...formData, tenureMonths: parseInt(e.target.value) })}
                min="6"
                max="60"
                step="6"
                style={{ width: '100%' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '14px', color: '#64748b' }}>
                <span>6 months</span>
                <span style={{ fontWeight: 'bold', color: '#3b82f6' }}>{formData.tenureMonths} months</span>
                <span>60 months</span>
              </div>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>Maturity Action</label>
              <select
                value={formData.maturityAction}
                onChange={(e) => setFormData({ ...formData, maturityAction: e.target.value as MaturityAction })}
                style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d1d5db' }}
              >
                <option value={MaturityAction.CREDIT_TO_ACCOUNT}>Credit to Account</option>
                <option value={MaturityAction.RENEW_PRINCIPAL}>Renew Principal Only</option>
                <option value={MaturityAction.RENEW_PRINCIPAL_AND_INTEREST}>Renew Principal + Interest</option>
              </select>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <input
                type="checkbox"
                checked={formData.autoRenew}
                onChange={(e) => setFormData({ ...formData, autoRenew: e.target.checked })}
                id="autoRenew"
              />
              <label htmlFor="autoRenew" style={{ fontWeight: '500' }}>Enable Auto-Renewal</label>
            </div>

            <button
              type="submit"
              disabled={loading}
              style={{
                padding: '12px',
                background: '#3b82f6',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                cursor: loading ? 'not-allowed' : 'pointer',
                fontWeight: '500',
                fontSize: '16px'
              }}
            >
              {loading ? 'Creating...' : 'Create Fixed Deposit'}
            </button>
          </form>
        </div>
      )}

      {/* View FDs Tab */}
      {activeTab === 'view' && (
        <div>
          {loading ? (
            <div>Loading...</div>
          ) : fixedDeposits.length === 0 ? (
            <div style={{ background: 'white', borderRadius: '12px', padding: '40px', textAlign: 'center', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3>No Fixed Deposits Yet</h3>
              <p style={{ color: '#64748b', margin: '16px 0' }}>Start investing in Fixed Deposits to earn guaranteed returns!</p>
              <button
                onClick={() => setActiveTab('create')}
                style={{ padding: '12px 24px', background: '#3b82f6', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
              >
                Create Your First FD
              </button>
            </div>
          ) : (
            <div style={{ display: 'grid', gap: '16px' }}>
              {fixedDeposits.map((fd) => (
                <div key={fd.id} style={{ background: 'white', borderRadius: '12px', padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '16px' }}>
                    <div>
                      <h3 style={{ margin: '0 0 8px 0' }}>{fd.fdNumber}</h3>
                      <span style={{ padding: '4px 12px', background: getStatusColor(fd.status), color: 'white', borderRadius: '12px', fontSize: '12px', fontWeight: '500' }}>
                        {fd.status}
                      </span>
                    </div>
                    {fd.status === InvestmentStatus.ACTIVE && (
                      <button
                        onClick={() => handleCloseFD(fd.fdNumber)}
                        style={{ padding: '8px 16px', background: '#ef4444', color: 'white', border: 'none', borderRadius: '6px', cursor: 'pointer' }}
                      >
                        Close FD
                      </button>
                    )}
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Principal Amount</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(fd.principalAmount)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Interest Rate</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{fd.interestRate}%</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Maturity Amount</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#22c55e' }}>{formatCurrency(fd.maturityAmount)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Interest Earned</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#3b82f6' }}>{formatCurrency(fd.interestEarned)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Tenure</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{fd.tenureMonths} months</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Maturity Date</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{new Date(fd.maturityDate).toLocaleDateString('en-IN')}</div>
                    </div>
                  </div>

                  {fd.autoRenew && (
                    <div style={{ marginTop: '12px', padding: '8px', background: '#dbeafe', borderRadius: '6px', fontSize: '14px', color: '#1e40af' }}>
                      ✓ Auto-renewal enabled
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default FixedDepositsPage;
