import React, { useEffect, useState } from 'react';
import { taxService, TaxSummaryResponse, formatCurrency, formatDate, getDeductionTypeLabel, getPaymentTypeLabel } from '../services/module9Service';
import './TaxDashboardPage.css';

const TaxDashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<TaxSummaryResponse | null>(null);
  const [financialYear, setFinancialYear] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadCurrentYear();
  }, []);

  const loadCurrentYear = async () => {
    try {
      const currentFY = await taxService.getCurrentFinancialYear();
      setFinancialYear(currentFY);
      loadTaxSummary(currentFY);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load current year');
    }
  };

  const loadTaxSummary = async (year: string) => {
    setLoading(true);
    setError('');
    try {
      const data = await taxService.getTaxSummary(year);
      setSummary(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load tax summary');
    } finally {
      setLoading(false);
    }
  };

  const handleYearChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const year = e.target.value;
    setFinancialYear(year);
    loadTaxSummary(year);
  };

  const getTaxStatus = () => {
    if (!summary) return { status: 'unknown', color: '#999' };
    
    const balance = summary.taxBalance;
    if (balance > 0) {
      return { status: `₹${formatCurrency(balance)} Due`, color: '#e74c3c' };
    } else if (balance < 0) {
      return { status: `₹${formatCurrency(Math.abs(balance))} Refund`, color: '#27ae60' };
    } else {
      return { status: 'Settled', color: '#3498db' };
    }
  };

  return (
    <div className="tax-dashboard-container">
      <div className="tax-dashboard-header">
        <h1>💰 Tax Dashboard</h1>
        <div className="year-selector">
          <label>Financial Year:</label>
          <select value={financialYear} onChange={handleYearChange}>
            <option value="2024-25">2024-25</option>
            <option value="2023-24">2023-24</option>
            <option value="2022-23">2022-23</option>
          </select>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="loading-spinner">Loading tax summary...</div>
      ) : summary ? (
        <>
          {/* Summary Cards */}
          <div className="tax-summary-grid">
            <div className="tax-card">
              <div className="tax-card-icon">📊</div>
              <div className="tax-card-content">
                <h3>Gross Income</h3>
                <p className="tax-amount">{formatCurrency(summary.totalGrossIncome)}</p>
                <span className="tax-label">FY {summary.financialYear}</span>
              </div>
            </div>

            <div className="tax-card">
              <div className="tax-card-icon">🎯</div>
              <div className="tax-card-content">
                <h3>Total Deductions</h3>
                <p className="tax-amount">{formatCurrency(summary.totalDeductions)}</p>
                <span className="tax-label">Tax Savings</span>
              </div>
            </div>

            <div className="tax-card">
              <div className="tax-card-icon">💵</div>
              <div className="tax-card-content">
                <h3>Taxable Income</h3>
                <p className="tax-amount">{formatCurrency(summary.taxableIncome)}</p>
                <span className="tax-label">After Deductions</span>
              </div>
            </div>

            <div className="tax-card">
              <div className="tax-card-icon">🧮</div>
              <div className="tax-card-content">
                <h3>Estimated Tax</h3>
                <p className="tax-amount">{formatCurrency(summary.estimatedTaxLiability)}</p>
                <span className="tax-label">AY {summary.assessmentYear}</span>
              </div>
            </div>

            <div className="tax-card">
              <div className="tax-card-icon">✅</div>
              <div className="tax-card-content">
                <h3>Tax Paid</h3>
                <p className="tax-amount">{formatCurrency(summary.totalTaxPaid)}</p>
                <span className="tax-label">Total Payments</span>
              </div>
            </div>

            <div className="tax-card highlight">
              <div className="tax-card-icon">⚖️</div>
              <div className="tax-card-content">
                <h3>Balance</h3>
                <p className="tax-amount" style={{ color: getTaxStatus().color }}>
                  {getTaxStatus().status}
                </p>
                <span className="tax-label">Current Status</span>
              </div>
            </div>
          </div>

          {/* Payments Table */}
          <div className="tax-section">
            <h2>💳 Tax Payments ({summary.payments.length})</h2>
            {summary.payments.length > 0 ? (
              <div className="tax-table-wrapper">
                <table className="tax-table">
                  <thead>
                    <tr>
                      <th>Payment Type</th>
                      <th>Amount</th>
                      <th>Payment Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {summary.payments.map((payment, index) => (
                      <tr key={index}>
                        <td>{getPaymentTypeLabel(payment.paymentType)}</td>
                        <td className="amount">{formatCurrency(payment.amount)}</td>
                        <td>{formatDate(payment.paymentDate)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <p className="empty-state">No tax payments recorded for this year</p>
            )}
          </div>

          {/* Documents Section */}
          <div className="tax-section">
            <h2>📄 Tax Documents ({summary.documents.length})</h2>
            {summary.documents.length > 0 ? (
              <div className="documents-grid">
                {summary.documents.map((doc, index) => (
                  <div key={index} className="document-card">
                    <div className="document-icon">📋</div>
                    <div className="document-info">
                      <h4>{doc.documentType}</h4>
                      <p>Uploaded: {formatDate(doc.uploadDate)}</p>
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className="empty-state">No tax documents uploaded for this year</p>
            )}
          </div>

          {/* Recommendations */}
          {summary.taxSavingRecommendations.length > 0 && (
            <div className="tax-section recommendations">
              <h2>💡 Tax Saving Recommendations</h2>
              <ul className="recommendations-list">
                {summary.taxSavingRecommendations.map((recommendation, index) => (
                  <li key={index}>{recommendation}</li>
                ))}
              </ul>
            </div>
          )}
        </>
      ) : (
        <div className="empty-state-large">
          <div className="empty-icon">📊</div>
          <h3>No Tax Data Available</h3>
          <p>Start by calculating your tax or adding deductions</p>
        </div>
      )}
    </div>
  );
};

export default TaxDashboardPage;
