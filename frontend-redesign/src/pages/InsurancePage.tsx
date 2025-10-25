import React, { useState, useEffect } from 'react';
import './InsurancePage.css';
import {
  getAllPolicies,
  applyForPolicy,
  payPremium,
  getPremiumHistory,
  cancelPolicy,
  PolicyDetailsResponse,
  PolicyApplicationRequest,
  PremiumPaymentRequest,
  InsurancePremiumPayment,
  InsuranceType,
  PremiumFrequency,
  InsurancePolicyStatus,
  getInsuranceTypeLabel,
  getPolicyStatusLabel,
  getPremiumFrequencyLabel,
  calculateDaysUntilDue,
  formatCurrency,
  formatDate
} from '../services/module8Service';

const InsurancePage: React.FC = () => {
  const [policies, setPolicies] = useState<PolicyDetailsResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [showPayPremiumModal, setShowPayPremiumModal] = useState(false);
  const [showPremiumHistoryModal, setShowPremiumHistoryModal] = useState(false);
  const [selectedPolicy, setSelectedPolicy] = useState<PolicyDetailsResponse | null>(null);
  const [premiumHistory, setPremiumHistory] = useState<InsurancePremiumPayment[]>([]);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const [applicationForm, setApplicationForm] = useState<PolicyApplicationRequest>({
    insuranceType: InsuranceType.LIFE,
    policyName: '',
    coverageAmount: 1000000,
    termYears: 10,
    premiumFrequency: PremiumFrequency.YEARLY,
    nominee: '',
    nomineeRelation: '',
    nomineePercentage: 100,
    remarks: ''
  });

  const [premiumPaymentForm, setPremiumPaymentForm] = useState<PremiumPaymentRequest>({
    policyId: 0,
    amount: 0,
    paymentMethod: 'ACCOUNT_DEBIT',
    accountNumber: '',
    remarks: ''
  });

  useEffect(() => {
    loadPolicies();
  }, []);

  const loadPolicies = async () => {
    try {
      setLoading(true);
      const data = await getAllPolicies();
      setPolicies(data);
    } catch (error) {
      showMessage('error', 'Failed to load policies');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleApplyForPolicy = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await applyForPolicy(applicationForm);
      showMessage('success', 'Policy application submitted successfully!');
      setShowApplyModal(false);
      loadPolicies();
      resetApplicationForm();
    } catch (error) {
      showMessage('error', 'Failed to apply for policy');
      console.error(error);
    }
  };

  const handlePayPremium = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await payPremium(premiumPaymentForm);
      showMessage('success', `Premium paid! Reference: ${response.paymentReference}`);
      setShowPayPremiumModal(false);
      loadPolicies();
      resetPremiumForm();
    } catch (error) {
      showMessage('error', 'Failed to pay premium');
      console.error(error);
    }
  };

  const handleViewPremiumHistory = async (policy: PolicyDetailsResponse) => {
    try {
      const history = await getPremiumHistory(policy.id);
      setPremiumHistory(history);
      setSelectedPolicy(policy);
      setShowPremiumHistoryModal(true);
    } catch (error) {
      showMessage('error', 'Failed to load premium history');
      console.error(error);
    }
  };

  const handleCancelPolicy = async (policyId: number) => {
    if (!window.confirm('Are you sure you want to cancel this policy?')) return;
    
    const reason = window.prompt('Please enter reason for cancellation:');
    if (!reason) return;

    try {
      await cancelPolicy(policyId, reason);
      showMessage('success', 'Policy cancelled successfully');
      loadPolicies();
    } catch (error) {
      showMessage('error', 'Failed to cancel policy');
      console.error(error);
    }
  };

  const openPayPremiumModal = (policy: PolicyDetailsResponse) => {
    setPremiumPaymentForm({
      policyId: policy.id,
      amount: policy.premiumAmount,
      paymentMethod: 'ACCOUNT_DEBIT',
      accountNumber: '',
      remarks: ''
    });
    setSelectedPolicy(policy);
    setShowPayPremiumModal(true);
  };

  const resetApplicationForm = () => {
    setApplicationForm({
      insuranceType: InsuranceType.LIFE,
      policyName: '',
      coverageAmount: 1000000,
      termYears: 10,
      premiumFrequency: PremiumFrequency.YEARLY,
      nominee: '',
      nomineeRelation: '',
      nomineePercentage: 100,
      remarks: ''
    });
  };

  const resetPremiumForm = () => {
    setPremiumPaymentForm({
      policyId: 0,
      amount: 0,
      paymentMethod: 'ACCOUNT_DEBIT',
      accountNumber: '',
      remarks: ''
    });
  };

  const showMessage = (type: 'success' | 'error', text: string) => {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 5000);
  };

  const getStatusClass = (status: InsurancePolicyStatus): string => {
    switch (status) {
      case InsurancePolicyStatus.ACTIVE: return 'status-active';
      case InsurancePolicyStatus.PENDING_APPROVAL: return 'status-pending';
      case InsurancePolicyStatus.PENDING_PAYMENT: return 'status-warning';
      case InsurancePolicyStatus.LAPSED:
      case InsurancePolicyStatus.EXPIRED:
      case InsurancePolicyStatus.CANCELLED:
      case InsurancePolicyStatus.REJECTED: return 'status-inactive';
      case InsurancePolicyStatus.MATURED: return 'status-matured';
      default: return '';
    }
  };

  const activePolicies = policies.filter(p => p.status === InsurancePolicyStatus.ACTIVE);
  const pendingPolicies = policies.filter(p => p.status === InsurancePolicyStatus.PENDING_APPROVAL || p.status === InsurancePolicyStatus.PENDING_PAYMENT);
  const inactivePolicies = policies.filter(p => ![InsurancePolicyStatus.ACTIVE, InsurancePolicyStatus.PENDING_APPROVAL, InsurancePolicyStatus.PENDING_PAYMENT].includes(p.status));

  return (
    <div className="insurance-page">
      <div className="page-header">
        <h1>Insurance Management</h1>
        <button className="btn btn-primary" onClick={() => setShowApplyModal(true)}>
          Apply for New Policy
        </button>
      </div>

      {message && (
        <div className={`alert alert-${message.type}`}>
          {message.text}
        </div>
      )}

      {loading ? (
        <div className="loading">Loading policies...</div>
      ) : (
        <>
          {/* Summary Cards */}
          <div className="summary-cards">
            <div className="summary-card">
              <div className="card-icon">🛡️</div>
              <div className="card-content">
                <h3>Active Policies</h3>
                <p className="card-value">{activePolicies.length}</p>
              </div>
            </div>
            <div className="summary-card">
              <div className="card-icon">⏳</div>
              <div className="card-content">
                <h3>Pending</h3>
                <p className="card-value">{pendingPolicies.length}</p>
              </div>
            </div>
            <div className="summary-card">
              <div className="card-icon">💰</div>
              <div className="card-content">
                <h3>Total Coverage</h3>
                <p className="card-value">
                  {formatCurrency(activePolicies.reduce((sum, p) => sum + p.coverageAmount, 0))}
                </p>
              </div>
            </div>
          </div>

          {/* Active Policies */}
          {activePolicies.length > 0 && (
            <div className="policies-section">
              <h2>Active Policies</h2>
              <div className="policies-grid">
                {activePolicies.map(policy => (
                  <div key={policy.id} className="policy-card">
                    <div className="policy-header">
                      <div className="policy-type">{getInsuranceTypeLabel(policy.insuranceType)}</div>
                      <span className={`policy-status ${getStatusClass(policy.status)}`}>
                        {getPolicyStatusLabel(policy.status)}
                      </span>
                    </div>
                    <div className="policy-body">
                      <h3>{policy.policyName}</h3>
                      <p className="policy-number">Policy #{policy.policyNumber}</p>
                      
                      <div className="policy-details">
                        <div className="detail-row">
                          <span className="label">Coverage:</span>
                          <span className="value">{formatCurrency(policy.coverageAmount)}</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">Premium:</span>
                          <span className="value">{formatCurrency(policy.premiumAmount)} ({getPremiumFrequencyLabel(policy.premiumFrequency)})</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">Term:</span>
                          <span className="value">{policy.termYears} years</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">Valid Until:</span>
                          <span className="value">{formatDate(policy.endDate)}</span>
                        </div>
                        {policy.nextPremiumDueDate && (
                          <div className="detail-row">
                            <span className="label">Next Premium:</span>
                            <span className="value due-date">
                              {formatDate(policy.nextPremiumDueDate)}
                              {calculateDaysUntilDue(policy.nextPremiumDueDate) <= 30 && (
                                <span className="due-warning"> ({calculateDaysUntilDue(policy.nextPremiumDueDate)} days)</span>
                              )}
                            </span>
                          </div>
                        )}
                        <div className="detail-row">
                          <span className="label">Nominee:</span>
                          <span className="value">{policy.nominee} ({policy.nomineeRelation})</span>
                        </div>
                      </div>
                    </div>
                    <div className="policy-actions">
                      <button className="btn btn-sm btn-primary" onClick={() => openPayPremiumModal(policy)}>
                        Pay Premium
                      </button>
                      <button className="btn btn-sm btn-secondary" onClick={() => handleViewPremiumHistory(policy)}>
                        View History
                      </button>
                      <button className="btn btn-sm btn-danger" onClick={() => handleCancelPolicy(policy.id)}>
                        Cancel
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Pending Policies */}
          {pendingPolicies.length > 0 && (
            <div className="policies-section">
              <h2>Pending Policies</h2>
              <div className="policies-grid">
                {pendingPolicies.map(policy => (
                  <div key={policy.id} className="policy-card pending">
                    <div className="policy-header">
                      <div className="policy-type">{getInsuranceTypeLabel(policy.insuranceType)}</div>
                      <span className={`policy-status ${getStatusClass(policy.status)}`}>
                        {getPolicyStatusLabel(policy.status)}
                      </span>
                    </div>
                    <div className="policy-body">
                      <h3>{policy.policyName}</h3>
                      <p className="policy-number">Application #{policy.policyNumber}</p>
                      <div className="policy-details">
                        <div className="detail-row">
                          <span className="label">Coverage:</span>
                          <span className="value">{formatCurrency(policy.coverageAmount)}</span>
                        </div>
                        <div className="detail-row">
                          <span className="label">Applied On:</span>
                          <span className="value">{formatDate(policy.applicationDate)}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Inactive Policies */}
          {inactivePolicies.length > 0 && (
            <div className="policies-section">
              <h2>Inactive Policies</h2>
              <div className="policies-list">
                {inactivePolicies.map(policy => (
                  <div key={policy.id} className="policy-list-item">
                    <div className="policy-info">
                      <span className="policy-name">{policy.policyName}</span>
                      <span className="policy-number">#{policy.policyNumber}</span>
                    </div>
                    <span className={`policy-status ${getStatusClass(policy.status)}`}>
                      {getPolicyStatusLabel(policy.status)}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {policies.length === 0 && (
            <div className="empty-state">
              <div className="empty-icon">🛡️</div>
              <h3>No Insurance Policies</h3>
              <p>Apply for your first insurance policy to get started</p>
              <button className="btn btn-primary" onClick={() => setShowApplyModal(true)}>
                Apply Now
              </button>
            </div>
          )}
        </>
      )}

      {/* Apply for Policy Modal */}
      {showApplyModal && (
        <div className="modal-overlay" onClick={() => setShowApplyModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Apply for Insurance Policy</h2>
              <button className="close-btn" onClick={() => setShowApplyModal(false)}>×</button>
            </div>
            <form onSubmit={handleApplyForPolicy}>
              <div className="form-group">
                <label>Insurance Type *</label>
                <select
                  value={applicationForm.insuranceType}
                  onChange={(e) => setApplicationForm({ ...applicationForm, insuranceType: e.target.value as InsuranceType })}
                  required
                >
                  {Object.values(InsuranceType).map(type => (
                    <option key={type} value={type}>{getInsuranceTypeLabel(type)}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Policy Name *</label>
                <input
                  type="text"
                  value={applicationForm.policyName}
                  onChange={(e) => setApplicationForm({ ...applicationForm, policyName: e.target.value })}
                  placeholder="e.g., My Life Insurance"
                  required
                />
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Coverage Amount *</label>
                  <input
                    type="number"
                    value={applicationForm.coverageAmount}
                    onChange={(e) => setApplicationForm({ ...applicationForm, coverageAmount: Number(e.target.value) })}
                    min="100000"
                    step="100000"
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Term (Years) *</label>
                  <input
                    type="number"
                    value={applicationForm.termYears}
                    onChange={(e) => setApplicationForm({ ...applicationForm, termYears: Number(e.target.value) })}
                    min="1"
                    max="40"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Premium Frequency *</label>
                <select
                  value={applicationForm.premiumFrequency}
                  onChange={(e) => setApplicationForm({ ...applicationForm, premiumFrequency: e.target.value as PremiumFrequency })}
                  required
                >
                  {Object.values(PremiumFrequency).map(freq => (
                    <option key={freq} value={freq}>{getPremiumFrequencyLabel(freq)}</option>
                  ))}
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Nominee Name *</label>
                  <input
                    type="text"
                    value={applicationForm.nominee}
                    onChange={(e) => setApplicationForm({ ...applicationForm, nominee: e.target.value })}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Relationship *</label>
                  <input
                    type="text"
                    value={applicationForm.nomineeRelation}
                    onChange={(e) => setApplicationForm({ ...applicationForm, nomineeRelation: e.target.value })}
                    placeholder="e.g., Spouse, Parent"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Remarks</label>
                <textarea
                  value={applicationForm.remarks}
                  onChange={(e) => setApplicationForm({ ...applicationForm, remarks: e.target.value })}
                  rows={3}
                  placeholder="Additional information (optional)"
                />
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowApplyModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Submit Application
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Pay Premium Modal */}
      {showPayPremiumModal && selectedPolicy && (
        <div className="modal-overlay" onClick={() => setShowPayPremiumModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Pay Premium</h2>
              <button className="close-btn" onClick={() => setShowPayPremiumModal(false)}>×</button>
            </div>
            <div className="payment-info">
              <p><strong>Policy:</strong> {selectedPolicy.policyName}</p>
              <p><strong>Premium Amount:</strong> {formatCurrency(selectedPolicy.premiumAmount)}</p>
            </div>
            <form onSubmit={handlePayPremium}>
              <div className="form-group">
                <label>Amount *</label>
                <input
                  type="number"
                  value={premiumPaymentForm.amount}
                  onChange={(e) => setPremiumPaymentForm({ ...premiumPaymentForm, amount: Number(e.target.value) })}
                  min="1"
                  step="0.01"
                  required
                />
              </div>

              <div className="form-group">
                <label>Payment Method *</label>
                <select
                  value={premiumPaymentForm.paymentMethod}
                  onChange={(e) => setPremiumPaymentForm({ ...premiumPaymentForm, paymentMethod: e.target.value })}
                  required
                >
                  <option value="ACCOUNT_DEBIT">Account Debit</option>
                  <option value="CARD">Debit/Credit Card</option>
                  <option value="UPI">UPI</option>
                  <option value="NETBANKING">Net Banking</option>
                </select>
              </div>

              {premiumPaymentForm.paymentMethod === 'ACCOUNT_DEBIT' && (
                <div className="form-group">
                  <label>Account Number *</label>
                  <input
                    type="text"
                    value={premiumPaymentForm.accountNumber}
                    onChange={(e) => setPremiumPaymentForm({ ...premiumPaymentForm, accountNumber: e.target.value })}
                    placeholder="Enter account number"
                    required
                  />
                </div>
              )}

              <div className="form-group">
                <label>Remarks</label>
                <input
                  type="text"
                  value={premiumPaymentForm.remarks}
                  onChange={(e) => setPremiumPaymentForm({ ...premiumPaymentForm, remarks: e.target.value })}
                  placeholder="Optional"
                />
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowPayPremiumModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Pay Now
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Premium History Modal */}
      {showPremiumHistoryModal && selectedPolicy && (
        <div className="modal-overlay" onClick={() => setShowPremiumHistoryModal(false)}>
          <div className="modal-content large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Premium Payment History</h2>
              <button className="close-btn" onClick={() => setShowPremiumHistoryModal(false)}>×</button>
            </div>
            <div className="history-info">
              <p><strong>Policy:</strong> {selectedPolicy.policyName} (#{selectedPolicy.policyNumber})</p>
            </div>
            <div className="history-table">
              {premiumHistory.length > 0 ? (
                <table>
                  <thead>
                    <tr>
                      <th>Payment Date</th>
                      <th>Amount</th>
                      <th>Period</th>
                      <th>Method</th>
                      <th>Reference</th>
                      <th>Status</th>
                    </tr>
                  </thead>
                  <tbody>
                    {premiumHistory.map(payment => (
                      <tr key={payment.id}>
                        <td>{formatDate(payment.paymentDate)}</td>
                        <td>{formatCurrency(payment.amount)}</td>
                        <td>{formatDate(payment.periodStartDate)} - {formatDate(payment.periodEndDate)}</td>
                        <td>{payment.paymentMethod}</td>
                        <td>{payment.paymentReference}</td>
                        <td><span className={`status-badge ${payment.status.toLowerCase()}`}>{payment.status}</span></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              ) : (
                <p className="no-data">No payment history available</p>
              )}
            </div>
            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setShowPremiumHistoryModal(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default InsurancePage;
