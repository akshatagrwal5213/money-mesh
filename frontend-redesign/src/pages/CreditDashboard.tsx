import React, { useState, useEffect } from 'react';
import {
  calculateCreditScore,
  getCreditScoreHistory,
  fileDispute,
  getImprovementPlan,
  simulateScoreImprovement,
  CreditScoreResponse,
  CreditDisputeRequest,
  CreditImprovementPlan,
  Recommendation,
  getScoreColor,
  getCategoryLabel,
  getCategoryColor,
  calculateScorePercentage,
  getTrendIcon,
  getTrendLabel,
  getPriorityColor,
  formatPercentage
} from '../services/module11Service';

const CreditDashboard: React.FC = () => {
  const customerId = 1; // TODO: Get from auth context

  // State management
  const [creditScore, setCreditScore] = useState<CreditScoreResponse | null>(null);
  const [scoreHistory, setScoreHistory] = useState<CreditScoreResponse[]>([]);
  const [improvementPlan, setImprovementPlan] = useState<CreditImprovementPlan | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Dialog states
  const [showDisputeDialog, setShowDisputeDialog] = useState(false);
  const [showSimulatorDialog, setShowSimulatorDialog] = useState(false);
  const [historyMonths, setHistoryMonths] = useState(6);

  // Form states
  const [disputeForm, setDisputeForm] = useState<CreditDisputeRequest>({
    customerId,
    accountReference: '',
    reason: '',
    supportingDocuments: ''
  });
  const [targetScore, setTargetScore] = useState(750);

  // Fetch data on mount
  useEffect(() => {
    loadDashboardData();
  }, [historyMonths]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Parallel API calls
      const [scoreData, historyData, planData] = await Promise.all([
        calculateCreditScore(customerId),
        getCreditScoreHistory(customerId, historyMonths),
        getImprovementPlan(customerId).catch(() => null)
      ]);

      setCreditScore(scoreData);
      setScoreHistory(historyData);
      setImprovementPlan(planData);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load credit score data');
    } finally {
      setLoading(false);
    }
  };

  const handleRefreshScore = async () => {
    try {
      setLoading(true);
      const scoreData = await calculateCreditScore(customerId);
      setCreditScore(scoreData);
      setSuccess('Credit score refreshed successfully!');
      setTimeout(() => setSuccess(null), 3000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to refresh credit score');
    } finally {
      setLoading(false);
    }
  };

  const handleFileDispute = async () => {
    try {
      if (!disputeForm.accountReference || !disputeForm.reason) {
        setError('Please provide account reference and reason');
        return;
      }

      await fileDispute(disputeForm);
      setSuccess('Dispute filed successfully! We will review it within 30 days.');
      setShowDisputeDialog(false);
      setDisputeForm({
        customerId,
        accountReference: '',
        reason: '',
        supportingDocuments: ''
      });
      setTimeout(() => setSuccess(null), 5000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to file dispute');
    }
  };

  const handleSimulateScore = async () => {
    try {
      const simulation = await simulateScoreImprovement(customerId, targetScore);
      setImprovementPlan(simulation);
      setShowSimulatorDialog(false);
      setSuccess(`Simulation complete! You can reach ${targetScore} in ${simulation.estimatedTimeframe}`);
      setTimeout(() => setSuccess(null), 5000);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to simulate score');
    }
  };

  if (loading && !creditScore) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <div style={{ fontSize: '48px', marginBottom: '20px' }}>⏳</div>
        <h3>Loading Credit Score...</h3>
      </div>
    );
  }

  if (error && !creditScore) {
    return (
      <div style={{ padding: '40px', textAlign: 'center', color: '#ef4444' }}>
        <div style={{ fontSize: '48px', marginBottom: '20px' }}>❌</div>
        <h3>{error}</h3>
        <button
          onClick={loadDashboardData}
          style={{
            marginTop: '20px',
            padding: '10px 24px',
            background: '#3b82f6',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer'
          }}
        >
          Retry
        </button>
      </div>
    );
  }

  if (!creditScore) return null;

  return (
    <div style={{ padding: '24px', maxWidth: '1400px', margin: '0 auto' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h1 style={{ margin: 0, fontSize: '28px', fontWeight: 'bold' }}>📊 Credit Score Dashboard</h1>
          <p style={{ margin: '4px 0 0 0', color: '#666' }}>
            Last updated: {new Date(creditScore.calculationDate).toLocaleDateString()}
          </p>
        </div>
        <div style={{ display: 'flex', gap: '12px' }}>
          <button
            onClick={handleRefreshScore}
            disabled={loading}
            style={{
              padding: '10px 20px',
              background: '#3b82f6',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.6 : 1
            }}
          >
            {loading ? '⏳ Refreshing...' : '🔄 Refresh Score'}
          </button>
          <button
            onClick={() => setShowDisputeDialog(true)}
            style={{
              padding: '10px 20px',
              background: '#f97316',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              cursor: 'pointer'
            }}
          >
            ⚠️ File Dispute
          </button>
        </div>
      </div>

      {/* Success/Error Messages */}
      {success && (
        <div style={{
          padding: '16px',
          background: '#d1fae5',
          color: '#065f46',
          borderRadius: '8px',
          marginBottom: '20px',
          border: '1px solid #6ee7b7'
        }}>
          ✅ {success}
        </div>
      )}

      {error && (
        <div style={{
          padding: '16px',
          background: '#fee2e2',
          color: '#991b1b',
          borderRadius: '8px',
          marginBottom: '20px',
          border: '1px solid #fca5a5'
        }}>
          ❌ {error}
        </div>
      )}

      {/* Main Score Card */}
      <div style={{
        background: `linear-gradient(135deg, ${getScoreColor(creditScore.score)}22, ${getScoreColor(creditScore.score)}44)`,
        borderRadius: '16px',
        padding: '32px',
        marginBottom: '24px',
        border: `2px solid ${getScoreColor(creditScore.score)}`,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <div>
          <div style={{ fontSize: '72px', fontWeight: 'bold', color: getScoreColor(creditScore.score) }}>
            {creditScore.score}
          </div>
          <div style={{ fontSize: '24px', fontWeight: '600', marginTop: '8px' }}>
            {getCategoryLabel(creditScore.category)}
          </div>
          {creditScore.trend && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginTop: '12px', fontSize: '18px' }}>
              <span>{getTrendIcon(creditScore.trend)}</span>
              <span>{getTrendLabel(creditScore.trend)}</span>
              {creditScore.scoreChange && (
                <span style={{
                  fontWeight: 'bold',
                  color: creditScore.scoreChange > 0 ? '#22c55e' : creditScore.scoreChange < 0 ? '#ef4444' : '#666'
                }}>
                  {creditScore.scoreChange > 0 ? '+' : ''}{creditScore.scoreChange} points
                </span>
              )}
            </div>
          )}
        </div>

        {/* Score Gauge */}
        <div style={{ position: 'relative', width: '200px', height: '200px' }}>
          <svg viewBox="0 0 200 200" style={{ transform: 'rotate(-90deg)' }}>
            {/* Background circle */}
            <circle
              cx="100"
              cy="100"
              r="80"
              fill="none"
              stroke="#e5e7eb"
              strokeWidth="20"
            />
            {/* Progress circle */}
            <circle
              cx="100"
              cy="100"
              r="80"
              fill="none"
              stroke={getScoreColor(creditScore.score)}
              strokeWidth="20"
              strokeDasharray={`${calculateScorePercentage(creditScore.score) * 5.02} 502`}
              strokeLinecap="round"
            />
          </svg>
          <div style={{
            position: 'absolute',
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            textAlign: 'center'
          }}>
            <div style={{ fontSize: '32px', fontWeight: 'bold', color: getScoreColor(creditScore.score) }}>
              {Math.round(calculateScorePercentage(creditScore.score))}%
            </div>
            <div style={{ fontSize: '12px', color: '#666' }}>300-900 scale</div>
          </div>
        </div>
      </div>

      {/* 5 Factor Breakdown */}
      <div style={{ marginBottom: '24px' }}>
        <h2 style={{ fontSize: '22px', fontWeight: 'bold', marginBottom: '16px' }}>
          📈 Credit Score Factors (5-Factor Model)
        </h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '16px' }}>
          {/* Payment History */}
          <FactorCard
            title="Payment History"
            score={creditScore.paymentHistoryScore}
            maxScore={350}
            impact={35}
            details={`${creditScore.onTimePaymentPercentage.toFixed(1)}% on-time payments`}
            color="#3b82f6"
          />

          {/* Credit Utilization */}
          <FactorCard
            title="Credit Utilization"
            score={creditScore.creditUtilizationScore}
            maxScore={300}
            impact={30}
            details={`${formatPercentage(creditScore.creditUtilizationRatio * 100)} utilization ratio`}
            color="#8b5cf6"
          />

          {/* Credit History Length */}
          <FactorCard
            title="Credit History Length"
            score={creditScore.creditHistoryLengthScore}
            maxScore={150}
            impact={15}
            details={`${creditScore.oldestAccountAgeMonths} months oldest account`}
            color="#ec4899"
          />

          {/* Credit Mix */}
          <FactorCard
            title="Credit Mix"
            score={creditScore.creditMixScore}
            maxScore={100}
            impact={10}
            details={`${creditScore.numberOfActiveAccounts} active accounts`}
            color="#f59e0b"
          />

          {/* Recent Inquiries */}
          <FactorCard
            title="Recent Inquiries"
            score={creditScore.recentInquiriesScore}
            maxScore={100}
            impact={10}
            details={`${creditScore.hardInquiriesLast6Months} hard inquiries (6 months)`}
            color="#ef4444"
          />
        </div>
      </div>

      {/* Score History Chart */}
      <div style={{
        background: 'white',
        borderRadius: '12px',
        padding: '24px',
        marginBottom: '24px',
        boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>📉 Score History</h2>
          <select
            value={historyMonths}
            onChange={(e) => setHistoryMonths(Number(e.target.value))}
            style={{
              padding: '8px 12px',
              border: '1px solid #d1d5db',
              borderRadius: '6px',
              fontSize: '14px'
            }}
          >
            <option value={3}>Last 3 months</option>
            <option value={6}>Last 6 months</option>
            <option value={12}>Last 12 months</option>
          </select>
        </div>

        <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-end', height: '200px' }}>
          {scoreHistory.map((score, index) => {
            const height = calculateScorePercentage(score.score);
            return (
              <div key={index} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <div style={{
                  fontSize: '12px',
                  fontWeight: 'bold',
                  marginBottom: '8px',
                  color: getScoreColor(score.score)
                }}>
                  {score.score}
                </div>
                <div style={{
                  width: '100%',
                  height: `${height}%`,
                  background: getScoreColor(score.score),
                  borderRadius: '4px 4px 0 0',
                  transition: 'all 0.3s',
                  cursor: 'pointer',
                  position: 'relative'
                }}
                  title={`${score.score} - ${new Date(score.calculationDate).toLocaleDateString()}`}
                />
                <div style={{ fontSize: '10px', color: '#666', marginTop: '8px' }}>
                  {new Date(score.calculationDate).toLocaleDateString('en-IN', { month: 'short' })}
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Improvement Suggestions */}
      {creditScore.improvementSuggestions && creditScore.improvementSuggestions.length > 0 && (
        <div style={{
          background: 'white',
          borderRadius: '12px',
          padding: '24px',
          marginBottom: '24px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
        }}>
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '16px' }}>💡 Improvement Suggestions</h2>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {creditScore.improvementSuggestions.map((suggestion, index) => (
              <div key={index} style={{
                padding: '16px',
                background: '#f3f4f6',
                borderRadius: '8px',
                borderLeft: '4px solid #3b82f6'
              }}>
                <div style={{ display: 'flex', alignItems: 'start', gap: '12px' }}>
                  <span style={{ fontSize: '20px' }}>💡</span>
                  <div style={{ flex: 1 }}>
                    <p style={{ margin: 0, lineHeight: '1.6' }}>{suggestion}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Improvement Plan */}
      {improvementPlan && (
        <div style={{
          background: 'white',
          borderRadius: '12px',
          padding: '24px',
          marginBottom: '24px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>🎯 Improvement Plan</h2>
            <button
              onClick={() => setShowSimulatorDialog(true)}
              style={{
                padding: '8px 16px',
                background: '#8b5cf6',
                color: 'white',
                border: 'none',
                borderRadius: '6px',
                cursor: 'pointer',
                fontSize: '14px'
              }}
            >
              🔮 Simulate Score
            </button>
          </div>

          <div style={{
            background: 'linear-gradient(135deg, #8b5cf622, #8b5cf644)',
            borderRadius: '8px',
            padding: '20px',
            marginBottom: '20px'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-around', textAlign: 'center' }}>
              <div>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>Current Score</div>
                <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#3b82f6' }}>
                  {improvementPlan.currentScore}
                </div>
              </div>
              <div style={{ fontSize: '32px', color: '#8b5cf6' }}>→</div>
              <div>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>Target Score</div>
                <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#22c55e' }}>
                  {improvementPlan.targetScore}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>Potential Gain</div>
                <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#f59e0b' }}>
                  +{improvementPlan.potentialIncrease}
                </div>
              </div>
              <div>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>Timeframe</div>
                <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#666' }}>
                  {improvementPlan.estimatedTimeframe}
                </div>
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {improvementPlan.recommendations.map((rec, index) => (
              <RecommendationCard key={index} recommendation={rec} />
            ))}
          </div>
        </div>
      )}

      {/* Dispute Dialog */}
      {showDisputeDialog && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            borderRadius: '12px',
            padding: '32px',
            maxWidth: '500px',
            width: '90%',
            maxHeight: '80vh',
            overflow: 'auto'
          }}>
            <h2 style={{ marginTop: 0, fontSize: '24px', fontWeight: 'bold' }}>⚠️ File Credit Dispute</h2>
            <p style={{ color: '#666', marginBottom: '24px' }}>
              If you find incorrect information on your credit report, file a dispute and we'll investigate.
            </p>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Account Reference *
              </label>
              <input
                type="text"
                value={disputeForm.accountReference}
                onChange={(e) => setDisputeForm({ ...disputeForm, accountReference: e.target.value })}
                placeholder="e.g., Credit Card ending 1234"
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px'
                }}
              />
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Reason for Dispute *
              </label>
              <textarea
                value={disputeForm.reason}
                onChange={(e) => setDisputeForm({ ...disputeForm, reason: e.target.value })}
                placeholder="Describe the issue in detail..."
                rows={4}
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px',
                  resize: 'vertical'
                }}
              />
            </div>

            <div style={{ marginBottom: '24px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Supporting Documents (Optional)
              </label>
              <input
                type="text"
                value={disputeForm.supportingDocuments}
                onChange={(e) => setDisputeForm({ ...disputeForm, supportingDocuments: e.target.value })}
                placeholder="Document URLs or references"
                style={{
                  width: '100%',
                  padding: '10px',
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  fontSize: '14px'
                }}
              />
            </div>

            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
              <button
                onClick={() => setShowDisputeDialog(false)}
                style={{
                  padding: '10px 20px',
                  background: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer'
                }}
              >
                Cancel
              </button>
              <button
                onClick={handleFileDispute}
                style={{
                  padding: '10px 20px',
                  background: '#ef4444',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer'
                }}
              >
                Submit Dispute
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Simulator Dialog */}
      {showSimulatorDialog && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'rgba(0,0,0,0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000
        }}>
          <div style={{
            background: 'white',
            borderRadius: '12px',
            padding: '32px',
            maxWidth: '400px',
            width: '90%'
          }}>
            <h2 style={{ marginTop: 0, fontSize: '24px', fontWeight: 'bold' }}>🔮 Score Simulator</h2>
            <p style={{ color: '#666', marginBottom: '24px' }}>
              See what it would take to reach your target credit score.
            </p>

            <div style={{ marginBottom: '24px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Target Score: {targetScore}
              </label>
              <input
                type="range"
                min={creditScore.score + 10}
                max={900}
                step={10}
                value={targetScore}
                onChange={(e) => setTargetScore(Number(e.target.value))}
                style={{ width: '100%' }}
              />
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '12px', color: '#666', marginTop: '4px' }}>
                <span>{creditScore.score + 10}</span>
                <span>900</span>
              </div>
            </div>

            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
              <button
                onClick={() => setShowSimulatorDialog(false)}
                style={{
                  padding: '10px 20px',
                  background: '#e5e7eb',
                  color: '#374151',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer'
                }}
              >
                Cancel
              </button>
              <button
                onClick={handleSimulateScore}
                style={{
                  padding: '10px 20px',
                  background: '#8b5cf6',
                  color: 'white',
                  border: 'none',
                  borderRadius: '6px',
                  cursor: 'pointer'
                }}
              >
                Simulate
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

// Factor Card Component
const FactorCard: React.FC<{
  title: string;
  score: number;
  maxScore: number;
  impact: number;
  details: string;
  color: string;
}> = ({ title, score, maxScore, impact, details, color }) => {
  const percentage = (score / maxScore) * 100;

  return (
    <div style={{
      background: 'white',
      borderRadius: '12px',
      padding: '20px',
      boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
      border: `2px solid ${color}22`
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
        <h3 style={{ margin: 0, fontSize: '16px', fontWeight: 'bold' }}>{title}</h3>
        <span style={{
          background: `${color}22`,
          color,
          padding: '4px 12px',
          borderRadius: '12px',
          fontSize: '14px',
          fontWeight: '600'
        }}>
          {impact}% impact
        </span>
      </div>

      <div style={{ marginBottom: '12px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
          <span style={{ fontSize: '24px', fontWeight: 'bold', color }}>{score}</span>
          <span style={{ fontSize: '14px', color: '#666' }}>/ {maxScore}</span>
        </div>
        <div style={{
          width: '100%',
          height: '8px',
          background: '#e5e7eb',
          borderRadius: '4px',
          overflow: 'hidden'
        }}>
          <div style={{
            width: `${percentage}%`,
            height: '100%',
            background: color,
            transition: 'width 0.3s'
          }} />
        </div>
      </div>

      <p style={{ margin: 0, fontSize: '13px', color: '#666' }}>{details}</p>
    </div>
  );
};

// Recommendation Card Component
const RecommendationCard: React.FC<{ recommendation: Recommendation }> = ({ recommendation }) => {
  const priorityColor = getPriorityColor(recommendation.priority);

  return (
    <div style={{
      background: 'white',
      border: `2px solid ${priorityColor}33`,
      borderRadius: '8px',
      padding: '20px'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
        <h3 style={{ margin: 0, fontSize: '18px', fontWeight: 'bold' }}>{recommendation.title}</h3>
        <span style={{
          background: priorityColor,
          color: 'white',
          padding: '4px 12px',
          borderRadius: '12px',
          fontSize: '12px',
          fontWeight: '600'
        }}>
          {recommendation.priority}
        </span>
      </div>

      <p style={{ margin: '0 0 12px 0', color: '#666', lineHeight: '1.6' }}>
        {recommendation.description}
      </p>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
        <div>
          <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>Expected Impact</div>
          <div style={{ fontSize: '18px', fontWeight: 'bold', color: priorityColor }}>
            +{recommendation.estimatedImpact} points
          </div>
        </div>
        <div>
          <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>Timeframe</div>
          <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#666' }}>
            {recommendation.timeframe}
          </div>
        </div>
      </div>

      <div style={{
        background: '#f3f4f6',
        borderRadius: '6px',
        padding: '12px'
      }}>
        <div style={{ fontSize: '12px', fontWeight: '600', marginBottom: '8px', color: '#374151' }}>
          📋 Action Steps:
        </div>
        <p style={{ margin: 0, fontSize: '13px', color: '#666', lineHeight: '1.5' }}>
          {recommendation.actionSteps}
        </p>
      </div>
    </div>
  );
};

export default CreditDashboard;
