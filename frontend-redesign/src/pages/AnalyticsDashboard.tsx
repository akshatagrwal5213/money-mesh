import React, { useState, useEffect } from 'react';
import { 
  getTransactionAnalytics, 
  getAccountSummary,
  AnalyticsResponse 
} from '../services/module7Service';
import './AnalyticsDashboard.css';

const AnalyticsDashboard: React.FC = () => {
  const [analytics, setAnalytics] = useState<AnalyticsResponse | null>(null);
  const [accountSummary, setAccountSummary] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [dateRange, setDateRange] = useState({
    startDate: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0]
  });

  useEffect(() => {
    loadAnalytics();
    loadAccountSummary();
  }, [dateRange]);

  const loadAnalytics = async () => {
    try {
      setLoading(true);
      const data = await getTransactionAnalytics({
        startDate: dateRange.startDate,
        endDate: dateRange.endDate,
        groupBy: 'DAY'
      });
      setAnalytics(data);
    } catch (error) {
      console.error('Error loading analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAccountSummary = async () => {
    try {
      const data = await getAccountSummary();
      setAccountSummary(data);
    } catch (error) {
      console.error('Error loading account summary:', error);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount);
  };

  const getTopCategories = () => {
    if (!analytics || !analytics.categoryBreakdown) return [];
    
    return Object.entries(analytics.categoryBreakdown)
      .map(([category, amount]) => ({ category, amount }))
      .sort((a, b) => b.amount - a.amount)
      .slice(0, 5);
  };

  if (loading && !analytics) {
    return (
      <div className="analytics-loading">
        <div className="spinner"></div>
        <p>Loading analytics...</p>
      </div>
    );
  }

  return (
    <div className="analytics-dashboard">
      <div className="analytics-header">
        <h1>📊 Analytics Dashboard</h1>
        <div className="date-range-selector">
          <input
            type="date"
            value={dateRange.startDate}
            onChange={(e) => setDateRange({ ...dateRange, startDate: e.target.value })}
          />
          <span>to</span>
          <input
            type="date"
            value={dateRange.endDate}
            onChange={(e) => setDateRange({ ...dateRange, endDate: e.target.value })}
          />
        </div>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="summary-card income">
          <div className="card-icon">💰</div>
          <div className="card-content">
            <h3>Total Income</h3>
            <p className="amount">{formatCurrency(analytics?.totalIncome || 0)}</p>
          </div>
        </div>

        <div className="summary-card expenses">
          <div className="card-icon">💸</div>
          <div className="card-content">
            <h3>Total Expenses</h3>
            <p className="amount">{formatCurrency(analytics?.totalExpenses || 0)}</p>
          </div>
        </div>

        <div className="summary-card savings">
          <div className="card-icon">🏦</div>
          <div className="card-content">
            <h3>Net Savings</h3>
            <p className="amount">{formatCurrency(analytics?.netSavings || 0)}</p>
          </div>
        </div>

        <div className="summary-card transactions">
          <div className="card-icon">📈</div>
          <div className="card-content">
            <h3>Transactions</h3>
            <p className="amount">{analytics?.transactionCount || 0}</p>
            <small>Avg: {formatCurrency(analytics?.averageTransactionAmount || 0)}</small>
          </div>
        </div>
      </div>

      <div className="analytics-grid">
        {/* Account Summary */}
        {accountSummary && (
          <div className="analytics-card account-summary-card">
            <h2>🏦 Account Summary</h2>
            <div className="account-summary">
              <div className="summary-item">
                <span>Total Accounts:</span>
                <strong>{accountSummary.totalAccounts}</strong>
              </div>
              <div className="summary-item">
                <span>Total Balance:</span>
                <strong>{formatCurrency(accountSummary.totalBalance)}</strong>
              </div>
              <div className="accounts-list">
                {accountSummary.accounts?.map((acc: any, idx: number) => (
                  <div key={idx} className="account-item">
                    <span className="account-number">{acc.accountNumber}</span>
                    <span className="account-type">{acc.accountType}</span>
                    <span className="account-balance">{formatCurrency(acc.balance)}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Top Categories */}
        <div className="analytics-card categories-card">
          <h2>📊 Top Spending Categories</h2>
          {analytics?.topCategory && (
            <div className="top-category">
              <div className="category-header">
                <span className="category-name">{analytics.topCategory}</span>
                <span className="category-amount">{formatCurrency(analytics.topCategoryAmount)}</span>
              </div>
            </div>
          )}
          <div className="categories-list">
            {getTopCategories().map((cat, idx) => {
              const total = analytics?.totalExpenses || 1;
              const percentage = (cat.amount / total) * 100;
              return (
                <div key={idx} className="category-row">
                  <div className="category-info">
                    <span className="category-name">{cat.category}</span>
                    <span className="category-amount">{formatCurrency(cat.amount)}</span>
                  </div>
                  <div className="category-bar">
                    <div 
                      className="category-progress" 
                      style={{ width: `${percentage}%` }}
                    ></div>
                  </div>
                  <span className="category-percentage">{percentage.toFixed(1)}%</span>
                </div>
              );
            })}
          </div>
        </div>

        {/* Daily Transactions Chart */}
        <div className="analytics-card transactions-chart-card">
          <h2>📈 Daily Transaction Trends</h2>
          <div className="transactions-chart">
            {analytics?.dailyTransactions?.map((day, idx) => {
              const maxAmount = Math.max(...(analytics.dailyTransactions?.map(d => d.amount) || [1]));
              const height = (day.amount / maxAmount) * 100;
              return (
                <div key={idx} className="chart-bar-container">
                  <div className="chart-bar" style={{ height: `${height}%` }}>
                    <div className="bar-tooltip">
                      <div>{new Date(day.date).toLocaleDateString('en-IN', { month: 'short', day: 'numeric' })}</div>
                      <div>{formatCurrency(day.amount)}</div>
                      <div>{day.count} txns</div>
                    </div>
                  </div>
                  <div className="chart-label">
                    {new Date(day.date).getDate()}
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Insights */}
        <div className="analytics-card insights-card">
          <h2>💡 Insights</h2>
          <div className="insights-list">
            {analytics && (
              <>
                {analytics.netSavings > 0 ? (
                  <div className="insight success">
                    <span className="insight-icon">✅</span>
                    <p>Great job! You saved {formatCurrency(analytics.netSavings)} this period.</p>
                  </div>
                ) : (
                  <div className="insight warning">
                    <span className="insight-icon">⚠️</span>
                    <p>Your expenses exceeded income by {formatCurrency(Math.abs(analytics.netSavings))}.</p>
                  </div>
                )}
                
                <div className="insight info">
                  <span className="insight-icon">📊</span>
                  <p>Your average transaction amount is {formatCurrency(analytics.averageTransactionAmount)}.</p>
                </div>

                {analytics.topCategory && (
                  <div className="insight info">
                    <span className="insight-icon">🎯</span>
                    <p>You spent the most on {analytics.topCategory} ({formatCurrency(analytics.topCategoryAmount)}).</p>
                  </div>
                )}

                <div className="insight info">
                  <span className="insight-icon">🔄</span>
                  <p>You made {analytics.transactionCount} transactions during this period.</p>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
