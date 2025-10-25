import React, { useEffect, useState } from 'react';
import { getAllMutualFunds, investInMutualFund, getMyMutualFundHoldings, MutualFund, MutualFundHolding, MutualFundInvestmentRequest } from '../services/investmentService';
import { accountService } from '../services/accountService';

const MutualFundsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'browse' | 'holdings'>('browse');
  const [funds, setFunds] = useState<MutualFund[]>([]);
  const [holdings, setHoldings] = useState<MutualFundHolding[]>([]);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedFund, setSelectedFund] = useState<MutualFund | null>(null);
  const [showInvestModal, setShowInvestModal] = useState(false);

  const [investForm, setInvestForm] = useState<MutualFundInvestmentRequest>({
    fundCode: '',
    accountNumber: '',
    amount: 0
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [fundsData, holdingsData, accountsData] = await Promise.all([
        getAllMutualFunds(),
        getMyMutualFundHoldings(),
        accountService.getAccounts()
      ]);
      setFunds(fundsData);
      setHoldings(holdingsData);
      setAccounts(accountsData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleInvest = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      await investInMutualFund(investForm);
      setSuccess('Investment successful!');
      setShowInvestModal(false);
      await loadData();
      setActiveTab('holdings');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Investment failed');
    } finally {
      setLoading(false);
    }
  };

  const openInvestModal = (fund: MutualFund) => {
    setSelectedFund(fund);
    setInvestForm({
      fundCode: fund.fundCode,
      accountNumber: '',
      amount: fund.minInvestment
    });
    setShowInvestModal(true);
  };

  const formatCurrency = (amount: number) => `₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2 })}`;

  const getRiskColor = (risk: string) => {
    switch (risk) {
      case 'VERY_LOW': return '#22c55e';
      case 'LOW': return '#84cc16';
      case 'MODERATE': return '#eab308';
      case 'HIGH': return '#f97316';
      case 'VERY_HIGH': return '#ef4444';
      default: return '#64748b';
    }
  };

  return (
    <div style={{ padding: '20px', maxWidth: '1400px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '24px' }}>Mutual Funds</h1>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '24px', borderBottom: '2px solid #e2e8f0' }}>
        <button
          onClick={() => setActiveTab('browse')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'browse' ? '#ec4899' : 'transparent',
            color: activeTab === 'browse' ? 'white' : '#64748b',
            border: 'none',
            borderRadius: '8px 8px 0 0',
            cursor: 'pointer',
            fontWeight: '500'
          }}
        >
          Browse Funds
        </button>
        <button
          onClick={() => setActiveTab('holdings')}
          style={{
            padding: '12px 24px',
            background: activeTab === 'holdings' ? '#ec4899' : 'transparent',
            color: activeTab === 'holdings' ? 'white' : '#64748b',
            border: 'none',
            borderRadius: '8px 8px 0 0',
            cursor: 'pointer',
            fontWeight: '500'
          }}
        >
          My Holdings ({holdings.length})
        </button>
      </div>

      {error && <div style={{ padding: '12px', background: '#fee2e2', color: '#dc2626', borderRadius: '8px', marginBottom: '16px' }}>{error}</div>}
      {success && <div style={{ padding: '12px', background: '#d1fae5', color: '#059669', borderRadius: '8px', marginBottom: '16px' }}>{success}</div>}

      {/* Browse Funds Tab */}
      {activeTab === 'browse' && (
        <div style={{ display: 'grid', gap: '16px' }}>
          {funds.map((fund) => (
            <div key={fund.id} style={{ background: 'white', borderRadius: '12px', padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '16px' }}>
                <div style={{ flex: 1 }}>
                  <h3 style={{ margin: '0 0 8px 0' }}>{fund.fundName}</h3>
                  <div style={{ fontSize: '14px', color: '#64748b', marginBottom: '8px' }}>{fund.amc} • {fund.fundCode}</div>
                  <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                    <span style={{ padding: '4px 12px', background: '#dbeafe', color: '#1e40af', borderRadius: '12px', fontSize: '12px', fontWeight: '500' }}>
                      {fund.category}
                    </span>
                    <span style={{ padding: '4px 12px', background: getRiskColor(fund.riskLevel), color: 'white', borderRadius: '12px', fontSize: '12px', fontWeight: '500' }}>
                      {fund.riskLevel.replace('_', ' ')} RISK
                    </span>
                  </div>
                </div>
                <button
                  onClick={() => openInvestModal(fund)}
                  style={{ padding: '10px 20px', background: '#ec4899', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: '500' }}
                >
                  Invest Now
                </button>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '16px', marginTop: '16px' }}>
                <div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>Current NAV</div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(fund.currentNav)}</div>
                </div>
                <div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>Min Investment</div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(fund.minInvestment)}</div>
                </div>
                <div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>1Y Returns</div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: fund.returns1Year && fund.returns1Year >= 0 ? '#22c55e' : '#ef4444' }}>
                    {fund.returns1Year ? `${fund.returns1Year >= 0 ? '+' : ''}${fund.returns1Year.toFixed(2)}%` : 'N/A'}
                  </div>
                </div>
                <div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>3Y Returns</div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: fund.returns3Year && fund.returns3Year >= 0 ? '#22c55e' : '#ef4444' }}>
                    {fund.returns3Year ? `${fund.returns3Year >= 0 ? '+' : ''}${fund.returns3Year.toFixed(2)}%` : 'N/A'}
                  </div>
                </div>
                <div>
                  <div style={{ fontSize: '12px', color: '#64748b' }}>Expense Ratio</div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{fund.expenseRatio}%</div>
                </div>
                {fund.sipAvailable && (
                  <div>
                    <div style={{ fontSize: '12px', color: '#64748b' }}>Min SIP</div>
                    <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(fund.minSipAmount)}</div>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Holdings Tab */}
      {activeTab === 'holdings' && (
        <div>
          {holdings.length === 0 ? (
            <div style={{ background: 'white', borderRadius: '12px', padding: '40px', textAlign: 'center', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
              <h3>No Holdings Yet</h3>
              <p style={{ color: '#64748b', margin: '16px 0' }}>Start investing in mutual funds to build your portfolio!</p>
              <button
                onClick={() => setActiveTab('browse')}
                style={{ padding: '12px 24px', background: '#ec4899', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
              >
                Browse Mutual Funds
              </button>
            </div>
          ) : (
            <div style={{ display: 'grid', gap: '16px' }}>
              {holdings.map((holding) => (
                <div key={holding.id} style={{ background: 'white', borderRadius: '12px', padding: '20px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
                  <div style={{ marginBottom: '16px' }}>
                    <h3 style={{ margin: '0 0 8px 0' }}>{holding.mutualFund.fundName}</h3>
                    <div style={{ fontSize: '14px', color: '#64748b' }}>Folio: {holding.folioNumber}</div>
                  </div>

                  <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))', gap: '16px' }}>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Units</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{holding.units.toFixed(4)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Invested</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(holding.totalInvested)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Current Value</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#1e293b' }}>{formatCurrency(holding.currentValue)}</div>
                    </div>
                    <div>
                      <div style={{ fontSize: '12px', color: '#64748b' }}>Gain/Loss</div>
                      <div style={{ fontSize: '18px', fontWeight: 'bold', color: holding.totalGainLoss >= 0 ? '#22c55e' : '#ef4444' }}>
                        {formatCurrency(holding.totalGainLoss)} ({holding.returnPercentage >= 0 ? '+' : ''}{holding.returnPercentage.toFixed(2)}%)
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Invest Modal */}
      {showInvestModal && selectedFund && (
        <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
          <div style={{ background: 'white', borderRadius: '12px', padding: '24px', maxWidth: '500px', width: '90%', maxHeight: '90vh', overflow: 'auto' }}>
            <h2 style={{ marginBottom: '16px' }}>Invest in {selectedFund.fundName}</h2>
            <form onSubmit={handleInvest} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>Select Account</label>
                <select
                  value={investForm.accountNumber}
                  onChange={(e) => setInvestForm({ ...investForm, accountNumber: e.target.value })}
                  required
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d1d5db' }}
                >
                  <option value="">Choose Account</option>
                  {accounts.map((account) => (
                    <option key={account.accountNumber} value={account.accountNumber}>
                      {account.accountNumber} - Balance: ₹{account.balance.toLocaleString('en-IN')}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '8px', fontWeight: '500' }}>
                  Amount (Min: {formatCurrency(selectedFund.minInvestment)})
                </label>
                <input
                  type="number"
                  value={investForm.amount || ''}
                  onChange={(e) => setInvestForm({ ...investForm, amount: parseFloat(e.target.value) })}
                  required
                  min={selectedFund.minInvestment}
                  step="100"
                  style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #d1d5db' }}
                />
              </div>

              <div style={{ padding: '12px', background: '#f8fafc', borderRadius: '6px' }}>
                <div style={{ fontSize: '14px', color: '#64748b', marginBottom: '4px' }}>Estimated Units</div>
                <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#1e293b' }}>
                  {((investForm.amount || 0) / selectedFund.currentNav).toFixed(4)} units
                </div>
              </div>

              <div style={{ display: 'flex', gap: '12px' }}>
                <button
                  type="button"
                  onClick={() => setShowInvestModal(false)}
                  style={{ flex: 1, padding: '12px', background: '#e2e8f0', color: '#1e293b', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: '500' }}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  style={{ flex: 1, padding: '12px', background: '#ec4899', color: 'white', border: 'none', borderRadius: '8px', cursor: loading ? 'not-allowed' : 'pointer', fontWeight: '500' }}
                >
                  {loading ? 'Investing...' : 'Invest'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default MutualFundsPage;
