import React, { useState, useEffect } from 'react';
import { taxService, TaxCalculationRequest, TaxCalculationResponse, formatCurrency } from '../services/module9Service';
import './TaxCalculatorPage.css';

const TaxCalculatorPage: React.FC = () => {
  const [formData, setFormData] = useState<TaxCalculationRequest>({
    grossIncome: 0,
    financialYear: '',
    assessmentYear: '',
    regime: 'NEW_REGIME',
    age: 30,
    deductions: [],
  });

  const [result, setResult] = useState<TaxCalculationResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [showDeductions, setShowDeductions] = useState(false);

  useEffect(() => {
    loadCurrentYear();
  }, []);

  const loadCurrentYear = async () => {
    try {
      const currentFY = await taxService.getCurrentFinancialYear();
      const [startYear] = currentFY.split('-');
      const assessmentYear = `${parseInt(startYear) + 1}-${parseInt(startYear) + 2}`;
      
      setFormData(prev => ({
        ...prev,
        financialYear: currentFY,
        assessmentYear: assessmentYear.substring(2),
      }));
    } catch (err) {
      console.error('Failed to load current year');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'grossIncome' || name === 'age' ? parseFloat(value) || 0 : value,
    }));
  };

  const handleCalculate = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      const response = await taxService.calculateTax(formData);
      setResult(response);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to calculate tax');
    } finally {
      setLoading(false);
    }
  };

  const getEffectiveTaxRate = () => {
    if (!result || result.grossIncome === 0) return 0;
    return ((result.totalTaxLiability / result.grossIncome) * 100).toFixed(2);
  };

  return (
    <div className="tax-calculator-container">
      <div className="tax-calculator-header">
        <h1>🧮 Income Tax Calculator</h1>
        <p>Calculate your tax liability for FY {formData.financialYear}</p>
      </div>

      <div className="tax-calculator-layout">
        {/* Input Form */}
        <div className="calculator-form-section">
          <form onSubmit={handleCalculate} className="calculator-form">
            <div className="form-group">
              <label htmlFor="grossIncome">
                <span className="label-icon">💰</span>
                Gross Annual Income
              </label>
              <input
                type="number"
                id="grossIncome"
                name="grossIncome"
                value={formData.grossIncome || ''}
                onChange={handleInputChange}
                placeholder="Enter your total income"
                required
                min="0"
                step="1000"
              />
            </div>

            <div className="form-group">
              <label htmlFor="age">
                <span className="label-icon">👤</span>
                Age
              </label>
              <input
                type="number"
                id="age"
                name="age"
                value={formData.age || ''}
                onChange={handleInputChange}
                placeholder="Your age"
                min="18"
                max="100"
              />
            </div>

            <div className="form-group">
              <label htmlFor="regime">
                <span className="label-icon">📋</span>
                Tax Regime
              </label>
              <select
                id="regime"
                name="regime"
                value={formData.regime}
                onChange={handleInputChange}
              >
                <option value="NEW_REGIME">New Regime (Lower Rates)</option>
                <option value="OLD_REGIME">Old Regime (With Deductions)</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="financialYear">
                <span className="label-icon">📅</span>
                Financial Year
              </label>
              <select
                id="financialYear"
                name="financialYear"
                value={formData.financialYear}
                onChange={handleInputChange}
              >
                <option value="2024-25">2024-25</option>
                <option value="2023-24">2023-24</option>
                <option value="2022-23">2022-23</option>
              </select>
            </div>

            {formData.regime === 'OLD_REGIME' && (
              <button
                type="button"
                className="deductions-toggle"
                onClick={() => setShowDeductions(!showDeductions)}
              >
                {showDeductions ? '▼' : '▶'} Add Deductions (Optional)
              </button>
            )}

            {error && <div className="error-message">{error}</div>}

            <button type="submit" className="calculate-btn" disabled={loading}>
              {loading ? 'Calculating...' : '🧮 Calculate Tax'}
            </button>
          </form>
        </div>

        {/* Results Section */}
        {result && (
          <div className="calculator-results-section">
            <div className="results-header">
              <h2>Tax Calculation Results</h2>
              <span className="regime-badge">{result.regime.replace('_', ' ')}</span>
            </div>

            {/* Summary Cards */}
            <div className="results-summary">
              <div className="summary-card primary">
                <div className="summary-label">Total Tax Liability</div>
                <div className="summary-value">{formatCurrency(result.totalTaxLiability)}</div>
                <div className="summary-detail">Effective Rate: {getEffectiveTaxRate()}%</div>
              </div>

              <div className="summary-card">
                <div className="summary-label">Taxable Income</div>
                <div className="summary-value">{formatCurrency(result.taxableIncome)}</div>
              </div>

              <div className="summary-card">
                <div className="summary-label">Total Deductions</div>
                <div className="summary-value">{formatCurrency(result.totalDeductions)}</div>
              </div>
            </div>

            {/* Tax Breakdown */}
            <div className="tax-breakdown">
              <h3>Tax Breakdown</h3>
              <div className="breakdown-items">
                <div className="breakdown-item">
                  <span>Tax before Rebate</span>
                  <span>{formatCurrency(result.taxBeforeRebate)}</span>
                </div>
                <div className="breakdown-item">
                  <span>Rebate (Section 87A)</span>
                  <span className="positive">- {formatCurrency(result.rebate87A)}</span>
                </div>
                <div className="breakdown-item">
                  <span>Tax after Rebate</span>
                  <span>{formatCurrency(result.taxAfterRebate)}</span>
                </div>
                <div className="breakdown-item">
                  <span>Health & Education Cess (4%)</span>
                  <span>{formatCurrency(result.cess)}</span>
                </div>
                <div className="breakdown-item total">
                  <span>Final Tax Liability</span>
                  <span>{formatCurrency(result.totalTaxLiability)}</span>
                </div>
              </div>
            </div>

            {/* Slab Breakdown */}
            {result.slabBreakdown && result.slabBreakdown.length > 0 && (
              <div className="slab-breakdown">
                <h3>Tax Slab Breakdown</h3>
                <table className="slab-table">
                  <thead>
                    <tr>
                      <th>Income Slab</th>
                      <th>Income in Slab</th>
                      <th>Rate</th>
                      <th>Tax Amount</th>
                    </tr>
                  </thead>
                  <tbody>
                    {result.slabBreakdown.map((slab, index) => (
                      <tr key={index}>
                        <td>{slab.slabDescription}</td>
                        <td>{formatCurrency(slab.incomeInSlab)}</td>
                        <td>{(slab.taxRate * 100).toFixed(0)}%</td>
                        <td>{formatCurrency(slab.taxAmount)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}

            {/* Regime Comparison */}
            {result.regimeComparison && (
              <div className="regime-comparison">
                <h3>Regime Comparison</h3>
                <div className="comparison-cards">
                  <div className="comparison-card">
                    <div className="comparison-regime">Old Regime</div>
                    <div className="comparison-amount">{formatCurrency(result.regimeComparison.oldRegimeTax)}</div>
                  </div>
                  <div className="comparison-vs">VS</div>
                  <div className="comparison-card">
                    <div className="comparison-regime">New Regime</div>
                    <div className="comparison-amount">{formatCurrency(result.regimeComparison.newRegimeTax)}</div>
                  </div>
                </div>
                <div className="comparison-recommendation">
                  <strong>💡 Recommendation:</strong> {result.regimeComparison.recommendation}
                  {result.regimeComparison.savingsAmount > 0 && (
                    <div className="savings-amount">
                      Save up to {formatCurrency(result.regimeComparison.savingsAmount)}!
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Info Cards */}
      <div className="info-cards">
        <div className="info-card">
          <h3>🆕 New Tax Regime</h3>
          <ul>
            <li>Lower tax rates (0-30%)</li>
            <li>Standard deduction: ₹50,000</li>
            <li>No other deductions allowed</li>
            <li>Rebate up to ₹25,000 (income ≤₹7L)</li>
          </ul>
        </div>
        <div className="info-card">
          <h3>📊 Old Tax Regime</h3>
          <ul>
            <li>Traditional tax slabs (0-30%)</li>
            <li>80C deduction: Up to ₹1.5L</li>
            <li>80D (Health): ₹25-50k</li>
            <li>Rebate up to ₹12,500 (income ≤₹5L)</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default TaxCalculatorPage;
