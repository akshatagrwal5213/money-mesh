import React, { useEffect, useState } from 'react';
import { getMyPortfolio, InvestmentPortfolio } from '../services/investmentService';

const PortfolioPage: React.FC = () => {
  const [portfolio, setPortfolio] = useState<InvestmentPortfolio | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPortfolio();
  }, []);

  const loadPortfolio = async () => {
    try {
      setLoading(true);
      const data = await getMyPortfolio();
      setPortfolio(data);
      setError('');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load portfolio');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return `₹${amount.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  const formatPercentage = (value: number) => {
    return `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  if (loading) return <div style={{ padding: '20px' }}>Loading portfolio...</div>;
  if (error) return <div style={{ padding: '20px', color: 'red' }}>{error}</div>;
  if (!portfolio) return <div style={{ padding: '20px' }}>No portfolio data available</div>;

  const gainLossColor = portfolio.totalGainLoss >= 0 ? '#22c55e' : '#ef4444';

  return (
    <div style={{ padding: '20px', maxWidth: '1400px', margin: '0 auto' }}>
      <h1 style={{ marginBottom: '30px' }}>Investment Portfolio</h1>

      {/* Summary Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '30px' }}>
        <div style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', borderRadius: '12px', padding: '20px', color: 'white', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px' }}>Total Investment Value</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{formatCurrency(portfolio.totalInvestmentValue)}</div>
        </div>

        <div style={{ background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)', borderRadius: '12px', padding: '20px', color: 'white', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px' }}>Total Invested</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{formatCurrency(portfolio.totalInvested)}</div>
        </div>

        <div style={{ background: `linear-gradient(135deg, ${portfolio.totalGainLoss >= 0 ? '#11998e 0%, #38ef7d' : '#ee0979 0%, #ff6a00'} 100%)`, borderRadius: '12px', padding: '20px', color: 'white', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px' }}>Total Gain/Loss</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{formatCurrency(portfolio.totalGainLoss)}</div>
        </div>

        <div style={{ background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)', borderRadius: '12px', padding: '20px', color: 'white', boxShadow: '0 4px 6px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', opacity: 0.9, marginBottom: '8px' }}>Overall Returns</div>
          <div style={{ fontSize: '28px', fontWeight: 'bold' }}>{formatPercentage(portfolio.overallReturnPercentage)}</div>
        </div>
      </div>

      {/* Investment Breakdown */}
      <div style={{ background: 'white', borderRadius: '12px', padding: '24px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
        <h2 style={{ marginBottom: '20px', fontSize: '20px' }}>Investment Breakdown</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px' }}>
          <div style={{ padding: '16px', background: '#f8fafc', borderRadius: '8px', border: '2px solid #e2e8f0' }}>
            <div style={{ fontSize: '12px', color: '#64748b', marginBottom: '8px', fontWeight: '600' }}>FIXED DEPOSITS</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', marginBottom: '4px' }}>{formatCurrency(portfolio.fdValue)}</div>
            <div style={{ fontSize: '14px', color: '#64748b' }}>{portfolio.totalFixedDeposits} Active</div>
          </div>

          <div style={{ padding: '16px', background: '#f8fafc', borderRadius: '8px', border: '2px solid #e2e8f0' }}>
            <div style={{ fontSize: '12px', color: '#64748b', marginBottom: '8px', fontWeight: '600' }}>RECURRING DEPOSITS</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', marginBottom: '4px' }}>{formatCurrency(portfolio.rdValue)}</div>
            <div style={{ fontSize: '14px', color: '#64748b' }}>{portfolio.totalRecurringDeposits} Active</div>
          </div>

          <div style={{ padding: '16px', background: '#f8fafc', borderRadius: '8px', border: '2px solid #e2e8f0' }}>
            <div style={{ fontSize: '12px', color: '#64748b', marginBottom: '8px', fontWeight: '600' }}>MUTUAL FUNDS</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', marginBottom: '4px' }}>{formatCurrency(portfolio.mutualFundValue)}</div>
            <div style={{ fontSize: '14px', color: '#64748b' }}>{portfolio.totalMutualFunds} Holdings</div>
          </div>

          <div style={{ padding: '16px', background: '#f8fafc', borderRadius: '8px', border: '2px solid #e2e8f0' }}>
            <div style={{ fontSize: '12px', color: '#64748b', marginBottom: '8px', fontWeight: '600' }}>SIP INVESTMENTS</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1e293b', marginBottom: '4px' }}>{formatCurrency(portfolio.sipValue)}</div>
            <div style={{ fontSize: '14px', color: '#64748b' }}>{portfolio.totalActiveSips} Active</div>
          </div>
        </div>
      </div>

      {/* Allocation Chart */}
      <div style={{ background: 'white', borderRadius: '12px', padding: '24px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
        <h2 style={{ marginBottom: '20px', fontSize: '20px' }}>Asset Allocation</h2>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {[
            { label: 'Fixed Deposits', value: portfolio.fdValue, color: '#3b82f6' },
            { label: 'Recurring Deposits', value: portfolio.rdValue, color: '#8b5cf6' },
            { label: 'Mutual Funds', value: portfolio.mutualFundValue, color: '#ec4899' },
            { label: 'SIP Investments', value: portfolio.sipValue, color: '#f59e0b' }
          ].map((item) => {
            const percentage = portfolio.totalInvestmentValue > 0 
              ? (item.value / portfolio.totalInvestmentValue) * 100 
              : 0;
            return (
              <div key={item.label}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span style={{ fontWeight: '500' }}>{item.label}</span>
                  <span style={{ color: '#64748b' }}>{formatCurrency(item.value)} ({percentage.toFixed(1)}%)</span>
                </div>
                <div style={{ background: '#e2e8f0', borderRadius: '4px', height: '8px', overflow: 'hidden' }}>
                  <div style={{ background: item.color, width: `${percentage}%`, height: '100%', transition: 'width 0.3s' }} />
                </div>
              </div>
            );
          })}
        </div>

        {portfolio.riskProfile && (
          <div style={{ marginTop: '24px', padding: '16px', background: '#fef3c7', borderRadius: '8px', border: '1px solid #fbbf24' }}>
            <strong>Risk Profile:</strong> {portfolio.riskProfile.replace('_', ' ')}
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div style={{ marginTop: '30px', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '16px' }}>
        <button
          onClick={() => window.location.href = '/fixed-deposits'}
          style={{ padding: '16px', background: '#3b82f6', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '16px', fontWeight: '500' }}
        >
          Create Fixed Deposit
        </button>
        <button
          onClick={() => window.location.href = '/recurring-deposits'}
          style={{ padding: '16px', background: '#8b5cf6', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '16px', fontWeight: '500' }}
        >
          Create Recurring Deposit
        </button>
        <button
          onClick={() => window.location.href = '/mutual-funds'}
          style={{ padding: '16px', background: '#ec4899', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '16px', fontWeight: '500' }}
        >
          Explore Mutual Funds
        </button>
        <button
          onClick={() => window.location.href = '/sip'}
          style={{ padding: '16px', background: '#f59e0b', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer', fontSize: '16px', fontWeight: '500' }}
        >
          Start SIP
        </button>
      </div>
    </div>
  );
};

export default PortfolioPage;
