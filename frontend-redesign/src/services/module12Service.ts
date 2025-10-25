import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// ========================= TypeScript Interfaces =========================

// -------------------- Loan Management DTOs --------------------

export interface EmiScheduleDto {
  id: number;
  loanId: number;
  emiNumber: number;
  dueDate: string; // LocalDate
  emiAmount: number;
  principalComponent: number;
  interestComponent: number;
  outstandingBalance: number;
  isPaid: boolean;
  paidDate?: string; // LocalDate
  paymentReference?: string;
  daysOverdue: number;
  penaltyAmount: number;
  status: 'UPCOMING' | 'DUE' | 'PAID' | 'OVERDUE';
}

export interface PrepaymentRequest {
  loanId: number;
  customerId: number;
  prepaymentType: 'PARTIAL' | 'FULL';
  amount: number;
  paymentReference: string;
}

export interface PrepaymentResponse {
  id: number;
  loanId: number;
  customerId: number;
  prepaymentType: 'PARTIAL' | 'FULL';
  amount: number;
  prepaymentDate: string; // LocalDate
  interestSaved: number;
  tenureReduced: number;
  outstandingBeforePrepayment: number;
  outstandingAfterPrepayment: number;
  prepaymentCharges: number;
  paymentReference: string;
}

export interface RestructureRequest {
  loanId: number;
  customerId: number;
  reason: 'FINANCIAL_HARDSHIP' | 'REDUCE_EMI' | 'SHORTEN_TENURE' | 'RATE_CHANGE' | 'EMERGENCY' | 'OTHER';
  requestedTenure?: number;
  requestedEmi?: number;
  requestedInterestRate?: number;
  remarks?: string;
}

export interface RestructureResponse {
  id: number;
  loanId: number;
  customerId: number;
  reason: string;
  requestDate: string;
  approvalDate?: string;
  implementationDate?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'IMPLEMENTED';
  originalPrincipal: number;
  originalTenure: number;
  originalInterestRate: number;
  originalEmi: number;
  newTenure: number;
  newInterestRate: number;
  newEmi: number;
  additionalInterest: number;
  restructuringCharges: number;
  remarks?: string;
  approvedBy?: string;
}

export interface ForeclosureRequest {
  loanId: number;
  customerId: number;
  requestedDate: string; // LocalDate
  paymentReference: string;
}

export interface ForeclosureResponse {
  id: number;
  loanId: number;
  customerId: number;
  requestDate: string;
  foreclosureDate?: string;
  status: 'REQUESTED' | 'APPROVED' | 'PROCESSING' | 'COMPLETED' | 'REJECTED';
  outstandingPrincipal: number;
  pendingInterest: number;
  foreclosureCharges: number;
  totalAmount: number;
  interestSaved: number;
  paymentReference?: string;
}

export interface CollateralDto {
  id: number;
  loanId: number;
  collateralType: 'PROPERTY' | 'VEHICLE' | 'GOLD' | 'SECURITIES' | 'FIXED_DEPOSIT' | 'OTHER';
  description: string;
  estimatedValue: number;
  documentNumber: string;
  valuationDate: string;
  isActive: boolean;
}

export interface OverdueTrackingDto {
  id: number;
  loanId: number;
  customerId: number;
  emiId: number;
  daysOverdue: number;
  overdueAmount: number;
  penaltyAccumulated: number;
  overdueStatus: 'CURRENT' | 'OVERDUE_1_30' | 'OVERDUE_31_60' | 'OVERDUE_61_90' | 'OVERDUE_90_PLUS' | 'NPA';
  lastNotificationDate?: string;
  collectionStatus?: string;
}

// -------------------- Rewards & Loyalty DTOs --------------------

export interface RewardPointsDto {
  id: number;
  customerId: number;
  points: number;
  category: 'TRANSACTION' | 'EMI_PAYMENT' | 'LOAN_CLOSURE' | 'REFERRAL' | 'MILESTONE' | 'CASHBACK' | 'SIGNUP_BONUS' | 'BIRTHDAY_BONUS' | 'TIER_UPGRADE' | 'PROMOTIONAL';
  earnedDate: string; // LocalDate
  expiryDate: string; // LocalDate
  description: string;
  isExpired: boolean;
  isRedeemed: boolean;
  redeemedDate?: string;
  transactionReference?: string;
}

export interface TierInfoDto {
  id: number;
  customerId: number;
  currentTier: 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  totalPointsEarned: number;
  activePoints: number;
  tierSince: string; // LocalDate
  nextTierThreshold: number;
  pointsToNextTier: number;
  cashbackMultiplier: number;
  interestRateDiscount: number;
  benefits: string[];
  lastUpdated: string; // LocalDateTime
}

export interface RedemptionRequest {
  customerId: number;
  redemptionType: 'CASH' | 'BILL_PAYMENT' | 'LOAN_EMI' | 'VOUCHER' | 'DONATION' | 'PRODUCT' | 'TRAVEL' | 'STATEMENT_CREDIT';
  pointsToRedeem: number;
  accountNumber?: string;
  voucherType?: string;
  remarks?: string;
}

export interface RedemptionResponse {
  id: number;
  customerId: number;
  redemptionType: string;
  pointsRedeemed: number;
  cashValue: number;
  redemptionDate: string;
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';
  accountNumber?: string;
  voucherCode?: string;
  transactionReference?: string;
  remarks?: string;
}

export interface ReferralRequest {
  customerId: number;
  referredCustomerEmail?: string;
  referredCustomerPhone?: string;
}

export interface ReferralDto {
  id: number;
  referrerCustomerId: number;
  referralCode: string;
  referredCustomerId?: number;
  referralStatus: 'PENDING' | 'REGISTERED' | 'QUALIFIED' | 'REWARDED' | 'EXPIRED' | 'REJECTED';
  generatedDate: string;
  registrationDate?: string;
  qualificationDate?: string;
  bonusPoints: number;
  expiryDate: string;
}

export interface MilestoneDto {
  id: number;
  customerId: number;
  milestoneType: string;
  achievedDate: string;
  bonusPoints: number;
  description: string;
}

export interface CashbackDto {
  id: number;
  customerId: number;
  transactionAmount: number;
  cashbackPercentage: number;
  cashbackAmount: number;
  tierMultiplier: number;
  finalCashback: number;
  transactionDate: string;
  creditDate?: string;
  status: 'PENDING' | 'CREDITED' | 'CANCELLED';
  transactionReference: string;
}

export interface LoyaltyOfferDto {
  id: number;
  offerTitle: string;
  description: string;
  offerType: 'CASHBACK' | 'DISCOUNT' | 'BONUS_POINTS' | 'FEE_WAIVER';
  tierLevel?: 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  validFrom: string;
  validUntil: string;
  isActive: boolean;
  termsAndConditions: string;
}

// ========================= API Response Wrapper =========================

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

// ========================= Loan Management API Functions =========================

/**
 * Get or generate EMI schedule for a loan
 */
export const getEmiSchedule = async (loanId: number): Promise<EmiScheduleDto[]> => {
  try {
    const response = await axios.get<ApiResponse<EmiScheduleDto[]>>(
      `${API_BASE_URL}/loan-management/loans/${loanId}/emi-schedule`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching EMI schedule:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch EMI schedule');
  }
};

/**
 * Record an EMI payment
 */
export const recordEmiPayment = async (
  emiId: number,
  amount: number,
  paymentReference: string
): Promise<EmiScheduleDto> => {
  try {
    const response = await axios.post<ApiResponse<EmiScheduleDto>>(
      `${API_BASE_URL}/loan-management/emi/${emiId}/pay`,
      { amount, paymentReference }
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error recording EMI payment:', error);
    throw new Error(error.response?.data?.message || 'Failed to record EMI payment');
  }
};

/**
 * Calculate prepayment details (preview only, doesn't process)
 */
export const calculatePrepayment = async (
  loanId: number,
  prepaymentType: 'PARTIAL' | 'FULL',
  amount: number
): Promise<any> => {
  try {
    const response = await axios.post<ApiResponse<any>>(
      `${API_BASE_URL}/loan-management/loans/${loanId}/prepayment/calculate`,
      { prepaymentType, amount }
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error calculating prepayment:', error);
    throw new Error(error.response?.data?.message || 'Failed to calculate prepayment');
  }
};

/**
 * Process actual prepayment
 */
export const processPrepayment = async (
  request: PrepaymentRequest
): Promise<PrepaymentResponse> => {
  try {
    const response = await axios.post<ApiResponse<PrepaymentResponse>>(
      `${API_BASE_URL}/loan-management/prepayment`,
      request
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error processing prepayment:', error);
    throw new Error(error.response?.data?.message || 'Failed to process prepayment');
  }
};

/**
 * Submit loan restructuring request
 */
export const requestRestructure = async (
  request: RestructureRequest
): Promise<RestructureResponse> => {
  try {
    const response = await axios.post<ApiResponse<RestructureResponse>>(
      `${API_BASE_URL}/loan-management/restructure/request`,
      request
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error requesting restructure:', error);
    throw new Error(error.response?.data?.message || 'Failed to request restructure');
  }
};

/**
 * Approve restructuring request (Admin only)
 */
export const approveRestructure = async (
  restructureId: number,
  approvedBy: string,
  remarks?: string
): Promise<RestructureResponse> => {
  try {
    const response = await axios.post<ApiResponse<RestructureResponse>>(
      `${API_BASE_URL}/loan-management/restructure/${restructureId}/approve`,
      { approvedBy, remarks }
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error approving restructure:', error);
    throw new Error(error.response?.data?.message || 'Failed to approve restructure');
  }
};

/**
 * Implement approved restructure
 */
export const implementRestructure = async (
  restructureId: number
): Promise<RestructureResponse> => {
  try {
    const response = await axios.post<ApiResponse<RestructureResponse>>(
      `${API_BASE_URL}/loan-management/restructure/${restructureId}/implement`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error implementing restructure:', error);
    throw new Error(error.response?.data?.message || 'Failed to implement restructure');
  }
};

/**
 * Calculate foreclosure amount
 */
export const calculateForeclosure = async (loanId: number): Promise<any> => {
  try {
    const response = await axios.get<ApiResponse<any>>(
      `${API_BASE_URL}/loan-management/loans/${loanId}/foreclosure/calculate`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error calculating foreclosure:', error);
    throw new Error(error.response?.data?.message || 'Failed to calculate foreclosure');
  }
};

/**
 * Process loan foreclosure
 */
export const processForeclosure = async (
  request: ForeclosureRequest
): Promise<ForeclosureResponse> => {
  try {
    const response = await axios.post<ApiResponse<ForeclosureResponse>>(
      `${API_BASE_URL}/loan-management/foreclosure`,
      request
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error processing foreclosure:', error);
    throw new Error(error.response?.data?.message || 'Failed to process foreclosure');
  }
};

/**
 * Trigger overdue check (Admin/Scheduled job)
 */
export const checkOverdueEmis = async (): Promise<string> => {
  try {
    const response = await axios.post<ApiResponse<string>>(
      `${API_BASE_URL}/loan-management/admin/check-overdues`
    );
    return response.data.message;
  } catch (error: any) {
    console.error('Error checking overdue EMIs:', error);
    throw new Error(error.response?.data?.message || 'Failed to check overdue EMIs');
  }
};

// ========================= Rewards & Loyalty API Functions =========================

/**
 * Get customer's reward points information
 */
export const getRewardPoints = async (customerId: number): Promise<any> => {
  try {
    const response = await axios.get<ApiResponse<any>>(
      `${API_BASE_URL}/rewards/points/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching reward points:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch reward points');
  }
};

/**
 * Get customer's tier information
 */
export const getTierInfo = async (customerId: number): Promise<TierInfoDto> => {
  try {
    const response = await axios.get<ApiResponse<TierInfoDto>>(
      `${API_BASE_URL}/rewards/tier/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching tier info:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch tier info');
  }
};

/**
 * Initialize tier for new customer
 */
export const initializeTier = async (customerId: number): Promise<TierInfoDto> => {
  try {
    const response = await axios.post<ApiResponse<TierInfoDto>>(
      `${API_BASE_URL}/rewards/tier/${customerId}/initialize`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error initializing tier:', error);
    throw new Error(error.response?.data?.message || 'Failed to initialize tier');
  }
};

/**
 * Redeem reward points
 */
export const redeemPoints = async (
  customerId: number,
  request: RedemptionRequest
): Promise<RedemptionResponse> => {
  try {
    const response = await axios.post<ApiResponse<RedemptionResponse>>(
      `${API_BASE_URL}/rewards/redeem?customerId=${customerId}`,
      request
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error redeeming points:', error);
    throw new Error(error.response?.data?.message || 'Failed to redeem points');
  }
};

/**
 * Get redemption history
 */
export const getRedemptionHistory = async (customerId: number): Promise<RedemptionResponse[]> => {
  try {
    const response = await axios.get<ApiResponse<RedemptionResponse[]>>(
      `${API_BASE_URL}/rewards/redemption-history/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching redemption history:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch redemption history');
  }
};

/**
 * Generate referral code
 */
export const generateReferralCode = async (customerId: number): Promise<ReferralDto> => {
  try {
    const response = await axios.post<ApiResponse<ReferralDto>>(
      `${API_BASE_URL}/rewards/referral/generate`,
      { customerId }
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error generating referral code:', error);
    throw new Error(error.response?.data?.message || 'Failed to generate referral code');
  }
};

/**
 * Get referral history
 */
export const getReferralHistory = async (customerId: number): Promise<any> => {
  try {
    const response = await axios.get<ApiResponse<any>>(
      `${API_BASE_URL}/rewards/referrals/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching referral history:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch referral history');
  }
};

/**
 * Process referral registration
 */
export const processReferralRegistration = async (
  referralCode: string,
  newCustomerId: number
): Promise<string> => {
  try {
    const response = await axios.post<ApiResponse<string>>(
      `${API_BASE_URL}/rewards/referral/register`,
      { referralCode, newCustomerId }
    );
    return response.data.message;
  } catch (error: any) {
    console.error('Error processing referral registration:', error);
    throw new Error(error.response?.data?.message || 'Failed to process referral registration');
  }
};

/**
 * Qualify referral after first transaction
 */
export const qualifyReferral = async (customerId: number): Promise<string> => {
  try {
    const response = await axios.post<ApiResponse<string>>(
      `${API_BASE_URL}/rewards/referral/qualify/${customerId}`
    );
    return response.data.message;
  } catch (error: any) {
    console.error('Error qualifying referral:', error);
    throw new Error(error.response?.data?.message || 'Failed to qualify referral');
  }
};

/**
 * Get milestone progress
 */
export const getMilestoneProgress = async (customerId: number): Promise<any> => {
  try {
    const response = await axios.get<ApiResponse<any>>(
      `${API_BASE_URL}/rewards/milestones/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching milestone progress:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch milestone progress');
  }
};

/**
 * Get active loyalty offers
 */
export const getActiveOffers = async (customerId: number): Promise<LoyaltyOfferDto[]> => {
  try {
    const response = await axios.get<ApiResponse<LoyaltyOfferDto[]>>(
      `${API_BASE_URL}/rewards/offers/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching active offers:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch active offers');
  }
};

/**
 * Get cashback history
 */
export const getCashbackHistory = async (customerId: number): Promise<CashbackDto[]> => {
  try {
    const response = await axios.get<ApiResponse<CashbackDto[]>>(
      `${API_BASE_URL}/rewards/cashback-history/${customerId}`
    );
    return response.data.data;
  } catch (error: any) {
    console.error('Error fetching cashback history:', error);
    throw new Error(error.response?.data?.message || 'Failed to fetch cashback history');
  }
};

/**
 * Award points manually (Admin only)
 */
export const awardPointsManually = async (
  customerId: number,
  points: number,
  category: string,
  description: string
): Promise<string> => {
  try {
    const response = await axios.post<ApiResponse<string>>(
      `${API_BASE_URL}/rewards/admin/award-points`,
      { customerId, points, category, description }
    );
    return response.data.message;
  } catch (error: any) {
    console.error('Error awarding points:', error);
    throw new Error(error.response?.data?.message || 'Failed to award points');
  }
};

/**
 * Trigger points expiry (Admin/Scheduled job)
 */
export const expireOldPoints = async (): Promise<string> => {
  try {
    const response = await axios.post<ApiResponse<string>>(
      `${API_BASE_URL}/rewards/admin/expire-points`
    );
    return response.data.message;
  } catch (error: any) {
    console.error('Error expiring points:', error);
    throw new Error(error.response?.data?.message || 'Failed to expire points');
  }
};

// ========================= Helper Functions =========================

/**
 * Format currency for display
 */
export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 2,
  }).format(amount);
};

/**
 * Calculate points to cash value
 */
export const pointsToCash = (points: number): number => {
  return points * 0.25; // 1 point = ₹0.25
};

/**
 * Get tier color for UI styling
 */
export const getTierColor = (tier: string): string => {
  switch (tier) {
    case 'SILVER':
      return '#C0C0C0';
    case 'GOLD':
      return '#FFD700';
    case 'PLATINUM':
      return '#E5E4E2';
    case 'DIAMOND':
      return '#B9F2FF';
    default:
      return '#808080';
  }
};

/**
 * Get tier gradient for badges
 */
export const getTierGradient = (tier: string): string => {
  switch (tier) {
    case 'SILVER':
      return 'linear-gradient(135deg, #C0C0C0 0%, #808080 100%)';
    case 'GOLD':
      return 'linear-gradient(135deg, #FFD700 0%, #FFA500 100%)';
    case 'PLATINUM':
      return 'linear-gradient(135deg, #E5E4E2 0%, #A8A8A8 100%)';
    case 'DIAMOND':
      return 'linear-gradient(135deg, #B9F2FF 0%, #0080FF 100%)';
    default:
      return 'linear-gradient(135deg, #808080 0%, #404040 100%)';
  }
};

/**
 * Format date for display
 */
export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

/**
 * Calculate days between dates
 */
export const daysBetween = (date1: string, date2: string): number => {
  const d1 = new Date(date1);
  const d2 = new Date(date2);
  const diffTime = Math.abs(d2.getTime() - d1.getTime());
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

export default {
  // Loan Management
  getEmiSchedule,
  recordEmiPayment,
  calculatePrepayment,
  processPrepayment,
  requestRestructure,
  approveRestructure,
  implementRestructure,
  calculateForeclosure,
  processForeclosure,
  checkOverdueEmis,
  
  // Rewards & Loyalty
  getRewardPoints,
  getTierInfo,
  initializeTier,
  redeemPoints,
  getRedemptionHistory,
  generateReferralCode,
  getReferralHistory,
  processReferralRegistration,
  qualifyReferral,
  getMilestoneProgress,
  getActiveOffers,
  getCashbackHistory,
  awardPointsManually,
  expireOldPoints,
  
  // Helpers
  formatCurrency,
  pointsToCash,
  getTierColor,
  getTierGradient,
  formatDate,
  daysBetween,
};
