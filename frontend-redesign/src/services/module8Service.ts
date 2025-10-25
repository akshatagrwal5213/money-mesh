import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/insurance';

// ===== TypeScript Interfaces =====

export enum InsuranceType {
  LIFE = 'LIFE',
  HEALTH = 'HEALTH',
  TERM = 'TERM',
  AUTO = 'AUTO',
  HOME = 'HOME',
  TRAVEL = 'TRAVEL',
  ACCIDENT = 'ACCIDENT',
  CRITICAL_ILLNESS = 'CRITICAL_ILLNESS',
  DISABILITY = 'DISABILITY',
  LOAN_PROTECTION = 'LOAN_PROTECTION'
}

export enum InsurancePolicyStatus {
  PENDING_APPROVAL = 'PENDING_APPROVAL',
  ACTIVE = 'ACTIVE',
  PENDING_PAYMENT = 'PENDING_PAYMENT',
  LAPSED = 'LAPSED',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
  REJECTED = 'REJECTED',
  MATURED = 'MATURED'
}

export enum ClaimStatus {
  SUBMITTED = 'SUBMITTED',
  UNDER_REVIEW = 'UNDER_REVIEW',
  PENDING_DOCUMENTS = 'PENDING_DOCUMENTS',
  APPROVED = 'APPROVED',
  REJECTED = 'REJECTED',
  PAID = 'PAID',
  CLOSED = 'CLOSED'
}

export enum PremiumFrequency {
  MONTHLY = 'MONTHLY',
  QUARTERLY = 'QUARTERLY',
  HALF_YEARLY = 'HALF_YEARLY',
  YEARLY = 'YEARLY',
  ONE_TIME = 'ONE_TIME'
}

export interface PolicyApplicationRequest {
  insuranceType: InsuranceType;
  policyName: string;
  coverageAmount: number;
  termYears: number;
  premiumFrequency: PremiumFrequency;
  nominee: string;
  nomineeRelation: string;
  nomineePercentage?: number;
  remarks?: string;
}

export interface PolicyDetailsResponse {
  id: number;
  policyNumber: string;
  insuranceType: InsuranceType;
  policyName: string;
  coverageAmount: number;
  premiumAmount: number;
  premiumFrequency: PremiumFrequency;
  startDate: string;
  endDate: string;
  nextPremiumDueDate: string | null;
  termYears: number;
  status: InsurancePolicyStatus;
  nominee: string;
  nomineeRelation: string;
  nomineePercentage: number;
  applicationDate: string;
  approvalDate: string | null;
  rejectionReason: string | null;
  remarks: string | null;
  customerId: number;
  customerName: string;
}

export interface PremiumPaymentRequest {
  policyId: number;
  amount: number;
  paymentMethod: string; // ACCOUNT_DEBIT, CARD, UPI, NETBANKING
  accountNumber?: string;
  remarks?: string;
}

export interface ClaimRequest {
  policyId: number;
  claimAmount: number;
  incidentDate: string;
  description: string;
  hospitalName?: string;
  doctorName?: string;
  documentsSubmitted?: string;
}

export interface ClaimDetailsResponse {
  id: number;
  claimNumber: string;
  policyId: number;
  policyNumber: string;
  policyName: string;
  claimAmount: number;
  incidentDate: string;
  description: string;
  hospitalName: string | null;
  doctorName: string | null;
  documentsSubmitted: string | null;
  status: ClaimStatus;
  approvedAmount: number | null;
  paidAmount: number | null;
  submittedDate: string;
  reviewedDate: string | null;
  approvedDate: string | null;
  paymentDate: string | null;
  rejectionReason: string | null;
  reviewerRemarks: string | null;
}

export interface InsurancePremiumPayment {
  id: number;
  paymentReference: string;
  amount: number;
  paymentDate: string;
  periodStartDate: string;
  periodEndDate: string;
  paymentMethod: string;
  transactionId: string;
  status: string;
  remarks: string | null;
}

// ===== API Helper Function =====
const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  };
};

// ===== Policy Management Functions =====

export const applyForPolicy = async (request: PolicyApplicationRequest): Promise<PolicyDetailsResponse> => {
  const response = await axios.post(`${API_BASE_URL}/apply`, request, getAuthHeaders());
  return response.data;
};

export const getAllPolicies = async (): Promise<PolicyDetailsResponse[]> => {
  const response = await axios.get(`${API_BASE_URL}/policies`, getAuthHeaders());
  return response.data;
};

export const getPolicyById = async (policyId: number): Promise<PolicyDetailsResponse> => {
  const response = await axios.get(`${API_BASE_URL}/policies/${policyId}`, getAuthHeaders());
  return response.data;
};

export const cancelPolicy = async (policyId: number, reason: string): Promise<PolicyDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/policies/${policyId}/cancel`,
    { reason },
    getAuthHeaders()
  );
  return response.data;
};

// ===== Premium Payment Functions =====

export const payPremium = async (request: PremiumPaymentRequest): Promise<{ status: string; paymentReference: string; message: string }> => {
  const response = await axios.post(`${API_BASE_URL}/premium/pay`, request, getAuthHeaders());
  return response.data;
};

export const getPremiumHistory = async (policyId: number): Promise<InsurancePremiumPayment[]> => {
  const response = await axios.get(`${API_BASE_URL}/premium/history/${policyId}`, getAuthHeaders());
  return response.data;
};

// ===== Claims Management Functions =====

export const fileClaim = async (request: ClaimRequest): Promise<ClaimDetailsResponse> => {
  const response = await axios.post(`${API_BASE_URL}/claims/file`, request, getAuthHeaders());
  return response.data;
};

export const getAllClaims = async (): Promise<ClaimDetailsResponse[]> => {
  const response = await axios.get(`${API_BASE_URL}/claims`, getAuthHeaders());
  return response.data;
};

export const getClaimsByPolicy = async (policyId: number): Promise<ClaimDetailsResponse[]> => {
  const response = await axios.get(`${API_BASE_URL}/claims/policy/${policyId}`, getAuthHeaders());
  return response.data;
};

// ===== Admin Functions =====

export const approvePolicy = async (policyId: number): Promise<PolicyDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/admin/policies/${policyId}/approve`,
    {},
    getAuthHeaders()
  );
  return response.data;
};

export const rejectPolicy = async (policyId: number, reason: string): Promise<PolicyDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/admin/policies/${policyId}/reject`,
    { reason },
    getAuthHeaders()
  );
  return response.data;
};

export const approveClaim = async (claimId: number, approvedAmount: number, remarks: string): Promise<ClaimDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/admin/claims/${claimId}/approve`,
    { approvedAmount, remarks },
    getAuthHeaders()
  );
  return response.data;
};

export const rejectClaim = async (claimId: number, reason: string): Promise<ClaimDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/admin/claims/${claimId}/reject`,
    { reason },
    getAuthHeaders()
  );
  return response.data;
};

export const payClaim = async (claimId: number): Promise<ClaimDetailsResponse> => {
  const response = await axios.post(
    `${API_BASE_URL}/admin/claims/${claimId}/pay`,
    {},
    getAuthHeaders()
  );
  return response.data;
};

// ===== Helper Functions =====

export const getInsuranceTypeLabel = (type: InsuranceType): string => {
  const labels: Record<InsuranceType, string> = {
    LIFE: 'Life Insurance',
    HEALTH: 'Health Insurance',
    TERM: 'Term Insurance',
    AUTO: 'Auto/Vehicle Insurance',
    HOME: 'Home Insurance',
    TRAVEL: 'Travel Insurance',
    ACCIDENT: 'Personal Accident Insurance',
    CRITICAL_ILLNESS: 'Critical Illness Insurance',
    DISABILITY: 'Disability Insurance',
    LOAN_PROTECTION: 'Loan Protection Insurance'
  };
  return labels[type];
};

export const getPolicyStatusLabel = (status: InsurancePolicyStatus): string => {
  const labels: Record<InsurancePolicyStatus, string> = {
    PENDING_APPROVAL: 'Pending Approval',
    ACTIVE: 'Active',
    PENDING_PAYMENT: 'Pending Payment',
    LAPSED: 'Lapsed',
    EXPIRED: 'Expired',
    CANCELLED: 'Cancelled',
    REJECTED: 'Rejected',
    MATURED: 'Matured'
  };
  return labels[status];
};

export const getClaimStatusLabel = (status: ClaimStatus): string => {
  const labels: Record<ClaimStatus, string> = {
    SUBMITTED: 'Submitted',
    UNDER_REVIEW: 'Under Review',
    PENDING_DOCUMENTS: 'Pending Documents',
    APPROVED: 'Approved',
    REJECTED: 'Rejected',
    PAID: 'Paid',
    CLOSED: 'Closed'
  };
  return labels[status];
};

export const getPremiumFrequencyLabel = (frequency: PremiumFrequency): string => {
  const labels: Record<PremiumFrequency, string> = {
    MONTHLY: 'Monthly',
    QUARTERLY: 'Quarterly',
    HALF_YEARLY: 'Half-Yearly',
    YEARLY: 'Yearly',
    ONE_TIME: 'One-Time'
  };
  return labels[frequency];
};

export const calculateDaysUntilDue = (dueDate: string | null): number => {
  if (!dueDate) return 0;
  const today = new Date();
  const due = new Date(dueDate);
  const diffTime = due.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  return diffDays;
};

export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(amount);
};

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
};
