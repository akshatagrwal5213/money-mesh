import React, { useState, useEffect } from 'react';
import './ClaimsPage.css';
import {
  getAllClaims,
  getAllPolicies,
  fileClaim,
  ClaimDetailsResponse,
  PolicyDetailsResponse,
  ClaimRequest,
  ClaimStatus,
  InsurancePolicyStatus,
  getClaimStatusLabel,
  getInsuranceTypeLabel,
  formatCurrency,
  formatDate
} from '../services/module8Service';

const ClaimsPage: React.FC = () => {
  const [claims, setClaims] = useState<ClaimDetailsResponse[]>([]);
  const [policies, setPolicies] = useState<PolicyDetailsResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [showFileClaimModal, setShowFileClaimModal] = useState(false);
  const [selectedClaim, setSelectedClaim] = useState<ClaimDetailsResponse | null>(null);
  const [showClaimDetailsModal, setShowClaimDetailsModal] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const [claimForm, setClaimForm] = useState<ClaimRequest>({
    policyId: 0,
    claimAmount: 0,
    incidentDate: '',
    description: '',
    hospitalName: '',
    doctorName: '',
    documentsSubmitted: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [claimsData, policiesData] = await Promise.all([
        getAllClaims(),
        getAllPolicies()
      ]);
      setClaims(claimsData);
      setPolicies(policiesData.filter(p => p.status === InsurancePolicyStatus.ACTIVE));
    } catch (error) {
      showMessage('error', 'Failed to load data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleFileClaim = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await fileClaim(claimForm);
      showMessage('success', 'Claim filed successfully!');
      setShowFileClaimModal(false);
      loadData();
      resetClaimForm();
    } catch (error: any) {
      showMessage('error', error.response?.data?.message || 'Failed to file claim');
      console.error(error);
    }
  };

  const handleViewDetails = (claim: ClaimDetailsResponse) => {
    setSelectedClaim(claim);
    setShowClaimDetailsModal(true);
  };

  const resetClaimForm = () => {
    setClaimForm({
      policyId: 0,
      claimAmount: 0,
      incidentDate: '',
      description: '',
      hospitalName: '',
      doctorName: '',
      documentsSubmitted: ''
    });
  };

  const showMessage = (type: 'success' | 'error', text: string) => {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 5000);
  };

  const getStatusClass = (status: ClaimStatus): string => {
    switch (status) {
      case ClaimStatus.SUBMITTED:
      case ClaimStatus.UNDER_REVIEW: return 'status-pending';
      case ClaimStatus.PENDING_DOCUMENTS: return 'status-warning';
      case ClaimStatus.APPROVED: return 'status-approved';
      case ClaimStatus.PAID: return 'status-paid';
      case ClaimStatus.REJECTED:
      case ClaimStatus.CLOSED: return 'status-rejected';
      default: return '';
    }
  };

  const getStatusIcon = (status: ClaimStatus): string => {
    switch (status) {
      case ClaimStatus.SUBMITTED: return '📝';
      case ClaimStatus.UNDER_REVIEW: return '🔍';
      case ClaimStatus.PENDING_DOCUMENTS: return '📄';
      case ClaimStatus.APPROVED: return '✅';
      case ClaimStatus.PAID: return '💰';
      case ClaimStatus.REJECTED: return '❌';
      case ClaimStatus.CLOSED: return '🔒';
      default: return '📋';
    }
  };

  const pendingClaims = claims.filter(c => 
    c.status === ClaimStatus.SUBMITTED || 
    c.status === ClaimStatus.UNDER_REVIEW || 
    c.status === ClaimStatus.PENDING_DOCUMENTS
  );
  const approvedClaims = claims.filter(c => c.status === ClaimStatus.APPROVED || c.status === ClaimStatus.PAID);
  const closedClaims = claims.filter(c => c.status === ClaimStatus.REJECTED || c.status === ClaimStatus.CLOSED);

  return (
    <div className="claims-page">
      <div className="page-header">
        <h1>Insurance Claims</h1>
        <button 
          className="btn btn-primary" 
          onClick={() => setShowFileClaimModal(true)}
          disabled={policies.length === 0}
        >
          File New Claim
        </button>
      </div>

      {message && (
        <div className={`alert alert-${message.type}`}>
          {message.text}
        </div>
      )}

      {loading ? (
        <div className="loading">Loading claims...</div>
      ) : (
        <>
          {/* Summary Cards */}
          <div className="summary-cards">
            <div className="summary-card pending">
              <div className="card-icon">⏳</div>
              <div className="card-content">
                <h3>Pending Review</h3>
                <p className="card-value">{pendingClaims.length}</p>
              </div>
            </div>
            <div className="summary-card approved">
              <div className="card-icon">✅</div>
              <div className="card-content">
                <h3>Approved</h3>
                <p className="card-value">{approvedClaims.length}</p>
              </div>
            </div>
            <div className="summary-card total">
              <div className="card-icon">💰</div>
              <div className="card-content">
                <h3>Total Claimed</h3>
                <p className="card-value">
                  {formatCurrency(claims.reduce((sum, c) => sum + c.claimAmount, 0))}
                </p>
              </div>
            </div>
          </div>

          {/* Pending Claims */}
          {pendingClaims.length > 0 && (
            <div className="claims-section">
              <h2>Pending Claims ({pendingClaims.length})</h2>
              <div className="claims-grid">
                {pendingClaims.map(claim => (
                  <div key={claim.id} className="claim-card pending">
                    <div className="claim-header">
                      <div className="claim-status-badge">
                        <span className="status-icon">{getStatusIcon(claim.status)}</span>
                        <span className={`status-text ${getStatusClass(claim.status)}`}>
                          {getClaimStatusLabel(claim.status)}
                        </span>
                      </div>
                      <span className="claim-number">#{claim.claimNumber}</span>
                    </div>
                    <div className="claim-body">
                      <h3>{claim.policyName}</h3>
                      <p className="claim-description">{claim.description}</p>
                      
                      <div className="claim-details">
                        <div className="detail-item">
                          <span className="label">Claim Amount:</span>
                          <span className="value amount">{formatCurrency(claim.claimAmount)}</span>
                        </div>
                        <div className="detail-item">
                          <span className="label">Incident Date:</span>
                          <span className="value">{formatDate(claim.incidentDate)}</span>
                        </div>
                        <div className="detail-item">
                          <span className="label">Filed On:</span>
                          <span className="value">{formatDate(claim.submittedDate)}</span>
                        </div>
                      </div>
                    </div>
                    <div className="claim-footer">
                      <button className="btn btn-sm btn-secondary" onClick={() => handleViewDetails(claim)}>
                        View Details
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Approved/Paid Claims */}
          {approvedClaims.length > 0 && (
            <div className="claims-section">
              <h2>Approved Claims ({approvedClaims.length})</h2>
              <div className="claims-grid">
                {approvedClaims.map(claim => (
                  <div key={claim.id} className="claim-card approved">
                    <div className="claim-header">
                      <div className="claim-status-badge">
                        <span className="status-icon">{getStatusIcon(claim.status)}</span>
                        <span className={`status-text ${getStatusClass(claim.status)}`}>
                          {getClaimStatusLabel(claim.status)}
                        </span>
                      </div>
                      <span className="claim-number">#{claim.claimNumber}</span>
                    </div>
                    <div className="claim-body">
                      <h3>{claim.policyName}</h3>
                      <p className="claim-description">{claim.description}</p>
                      
                      <div className="claim-details">
                        <div className="detail-item">
                          <span className="label">Claimed:</span>
                          <span className="value">{formatCurrency(claim.claimAmount)}</span>
                        </div>
                        <div className="detail-item">
                          <span className="label">Approved:</span>
                          <span className="value amount">{formatCurrency(claim.approvedAmount || 0)}</span>
                        </div>
                        {claim.paidAmount && (
                          <div className="detail-item">
                            <span className="label">Paid:</span>
                            <span className="value success">{formatCurrency(claim.paidAmount)}</span>
                          </div>
                        )}
                        <div className="detail-item">
                          <span className="label">Approved On:</span>
                          <span className="value">{claim.approvedDate ? formatDate(claim.approvedDate) : '-'}</span>
                        </div>
                      </div>
                    </div>
                    <div className="claim-footer">
                      <button className="btn btn-sm btn-secondary" onClick={() => handleViewDetails(claim)}>
                        View Details
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Rejected/Closed Claims */}
          {closedClaims.length > 0 && (
            <div className="claims-section">
              <h2>Closed Claims ({closedClaims.length})</h2>
              <div className="claims-list">
                {closedClaims.map(claim => (
                  <div key={claim.id} className="claim-list-item">
                    <div className="claim-info">
                      <span className="status-icon">{getStatusIcon(claim.status)}</span>
                      <div>
                        <div className="claim-title">{claim.policyName}</div>
                        <div className="claim-meta">
                          #{claim.claimNumber} • {formatDate(claim.submittedDate)}
                        </div>
                      </div>
                    </div>
                    <div className="claim-amount">{formatCurrency(claim.claimAmount)}</div>
                    <span className={`claim-status ${getStatusClass(claim.status)}`}>
                      {getClaimStatusLabel(claim.status)}
                    </span>
                    <button className="btn btn-sm btn-text" onClick={() => handleViewDetails(claim)}>
                      Details
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {claims.length === 0 && (
            <div className="empty-state">
              <div className="empty-icon">📋</div>
              <h3>No Claims Yet</h3>
              <p>File your first insurance claim when needed</p>
              {policies.length > 0 && (
                <button className="btn btn-primary" onClick={() => setShowFileClaimModal(true)}>
                  File a Claim
                </button>
              )}
              {policies.length === 0 && (
                <p className="note">You need an active insurance policy to file a claim</p>
              )}
            </div>
          )}
        </>
      )}

      {/* File Claim Modal */}
      {showFileClaimModal && (
        <div className="modal-overlay" onClick={() => setShowFileClaimModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>File Insurance Claim</h2>
              <button className="close-btn" onClick={() => setShowFileClaimModal(false)}>×</button>
            </div>
            <form onSubmit={handleFileClaim}>
              <div className="form-content">
                <div className="form-group">
                  <label>Select Policy *</label>
                  <select
                    value={claimForm.policyId}
                    onChange={(e) => setClaimForm({ ...claimForm, policyId: Number(e.target.value) })}
                    required
                  >
                    <option value={0}>-- Select a Policy --</option>
                    {policies.map(policy => (
                      <option key={policy.id} value={policy.id}>
                        {policy.policyName} (#{policy.policyNumber}) - {getInsuranceTypeLabel(policy.insuranceType)}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Claim Amount *</label>
                    <input
                      type="number"
                      value={claimForm.claimAmount || ''}
                      onChange={(e) => setClaimForm({ ...claimForm, claimAmount: Number(e.target.value) })}
                      placeholder="Enter amount"
                      min="1"
                      step="0.01"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label>Incident Date *</label>
                    <input
                      type="date"
                      value={claimForm.incidentDate}
                      onChange={(e) => setClaimForm({ ...claimForm, incidentDate: e.target.value })}
                      max={new Date().toISOString().split('T')[0]}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Description *</label>
                  <textarea
                    value={claimForm.description}
                    onChange={(e) => setClaimForm({ ...claimForm, description: e.target.value })}
                    placeholder="Describe the incident and reason for claim..."
                    rows={4}
                    required
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Hospital Name</label>
                    <input
                      type="text"
                      value={claimForm.hospitalName}
                      onChange={(e) => setClaimForm({ ...claimForm, hospitalName: e.target.value })}
                      placeholder="For medical claims"
                    />
                  </div>

                  <div className="form-group">
                    <label>Doctor Name</label>
                    <input
                      type="text"
                      value={claimForm.doctorName}
                      onChange={(e) => setClaimForm({ ...claimForm, doctorName: e.target.value })}
                      placeholder="For medical claims"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Documents Submitted</label>
                  <input
                    type="text"
                    value={claimForm.documentsSubmitted}
                    onChange={(e) => setClaimForm({ ...claimForm, documentsSubmitted: e.target.value })}
                    placeholder="e.g., Medical reports, Bills, Police report (comma separated)"
                  />
                </div>

                <div className="info-box">
                  <strong>Note:</strong> Please ensure all required documents are ready before filing the claim. 
                  Claims are typically processed within 7-15 business days.
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowFileClaimModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  File Claim
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Claim Details Modal */}
      {showClaimDetailsModal && selectedClaim && (
        <div className="modal-overlay" onClick={() => setShowClaimDetailsModal(false)}>
          <div className="modal-content large" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Claim Details</h2>
              <button className="close-btn" onClick={() => setShowClaimDetailsModal(false)}>×</button>
            </div>
            <div className="details-content">
              <div className="details-section">
                <div className="section-header">
                  <h3>Basic Information</h3>
                  <span className={`status-badge ${getStatusClass(selectedClaim.status)}`}>
                    {getStatusIcon(selectedClaim.status)} {getClaimStatusLabel(selectedClaim.status)}
                  </span>
                </div>
                <div className="details-grid">
                  <div className="detail-item">
                    <span className="label">Claim Number:</span>
                    <span className="value">{selectedClaim.claimNumber}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Policy:</span>
                    <span className="value">{selectedClaim.policyName} (#{selectedClaim.policyNumber})</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Claim Amount:</span>
                    <span className="value amount">{formatCurrency(selectedClaim.claimAmount)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Incident Date:</span>
                    <span className="value">{formatDate(selectedClaim.incidentDate)}</span>
                  </div>
                </div>
              </div>

              <div className="details-section">
                <h3>Description</h3>
                <p className="description-text">{selectedClaim.description}</p>
              </div>

              {(selectedClaim.hospitalName || selectedClaim.doctorName) && (
                <div className="details-section">
                  <h3>Medical Information</h3>
                  <div className="details-grid">
                    {selectedClaim.hospitalName && (
                      <div className="detail-item">
                        <span className="label">Hospital:</span>
                        <span className="value">{selectedClaim.hospitalName}</span>
                      </div>
                    )}
                    {selectedClaim.doctorName && (
                      <div className="detail-item">
                        <span className="label">Doctor:</span>
                        <span className="value">{selectedClaim.doctorName}</span>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {selectedClaim.documentsSubmitted && (
                <div className="details-section">
                  <h3>Documents Submitted</h3>
                  <p>{selectedClaim.documentsSubmitted}</p>
                </div>
              )}

              <div className="details-section">
                <h3>Timeline</h3>
                <div className="timeline">
                  <div className="timeline-item completed">
                    <div className="timeline-marker">✓</div>
                    <div className="timeline-content">
                      <div className="timeline-title">Claim Submitted</div>
                      <div className="timeline-date">{formatDate(selectedClaim.submittedDate)}</div>
                    </div>
                  </div>
                  
                  {selectedClaim.reviewedDate && (
                    <div className="timeline-item completed">
                      <div className="timeline-marker">✓</div>
                      <div className="timeline-content">
                        <div className="timeline-title">Under Review</div>
                        <div className="timeline-date">{formatDate(selectedClaim.reviewedDate)}</div>
                      </div>
                    </div>
                  )}

                  {selectedClaim.approvedDate && (
                    <div className="timeline-item completed">
                      <div className="timeline-marker">✓</div>
                      <div className="timeline-content">
                        <div className="timeline-title">Claim Approved</div>
                        <div className="timeline-date">{formatDate(selectedClaim.approvedDate)}</div>
                        <div className="timeline-amount">{formatCurrency(selectedClaim.approvedAmount || 0)}</div>
                      </div>
                    </div>
                  )}

                  {selectedClaim.paymentDate && (
                    <div className="timeline-item completed">
                      <div className="timeline-marker">✓</div>
                      <div className="timeline-content">
                        <div className="timeline-title">Payment Made</div>
                        <div className="timeline-date">{formatDate(selectedClaim.paymentDate)}</div>
                        <div className="timeline-amount">{formatCurrency(selectedClaim.paidAmount || 0)}</div>
                      </div>
                    </div>
                  )}
                </div>
              </div>

              {selectedClaim.rejectionReason && (
                <div className="details-section rejection">
                  <h3>Rejection Reason</h3>
                  <p className="rejection-text">{selectedClaim.rejectionReason}</p>
                </div>
              )}

              {selectedClaim.reviewerRemarks && (
                <div className="details-section">
                  <h3>Reviewer Remarks</h3>
                  <p>{selectedClaim.reviewerRemarks}</p>
                </div>
              )}
            </div>
            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setShowClaimDetailsModal(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ClaimsPage;
