import React, { useState } from 'react';
import { 
  createRetirementPlan, 
  RetirementPlanRequest, 
  RetirementPlan,
  formatCurrency,
  formatPercentage,
  calculateYearsToRetirement,
  calculateRetirementReadiness
} from '../services/module10Service';
import './RetirementPlannerPage.css';

const RetirementPlannerPage: React.FC = () => {
  const [formData, setFormData] = useState<RetirementPlanRequest>({
    currentAge: 30,
    retirementAge: 60,
    currentSavings: 500000,
    monthlyInvestment: 15000,
    expectedReturn: 12.0,
    inflationRate: 6.0,
    desiredMonthlyIncome: 50000,
    lifeExpectancy: 80
  });

  const [plan, setPlan] = useState<RetirementPlan | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: parseFloat(value) || 0
    }));
  };

  const handleCalculate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const result = await createRetirementPlan(formData);
      setPlan(result);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to calculate retirement plan');
    } finally {
      setLoading(false);
    }
  };

  const yearsToRetirement = calculateYearsToRetirement(formData.currentAge, formData.retirementAge);
  const readinessPercentage = plan ? calculateRetirementReadiness(plan.projectedCorpus, plan.corpusRequired) : 0;

  return (
    <div className="retirement-planner-container">
      <div className="retirement-planner-header">
        <h1>🏖️ Retirement Planner</h1>
        <p>Plan your golden years with confidence</p>
      </div>

      <div className="retirement-planner-content">
        {/* Input Form */}
        <div className="planner-form-section">
          <h2>📋 Your Information</h2>
          <form onSubmit={handleCalculate} className="planner-form">
            <div className="form-group">
              <label>Current Age</label>
              <input
                type="number"
                name="currentAge"
                value={formData.currentAge}
                onChange={handleInputChange}
                min="18"
                max="100"
                required
              />
            </div>

            <div className="form-group">
              <label>Retirement Age</label>
              <input
                type="number"
                name="retirementAge"
                value={formData.retirementAge}
                onChange={handleInputChange}
                min={formData.currentAge + 1}
                max="100"
                required
              />
            </div>

            <div className="form-group">
              <label>Current Savings (₹)</label>
              <input
                type="number"
                name="currentSavings"
                value={formData.currentSavings}
                onChange={handleInputChange}
                min="0"
                step="10000"
                required
              />
            </div>

            <div className="form-group">
              <label>Monthly Investment (₹)</label>
              <input
                type="number"
                name="monthlyInvestment"
                value={formData.monthlyInvestment}
                onChange={handleInputChange}
                min="0"
                step="1000"
                required
              />
            </div>

            <div className="form-group">
              <label>Expected Return (% p.a.)</label>
              <input
                type="number"
                name="expectedReturn"
                value={formData.expectedReturn}
                onChange={handleInputChange}
                min="1"
                max="30"
                step="0.5"
                required
              />
            </div>

            <div className="form-group">
              <label>Inflation Rate (% p.a.)</label>
              <input
                type="number"
                name="inflationRate"
                value={formData.inflationRate}
                onChange={handleInputChange}
                min="1"
                max="15"
                step="0.5"
                required
              />
            </div>

            <div className="form-group">
              <label>Desired Monthly Income in Retirement (₹)</label>
              <input
                type="number"
                name="desiredMonthlyIncome"
                value={formData.desiredMonthlyIncome}
                onChange={handleInputChange}
                min="10000"
                step="5000"
                required
              />
            </div>

            <div className="form-group">
              <label>Life Expectancy</label>
              <input
                type="number"
                name="lifeExpectancy"
                value={formData.lifeExpectancy}
                onChange={handleInputChange}
                min={formData.retirementAge + 1}
                max="120"
                required
              />
            </div>

            <button type="submit" className="calculate-button" disabled={loading}>
              {loading ? 'Calculating...' : '🧮 Calculate Retirement Plan'}
            </button>
          </form>

          {error && <div className="error-message">{error}</div>}
        </div>

        {/* Results Section */}
        {plan && (
          <div className="planner-results-section">
            <h2>📊 Your Retirement Plan</h2>

            {/* Readiness Indicator */}
            <div className="readiness-card">
              <div className="readiness-header">
                <h3>Retirement Readiness</h3>
                <div className={`readiness-status ${plan.onTrack ? 'on-track' : 'off-track'}`}>
                  {plan.onTrack ? '✅ On Track' : '⚠️ Needs Attention'}
                </div>
              </div>
              <div className="readiness-bar-container">
                <div 
                  className="readiness-bar" 
                  style={{ 
                    width: `${Math.min(readinessPercentage, 100)}%`,
                    backgroundColor: readinessPercentage >= 100 ? '#10b981' : readinessPercentage >= 75 ? '#3b82f6' : readinessPercentage >= 50 ? '#f59e0b' : '#ef4444'
                  }}
                >
                  <span className="readiness-percentage">{readinessPercentage.toFixed(1)}%</span>
                </div>
              </div>
            </div>

            {/* Corpus Comparison */}
            <div className="corpus-comparison-grid">
              <div className="corpus-card projected">
                <div className="corpus-icon">💰</div>
                <div className="corpus-label">Projected Corpus</div>
                <div className="corpus-value">{formatCurrency(plan.projectedCorpus)}</div>
                <div className="corpus-detail">
                  Based on {yearsToRetirement} years of investing
                </div>
              </div>

              <div className="corpus-card required">
                <div className="corpus-icon">🎯</div>
                <div className="corpus-label">Required Corpus</div>
                <div className="corpus-value">{formatCurrency(plan.corpusRequired)}</div>
                <div className="corpus-detail">
                  For {formData.lifeExpectancy - plan.retirementAge} years post-retirement
                </div>
              </div>

              {plan.shortfall > 0 && (
                <div className="corpus-card shortfall">
                  <div className="corpus-icon">⚠️</div>
                  <div className="corpus-label">Shortfall</div>
                  <div className="corpus-value">{formatCurrency(plan.shortfall)}</div>
                  <div className="corpus-detail">
                    Gap to close
                  </div>
                </div>
              )}

              {plan.surplus > 0 && (
                <div className="corpus-card surplus">
                  <div className="corpus-icon">🎉</div>
                  <div className="corpus-label">Surplus</div>
                  <div className="corpus-value">{formatCurrency(plan.surplus)}</div>
                  <div className="corpus-detail">
                    Extra cushion
                  </div>
                </div>
              )}
            </div>

            {/* SIP Recommendation */}
            {plan.recommendedMonthlySip > formData.monthlyInvestment && (
              <div className="sip-recommendation-card">
                <div className="sip-recommendation-header">
                  <h3>💡 Recommended Action</h3>
                </div>
                <div className="sip-recommendation-content">
                  <div className="sip-current">
                    <div className="sip-label">Current SIP</div>
                    <div className="sip-value">{formatCurrency(formData.monthlyInvestment)}/month</div>
                  </div>
                  <div className="sip-arrow">→</div>
                  <div className="sip-recommended">
                    <div className="sip-label">Recommended SIP</div>
                    <div className="sip-value highlight">{formatCurrency(plan.recommendedMonthlySip)}/month</div>
                  </div>
                </div>
                <div className="sip-increase">
                  Increase by {formatCurrency(plan.recommendedMonthlySip - formData.monthlyInvestment)} per month to meet your goal
                </div>
              </div>
            )}

            {/* Timeline Visual */}
            <div className="timeline-card">
              <h3>⏰ Retirement Timeline</h3>
              <div className="timeline-container">
                <div className="timeline-point current">
                  <div className="timeline-marker"></div>
                  <div className="timeline-label">Now</div>
                  <div className="timeline-age">Age {plan.currentAge}</div>
                </div>
                <div className="timeline-line">
                  <div className="timeline-duration">{plan.yearsToRetirement} years</div>
                </div>
                <div className="timeline-point retirement">
                  <div className="timeline-marker"></div>
                  <div className="timeline-label">Retirement</div>
                  <div className="timeline-age">Age {plan.retirementAge}</div>
                </div>
              </div>
            </div>

            {/* Key Assumptions */}
            <div className="assumptions-card">
              <h3>📌 Key Assumptions</h3>
              <div className="assumptions-grid">
                <div className="assumption-item">
                  <div className="assumption-label">Current Savings</div>
                  <div className="assumption-value">{formatCurrency(formData.currentSavings)}</div>
                </div>
                <div className="assumption-item">
                  <div className="assumption-label">Monthly Investment</div>
                  <div className="assumption-value">{formatCurrency(formData.monthlyInvestment)}</div>
                </div>
                <div className="assumption-item">
                  <div className="assumption-label">Expected Return</div>
                  <div className="assumption-value">{formatPercentage(formData.expectedReturn)}</div>
                </div>
                <div className="assumption-item">
                  <div className="assumption-label">Inflation Rate</div>
                  <div className="assumption-value">{formatPercentage(formData.inflationRate)}</div>
                </div>
                <div className="assumption-item">
                  <div className="assumption-label">Desired Income</div>
                  <div className="assumption-value">{formatCurrency(formData.desiredMonthlyIncome)}/mo</div>
                </div>
                <div className="assumption-item">
                  <div className="assumption-label">Life Expectancy</div>
                  <div className="assumption-value">{formData.lifeExpectancy} years</div>
                </div>
              </div>
            </div>

            {/* Recommendations */}
            {plan.recommendations && plan.recommendations.length > 0 && (
              <div className="recommendations-card">
                <h3>💡 Personalized Recommendations</h3>
                <div className="recommendations-list">
                  {plan.recommendations.map((rec, index) => (
                    <div key={index} className="recommendation-item">
                      <div className="recommendation-bullet">•</div>
                      <div className="recommendation-text">{rec}</div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default RetirementPlannerPage;
