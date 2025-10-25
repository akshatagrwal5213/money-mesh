import React, { useEffect, useState } from 'react';
import { 
  getWealthDashboard, 
  WealthDashboard,
  formatCurrency,
  formatPercentage,
  formatScore,
  getScoreColor,
  getCategoryColor,
  getCategoryLabel,
  getRiskProfileLabel,
  getPriorityLabel,
  getPriorityColor
} from '../services/module10Service';
import './WealthDashboardPage.css';

const WealthDashboardPage: React.FC = () => {
  const [dashboard, setDashboard] = useState<WealthDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      setLoading(true);
      const data = await getWealthDashboard();
      setDashboard(data);
      setError(null);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load wealth dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="wealth-dashboard-container">
        <div className="loading-spinner">Loading your wealth dashboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="wealth-dashboard-container">
        <div className="error-message">{error}</div>
      </div>
    );
  }

  if (!dashboard) {
    return null;
  }

  const { profile, portfolio, health, recommendations } = dashboard;

  return (
    <div className="wealth-dashboard-container">
      <div className="wealth-dashboard-header">
        <h1>💎 Wealth Dashboard</h1>
        <p>Comprehensive view of your financial health and portfolio</p>
      </div>

      {/* Profile Summary */}
      {profile ? (
        <div className="profile-summary-section">
          <h2>📊 Your Wealth Profile</h2>
          <div className="profile-cards">
            <div className="profile-card">
              <div className="profile-icon">🎯</div>
              <div className="profile-content">
                <div className="profile-label">Risk Profile</div>
                <div className="profile-value">{getRiskProfileLabel(profile.riskProfile)}</div>
              </div>
            </div>
            <div className="profile-card">
              <div className="profile-icon">💰</div>
              <div className="profile-content">
                <div className="profile-label">Monthly Income</div>
                <div className="profile-value">{formatCurrency(profile.monthlyIncome)}</div>
              </div>
            </div>
            <div className="profile-card">
              <div className="profile-icon">📉</div>
              <div className="profile-content">
                <div className="profile-label">Monthly Expenses</div>
                <div className="profile-value">{formatCurrency(profile.monthlyExpenses)}</div>
              </div>
            </div>
            <div className="profile-card">
              <div className="profile-icon">🛡️</div>
              <div className="profile-content">
                <div className="profile-label">Emergency Fund</div>
                <div className="profile-value">{profile.emergencyFundMonths} months</div>
              </div>
            </div>
            <div className="profile-card">
              <div className="profile-icon">🏖️</div>
              <div className="profile-content">
                <div className="profile-label">Retirement Age</div>
                <div className="profile-value">{profile.retirementAge} years</div>
              </div>
            </div>
            <div className="profile-card">
              <div className="profile-icon">📊</div>
              <div className="profile-content">
                <div className="profile-label">Savings Rate</div>
                <div className="profile-value">
                  {formatPercentage(((profile.monthlyIncome - profile.monthlyExpenses) / profile.monthlyIncome) * 100)}
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <div className="warning-message">
          {dashboard.profileMessage || 'Please create a wealth profile to get started'}
        </div>
      )}

      {/* Net Worth & Asset Allocation */}
      {portfolio && (
        <div className="portfolio-section">
          <h2>💼 Portfolio Analysis</h2>
          <div className="net-worth-card">
            <div className="net-worth-main">
              <div className="net-worth-label">Total Net Worth</div>
              <div className="net-worth-value">{formatCurrency(portfolio.totalNetWorth)}</div>
            </div>
            <div className="net-worth-metrics">
              <div className="metric">
                <div className="metric-icon">🎨</div>
                <div>
                  <div className="metric-label">Diversification</div>
                  <div className="metric-value" style={{ color: getScoreColor(portfolio.diversificationScore) }}>
                    {formatScore(portfolio.diversificationScore)}/100
                  </div>
                </div>
              </div>
              <div className="metric">
                <div className="metric-icon">⚡</div>
                <div>
                  <div className="metric-label">Risk Score</div>
                  <div className="metric-value" style={{ color: getScoreColor(100 - portfolio.riskScore) }}>
                    {formatScore(portfolio.riskScore)}/100
                  </div>
                </div>
              </div>
              <div className="metric">
                <div className="metric-icon">⚖️</div>
                <div>
                  <div className="metric-label">Needs Rebalancing</div>
                  <div className="metric-value" style={{ color: portfolio.needsRebalancing ? '#ef4444' : '#10b981' }}>
                    {portfolio.needsRebalancing ? 'Yes' : 'No'}
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Asset Allocation */}
          <div className="asset-allocation-grid">
            <div className="asset-card equity">
              <div className="asset-icon">📈</div>
              <div className="asset-name">Equity</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.equityValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.equityPercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.equityPercentage)}</div>
              )}
            </div>

            <div className="asset-card debt">
              <div className="asset-icon">📊</div>
              <div className="asset-name">Debt</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.debtValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.debtPercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.debtPercentage)}</div>
              )}
            </div>

            <div className="asset-card gold">
              <div className="asset-icon">🪙</div>
              <div className="asset-name">Gold</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.goldValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.goldPercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.goldPercentage)}</div>
              )}
            </div>

            <div className="asset-card cash">
              <div className="asset-icon">💵</div>
              <div className="asset-name">Cash</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.cashValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.cashPercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.cashPercentage)}</div>
              )}
            </div>

            <div className="asset-card real-estate">
              <div className="asset-icon">🏠</div>
              <div className="asset-name">Real Estate</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.realEstateValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.realEstatePercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.realEstatePercentage)}</div>
              )}
            </div>

            <div className="asset-card alternative">
              <div className="asset-icon">🌐</div>
              <div className="asset-name">Alternative</div>
              <div className="asset-amount">{formatCurrency(portfolio.currentAllocation.alternativeValue)}</div>
              <div className="asset-percentage">{formatPercentage(portfolio.currentAllocation.alternativePercentage)}</div>
              {portfolio.targetAllocation && (
                <div className="asset-target">Target: {formatPercentage(portfolio.targetAllocation.alternativePercentage)}</div>
              )}
            </div>
          </div>

          {/* Rebalancing Recommendations */}
          {portfolio.needsRebalancing && portfolio.rebalancingRecommendations.length > 0 && (
            <div className="rebalancing-section">
              <h3>⚖️ Rebalancing Recommendations</h3>
              <div className="rebalancing-list">
                {portfolio.rebalancingRecommendations.map((rec, index) => (
                  <div key={index} className="rebalancing-card">
                    <div className="rebalancing-action" style={{ 
                      backgroundColor: rec.action === 'INCREASE' ? '#dcfce7' : '#fee2e2',
                      color: rec.action === 'INCREASE' ? '#166534' : '#991b1b'
                    }}>
                      {rec.action === 'INCREASE' ? '↑' : '↓'} {rec.action}
                    </div>
                    <div className="rebalancing-details">
                      <div className="rebalancing-asset">{rec.assetClass}</div>
                      <div className="rebalancing-info">
                        Current: {formatPercentage(rec.currentPercentage)} → Target: {formatPercentage(rec.targetPercentage)}
                      </div>
                      <div className="rebalancing-amount">Amount: {formatCurrency(rec.amountToAdjust)}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Financial Health Score */}
      {health && (
        <div className="health-section">
          <h2>❤️ Financial Health Score</h2>
          <div className="health-overall-card">
            <div className="health-score-display">
              <div 
                className="health-score-circle"
                style={{ 
                  background: `conic-gradient(${getCategoryColor(health.category)} ${health.overallScore * 3.6}deg, #e5e7eb 0deg)`
                }}
              >
                <div className="health-score-inner">
                  <div className="health-score-number">{formatScore(health.overallScore)}</div>
                  <div className="health-score-category" style={{ color: getCategoryColor(health.category) }}>
                    {getCategoryLabel(health.category)}
                  </div>
                </div>
              </div>
            </div>
            {health.previousOverallScore !== undefined && health.scoreImprovement !== undefined && (
              <div className="health-improvement">
                <div className="improvement-label">Change from last assessment</div>
                <div 
                  className="improvement-value"
                  style={{ color: health.scoreImprovement >= 0 ? '#10b981' : '#ef4444' }}
                >
                  {health.scoreImprovement >= 0 ? '+' : ''}{health.scoreImprovement.toFixed(1)} points
                </div>
              </div>
            )}
          </div>

          {/* Component Scores */}
          <div className="health-components-grid">
            <div className="health-component">
              <div className="component-icon">💰</div>
              <div className="component-name">Savings</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.savingsScore}%`, 
                    backgroundColor: getScoreColor(health.savingsScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.savingsScore)}/100</div>
              <div className="component-detail">Rate: {formatPercentage(health.savingsRate)}</div>
            </div>

            <div className="health-component">
              <div className="component-icon">💳</div>
              <div className="component-name">Debt</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.debtScore}%`, 
                    backgroundColor: getScoreColor(health.debtScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.debtScore)}/100</div>
              <div className="component-detail">DTI: {formatPercentage(health.debtToIncomeRatio)}</div>
            </div>

            <div className="health-component">
              <div className="component-icon">🛡️</div>
              <div className="component-name">Emergency Fund</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.emergencyFundScore}%`, 
                    backgroundColor: getScoreColor(health.emergencyFundScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.emergencyFundScore)}/100</div>
              <div className="component-detail">Coverage: {health.emergencyFundMonths} months</div>
            </div>

            <div className="health-component">
              <div className="component-icon">📊</div>
              <div className="component-name">Investment</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.investmentScore}%`, 
                    backgroundColor: getScoreColor(health.investmentScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.investmentScore)}/100</div>
              <div className="component-detail">Diversity: {formatScore(health.investmentDiversity)}</div>
            </div>

            <div className="health-component">
              <div className="component-icon">🏥</div>
              <div className="component-name">Insurance</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.insuranceScore}%`, 
                    backgroundColor: getScoreColor(health.insuranceScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.insuranceScore)}/100</div>
            </div>

            <div className="health-component">
              <div className="component-icon">🏖️</div>
              <div className="component-name">Retirement</div>
              <div className="component-bar">
                <div 
                  className="component-fill" 
                  style={{ 
                    width: `${health.retirementScore}%`, 
                    backgroundColor: getScoreColor(health.retirementScore) 
                  }}
                />
              </div>
              <div className="component-score">{formatScore(health.retirementScore)}/100</div>
            </div>
          </div>
        </div>
      )}

      {/* Investment Recommendations */}
      {recommendations && recommendations.length > 0 && (
        <div className="recommendations-section">
          <h2>💡 Investment Recommendations</h2>
          <div className="recommendations-grid">
            {recommendations.slice(0, 6).map((rec) => (
              <div key={rec.id} className="recommendation-card">
                <div className="recommendation-header">
                  <div 
                    className="recommendation-priority" 
                    style={{ 
                      backgroundColor: getPriorityColor(rec.priority),
                      color: '#fff'
                    }}
                  >
                    {getPriorityLabel(rec.priority)}
                  </div>
                  <div className="recommendation-impact">
                    Impact: +{rec.potentialImpact} points
                  </div>
                </div>
                <div className="recommendation-title">{rec.title}</div>
                <div className="recommendation-description">{rec.description}</div>
                {rec.suggestedAmount && (
                  <div className="recommendation-amount">
                    💰 {formatCurrency(rec.suggestedAmount)}
                  </div>
                )}
                <div className="recommendation-type">{rec.recommendationType}</div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default WealthDashboardPage;
