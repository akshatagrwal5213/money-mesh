import React, { useState, useEffect } from 'react';
import {
  checkLoanEligibility,
  getPrequalifiedOffers,
  calculateCreditScore,
  getImprovementPlan,
  LoanType,
  LoanEligibilityRequest,
  LoanEligibilityResponse,
  CreditScoreResponse,
  CreditImprovementPlan,
  getLoanTypeLabel,
  formatCurrency,
  formatPercentage,
  getEligibilityStatusColor,
  getEligibilityStatusLabel,
  calculateEMI,
  getPriorityColor
} from '../services/module11Service';

const LoanEligibilityPage: React.FC = () => {
  const customerId = 1; // TODO: Get from auth context

  // State management
  const [creditScore, setCreditScore] = useState<CreditScoreResponse | null>(null);
  const [eligibilityResult, setEligibilityResult] = useState<LoanEligibilityResponse | null>(null);
  const [prequalifiedOffers, setPrequalifiedOffers] = useState<LoanEligibilityResponse[]>([]);
  const [improvementPlan, setImprovementPlan] = useState<CreditImprovementPlan | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Form state
  const [formData, setFormData] = useState<LoanEligibilityRequest>({
    customerId,
    loanType: LoanType.PERSONAL,
    requestedAmount: 100000,
    requestedTenureMonths: 12,
    monthlyIncome: 50000
  });

  // EMI Calculator state
  const [emiAmount, setEmiAmount] = useState<number | null>(null);
  const [showEmiCalculator, setShowEmiCalculator] = useState(false);

  // Load initial data
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [scoreData, offersData] = await Promise.all([
        calculateCreditScore(customerId),
        getPrequalifiedOffers(customerId).catch(() => [])
      ]);
      setCreditScore(scoreData);
      setPrequalifiedOffers(offersData);
    } catch (err: any) {
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckEligibility = async () => {
    try {
      setLoading(true);
      setError(null);
      setSuccess(null);

      const result = await checkLoanEligibility(formData);
      setEligibilityResult(result);

      if (result.eligible) {
        setSuccess(`✅ You are ${getEligibilityStatusLabel(result.eligibilityStatus).toLowerCase()} for this loan!`);
        
        // Calculate EMI
        const emi = calculateEMI(
          formData.requestedAmount,
          result.interestRate,
          result.recommendedTenureMonths
        );
        setEmiAmount(emi);
      } else {
        // Fetch improvement plan if not eligible
        const plan = await getImprovementPlan(customerId);
        setImprovementPlan(plan);
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to check eligibility');
    } finally {
      setLoading(false);
    }
  };

  const handleApplyLoan = (offer: LoanEligibilityResponse) => {
    setFormData({
      customerId,
      loanType: offer.loanType,
      requestedAmount: offer.maxEligibleAmount,
      requestedTenureMonths: offer.recommendedTenureMonths,
      monthlyIncome: formData.monthlyIncome
    });
    setEligibilityResult(offer);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const calculateEMIForCurrentForm = () => {
    if (!eligibilityResult) return;
    const emi = calculateEMI(
      formData.requestedAmount,
      eligibilityResult.interestRate,
      formData.requestedTenureMonths || eligibilityResult.recommendedTenureMonths
    );
    setEmiAmount(emi);
  };

  useEffect(() => {
    if (eligibilityResult) {
      calculateEMIForCurrentForm();
    }
  }, [formData.requestedAmount, formData.requestedTenureMonths]);

  return (
    <div style={{ padding: '24px', maxWidth: '1400px', margin: '0 auto' }}>
      {/* Header */}
      <div style={{ marginBottom: '24px' }}>
        <h1 style={{ margin: 0, fontSize: '28px', fontWeight: 'bold' }}>🏦 Loan Eligibility Checker</h1>
        <p style={{ margin: '4px 0 0 0', color: '#666' }}>
          Check your eligibility for various loan types instantly
        </p>
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
          {success}
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

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
        {/* Left Column - Eligibility Form */}
        <div>
          {/* Credit Score Card */}
          {creditScore && (
            <div style={{
              background: `linear-gradient(135deg, #3b82f622, #3b82f644)`,
              borderRadius: '12px',
              padding: '20px',
              marginBottom: '24px',
              border: '2px solid #3b82f6'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>Your Credit Score</div>
                  <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#3b82f6' }}>
                    {creditScore.score}
                  </div>
                </div>
                <div style={{
                  background: 'white',
                  padding: '12px 20px',
                  borderRadius: '8px',
                  textAlign: 'center'
                }}>
                  <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>Category</div>
                  <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#3b82f6' }}>
                    {creditScore.category}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Eligibility Check Form */}
          <div style={{
            background: 'white',
            borderRadius: '12px',
            padding: '24px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <h2 style={{ marginTop: 0, fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>
              📝 Check Eligibility
            </h2>

            {/* Loan Type */}
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Loan Type
              </label>
              <select
                value={formData.loanType}
                onChange={(e) => setFormData({ ...formData, loanType: e.target.value as LoanType })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  fontSize: '14px'
                }}
              >
                {Object.values(LoanType).map(type => (
                  <option key={type} value={type}>{getLoanTypeLabel(type)}</option>
                ))}
              </select>
            </div>

            {/* Requested Amount */}
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Requested Amount
              </label>
              <input
                type="number"
                value={formData.requestedAmount}
                onChange={(e) => setFormData({ ...formData, requestedAmount: Number(e.target.value) })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  fontSize: '14px'
                }}
                step={10000}
                min={10000}
              />
              <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                {formatCurrency(formData.requestedAmount)}
              </div>
            </div>

            {/* Tenure */}
            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Tenure (months)
              </label>
              <input
                type="number"
                value={formData.requestedTenureMonths || ''}
                onChange={(e) => setFormData({ ...formData, requestedTenureMonths: Number(e.target.value) })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  fontSize: '14px'
                }}
                step={6}
                min={6}
                max={360}
              />
              <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                {formData.requestedTenureMonths ? `${Math.floor(formData.requestedTenureMonths / 12)} years ${formData.requestedTenureMonths % 12} months` : ''}
              </div>
            </div>

            {/* Monthly Income */}
            <div style={{ marginBottom: '24px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: '600' }}>
                Monthly Income
              </label>
              <input
                type="number"
                value={formData.monthlyIncome || ''}
                onChange={(e) => setFormData({ ...formData, monthlyIncome: Number(e.target.value) })}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #d1d5db',
                  borderRadius: '8px',
                  fontSize: '14px'
                }}
                step={5000}
                min={10000}
              />
              <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                {formData.monthlyIncome ? formatCurrency(formData.monthlyIncome) : ''}
              </div>
            </div>

            <button
              onClick={handleCheckEligibility}
              disabled={loading}
              style={{
                width: '100%',
                padding: '14px',
                background: loading ? '#9ca3af' : '#3b82f6',
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '16px',
                fontWeight: '600',
                cursor: loading ? 'not-allowed' : 'pointer'
              }}
            >
              {loading ? '⏳ Checking...' : '🔍 Check Eligibility'}
            </button>
          </div>

          {/* EMI Calculator */}
          {eligibilityResult && (
            <div style={{
              background: 'white',
              borderRadius: '12px',
              padding: '24px',
              marginTop: '24px',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
            }}>
              <h3 style={{ marginTop: 0, fontSize: '18px', fontWeight: 'bold', marginBottom: '16px' }}>
                💰 EMI Calculator
              </h3>

              <div style={{
                background: '#f3f4f6',
                borderRadius: '8px',
                padding: '20px',
                textAlign: 'center'
              }}>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>
                  Estimated Monthly EMI
                </div>
                <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#3b82f6' }}>
                  {emiAmount ? formatCurrency(emiAmount) : '—'}
                </div>
              </div>

              <div style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '16px',
                marginTop: '16px'
              }}>
                <div>
                  <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>
                    Total Interest
                  </div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#f59e0b' }}>
                    {eligibilityResult.totalInterestPayable ? formatCurrency(eligibilityResult.totalInterestPayable) : '—'}
                  </div>
                </div>
                <div>
                  <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>
                    Total Payable
                  </div>
                  <div style={{ fontSize: '18px', fontWeight: 'bold', color: '#ef4444' }}>
                    {eligibilityResult.totalAmountPayable ? formatCurrency(eligibilityResult.totalAmountPayable) : '—'}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Right Column - Results */}
        <div>
          {/* Eligibility Result */}
          {eligibilityResult && (
            <div style={{
              background: 'white',
              borderRadius: '12px',
              padding: '24px',
              marginBottom: '24px',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
              border: `3px solid ${getEligibilityStatusColor(eligibilityResult.eligibilityStatus)}`
            }}>
              <div style={{ textAlign: 'center', marginBottom: '20px' }}>
                <div style={{ fontSize: '48px', marginBottom: '12px' }}>
                  {eligibilityResult.eligible ? '✅' : '❌'}
                </div>
                <h2 style={{
                  margin: 0,
                  fontSize: '24px',
                  fontWeight: 'bold',
                  color: getEligibilityStatusColor(eligibilityResult.eligibilityStatus)
                }}>
                  {getEligibilityStatusLabel(eligibilityResult.eligibilityStatus)}
                </h2>
                <p style={{ margin: '8px 0 0 0', fontSize: '18px', fontWeight: '600' }}>
                  {getLoanTypeLabel(eligibilityResult.loanType)}
                </p>
              </div>

              {/* Key Metrics */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: '1fr 1fr',
                gap: '16px',
                marginBottom: '20px'
              }}>
                <MetricCard
                  label="Max Eligible Amount"
                  value={formatCurrency(eligibilityResult.maxEligibleAmount)}
                  color="#22c55e"
                />
                <MetricCard
                  label="Interest Rate"
                  value={formatPercentage(eligibilityResult.interestRate)}
                  color="#3b82f6"
                />
                <MetricCard
                  label="Max Tenure"
                  value={`${Math.floor(eligibilityResult.maxTenureMonths / 12)} years`}
                  color="#f59e0b"
                />
                <MetricCard
                  label="Credit Score"
                  value={eligibilityResult.creditScoreAtCheck.toString()}
                  color="#8b5cf6"
                />
              </div>

              {/* Debt-to-Income Ratio */}
              <div style={{
                background: '#f3f4f6',
                borderRadius: '8px',
                padding: '16px',
                marginBottom: '20px'
              }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                  <span style={{ fontSize: '14px', fontWeight: '600' }}>Debt-to-Income Ratio</span>
                  <span style={{ fontSize: '14px', fontWeight: 'bold', color: '#3b82f6' }}>
                    {formatPercentage(eligibilityResult.debtToIncomeRatio * 100)}
                  </span>
                </div>
                <div style={{
                  width: '100%',
                  height: '8px',
                  background: '#e5e7eb',
                  borderRadius: '4px',
                  overflow: 'hidden'
                }}>
                  <div style={{
                    width: `${Math.min(eligibilityResult.debtToIncomeRatio * 100, 100)}%`,
                    height: '100%',
                    background: eligibilityResult.debtToIncomeRatio > 0.4 ? '#ef4444' : '#22c55e',
                    transition: 'width 0.3s'
                  }} />
                </div>
                <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                  {eligibilityResult.debtToIncomeRatio <= 0.4 ? 'Healthy ratio ✅' : 'High ratio ⚠️'}
                </div>
              </div>

              {/* Eligibility Reasons */}
              {eligibilityResult.eligibilityReasons && eligibilityResult.eligibilityReasons.length > 0 && (
                <div style={{ marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '12px' }}>
                    📋 Eligibility Factors
                  </h3>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {eligibilityResult.eligibilityReasons.map((reason, index) => (
                      <div key={index} style={{
                        padding: '12px',
                        background: '#f3f4f6',
                        borderRadius: '6px',
                        fontSize: '13px',
                        display: 'flex',
                        alignItems: 'start',
                        gap: '8px'
                      }}>
                        <span>•</span>
                        <span>{reason}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Conditions */}
              {eligibilityResult.conditions && eligibilityResult.conditions.length > 0 && (
                <div style={{ marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '12px', color: '#f59e0b' }}>
                    ⚠️ Conditions
                  </h3>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {eligibilityResult.conditions.map((condition, index) => (
                      <div key={index} style={{
                        padding: '12px',
                        background: '#fef3c7',
                        borderRadius: '6px',
                        fontSize: '13px',
                        display: 'flex',
                        alignItems: 'start',
                        gap: '8px',
                        border: '1px solid #fbbf24'
                      }}>
                        <span>⚠️</span>
                        <span>{condition}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Suggestions */}
              {eligibilityResult.suggestions && eligibilityResult.suggestions.length > 0 && (
                <div style={{ marginBottom: '20px' }}>
                  <h3 style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '12px' }}>
                    💡 Suggestions
                  </h3>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    {eligibilityResult.suggestions.map((suggestion, index) => (
                      <div key={index} style={{
                        padding: '12px',
                        background: '#dbeafe',
                        borderRadius: '6px',
                        fontSize: '13px',
                        display: 'flex',
                        alignItems: 'start',
                        gap: '8px',
                        border: '1px solid #60a5fa'
                      }}>
                        <span>💡</span>
                        <span>{suggestion}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Valid Until */}
              <div style={{
                fontSize: '12px',
                color: '#666',
                textAlign: 'center',
                padding: '12px',
                background: '#f9fafb',
                borderRadius: '6px'
              }}>
                Valid until: {new Date(eligibilityResult.expiryDate).toLocaleDateString()}
              </div>
            </div>
          )}

          {/* Improvement Plan (if not eligible) */}
          {improvementPlan && !eligibilityResult?.eligible && (
            <div style={{
              background: 'white',
              borderRadius: '12px',
              padding: '24px',
              marginBottom: '24px',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
            }}>
              <h2 style={{ marginTop: 0, fontSize: '20px', fontWeight: 'bold', marginBottom: '16px' }}>
                🎯 How to Qualify
              </h2>

              <div style={{
                background: 'linear-gradient(135deg, #8b5cf622, #8b5cf644)',
                borderRadius: '8px',
                padding: '16px',
                marginBottom: '16px',
                textAlign: 'center'
              }}>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>
                  Improve your score to
                </div>
                <div style={{ fontSize: '32px', fontWeight: 'bold', color: '#22c55e' }}>
                  {improvementPlan.targetScore}
                </div>
                <div style={{ fontSize: '14px', color: '#666', marginTop: '8px' }}>
                  in approximately <strong>{improvementPlan.estimatedTimeframe}</strong>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {improvementPlan.recommendations.slice(0, 3).map((rec, index) => (
                  <div key={index} style={{
                    padding: '16px',
                    background: '#f3f4f6',
                    borderRadius: '8px',
                    borderLeft: `4px solid ${getPriorityColor(rec.priority)}`
                  }}>
                    <div style={{ fontWeight: 'bold', marginBottom: '4px' }}>{rec.title}</div>
                    <div style={{ fontSize: '13px', color: '#666' }}>{rec.description}</div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Prequalified Offers */}
          {prequalifiedOffers.length > 0 && (
            <div style={{
              background: 'white',
              borderRadius: '12px',
              padding: '24px',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
            }}>
              <h2 style={{ marginTop: 0, fontSize: '20px', fontWeight: 'bold', marginBottom: '16px' }}>
                🎁 Pre-Qualified Loan Offers
              </h2>
              <p style={{ margin: '0 0 20px 0', color: '#666', fontSize: '14px' }}>
                Based on your credit profile, you're pre-approved for these loans:
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                {prequalifiedOffers.map((offer, index) => (
                  <div key={index} style={{
                    background: 'linear-gradient(135deg, #22c55e22, #22c55e44)',
                    borderRadius: '8px',
                    padding: '20px',
                    border: '2px solid #22c55e'
                  }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
                      <h3 style={{ margin: 0, fontSize: '18px', fontWeight: 'bold' }}>
                        {getLoanTypeLabel(offer.loanType)}
                      </h3>
                      <span style={{
                        background: '#22c55e',
                        color: 'white',
                        padding: '4px 12px',
                        borderRadius: '12px',
                        fontSize: '12px',
                        fontWeight: '600'
                      }}>
                        PRE-APPROVED
                      </span>
                    </div>

                    <div style={{
                      display: 'grid',
                      gridTemplateColumns: '1fr 1fr',
                      gap: '12px',
                      marginBottom: '16px'
                    }}>
                      <div>
                        <div style={{ fontSize: '12px', color: '#666' }}>Max Amount</div>
                        <div style={{ fontSize: '16px', fontWeight: 'bold' }}>
                          {formatCurrency(offer.maxEligibleAmount)}
                        </div>
                      </div>
                      <div>
                        <div style={{ fontSize: '12px', color: '#666' }}>Interest Rate</div>
                        <div style={{ fontSize: '16px', fontWeight: 'bold' }}>
                          {formatPercentage(offer.interestRate)}
                        </div>
                      </div>
                    </div>

                    <button
                      onClick={() => handleApplyLoan(offer)}
                      style={{
                        width: '100%',
                        padding: '10px',
                        background: '#22c55e',
                        color: 'white',
                        border: 'none',
                        borderRadius: '6px',
                        fontSize: '14px',
                        fontWeight: '600',
                        cursor: 'pointer'
                      }}
                    >
                      Check Detailed Eligibility
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

// Metric Card Component
const MetricCard: React.FC<{
  label: string;
  value: string;
  color: string;
}> = ({ label, value, color }) => (
  <div style={{
    background: `${color}11`,
    borderRadius: '8px',
    padding: '16px',
    border: `1px solid ${color}33`
  }}>
    <div style={{ fontSize: '12px', color: '#666', marginBottom: '4px' }}>
      {label}
    </div>
    <div style={{ fontSize: '20px', fontWeight: 'bold', color }}>
      {value}
    </div>
  </div>
);

export default LoanEligibilityPage;
