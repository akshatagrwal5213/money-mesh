import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/credit';

// Types and Interfaces
export enum CreditScoreCategory {
  POOR = 'POOR',
  FAIR = 'FAIR',
  GOOD = 'GOOD',
  VERY_GOOD = 'VERY_GOOD',
  EXCELLENT = 'EXCELLENT'
}

export enum EligibilityStatus {
  ELIGIBLE = 'ELIGIBLE',
  CONDITIONALLY_ELIGIBLE = 'CONDITIONALLY_ELIGIBLE',
  NOT_ELIGIBLE = 'NOT_ELIGIBLE'
}

export enum RecommendationPriority {
  CRITICAL = 'CRITICAL',
  HIGH = 'HIGH',
  MEDIUM = 'MEDIUM',
  LOW = 'LOW'
}

export enum LoanType {
  PERSONAL = 'PERSONAL',
  HOME = 'HOME',
  CAR = 'CAR',
  EDUCATION = 'EDUCATION',
  BUSINESS = 'BUSINESS',
  GOLD = 'GOLD',
  AGRICULTURE = 'AGRICULTURE'
}

export interface CreditScoreResponse {
  id: number;
  score: number;
  category: CreditScoreCategory;
  calculationDate: string;
  paymentHistoryScore: number;
  creditUtilizationScore: number;
  creditHistoryLengthScore: number;
  creditMixScore: number;
  recentInquiriesScore: number;
  onTimePaymentPercentage: number;
  creditUtilizationRatio: number;
  oldestAccountAgeMonths: number;
  numberOfActiveAccounts: number;
  hardInquiriesLast6Months: number;
  previousScore?: number;
  scoreChange?: number;
  trend?: string;
  improvementSuggestions?: string[];
  factorImpact?: { [key: string]: number };
}

export interface LoanEligibilityRequest {
  customerId: number;
  loanType: LoanType;
  requestedAmount: number;
  requestedTenureMonths?: number;
  monthlyIncome?: number;
}

export interface LoanEligibilityResponse {
  loanType: LoanType;
  eligibilityStatus: EligibilityStatus;
  eligible: boolean;
  requestedAmount: number;
  maxEligibleAmount: number;
  interestRate: number;
  maxTenureMonths: number;
  recommendedTenureMonths: number;
  monthlyEmiEstimate: number;
  totalInterestPayable: number;
  totalAmountPayable: number;
  creditScoreAtCheck: number;
  debtToIncomeRatio: number;
  eligibilityReasons: string[];
  conditions: string[];
  suggestions: string[];
  checkDate: string;
  expiryDate: string;
}

export interface CreditDisputeRequest {
  customerId: number;
  accountReference: string;
  reason: string;
  supportingDocuments?: string;
}

export interface Recommendation {
  title: string;
  description: string;
  priority: RecommendationPriority;
  estimatedImpact: number;
  actionSteps: string;
  timeframe: string;
}

export interface CreditImprovementPlan {
  currentScore: number;
  targetScore: number;
  potentialIncrease: number;
  estimatedTimeframe: string;
  recommendations: Recommendation[];
}

// API Functions
export const calculateCreditScore = async (customerId: number): Promise<CreditScoreResponse> => {
  const response = await axios.post(`${API_BASE_URL}/score/${customerId}`);
  return response.data;
};

export const getCreditScoreHistory = async (customerId: number, months: number = 6): Promise<CreditScoreResponse[]> => {
  const response = await axios.get(`${API_BASE_URL}/score/history/${customerId}`, {
    params: { months }
  });
  return response.data;
};

export const checkLoanEligibility = async (request: LoanEligibilityRequest): Promise<LoanEligibilityResponse> => {
  const response = await axios.post(`${API_BASE_URL}/eligibility`, request);
  return response.data;
};

export const getPrequalifiedOffers = async (customerId: number): Promise<LoanEligibilityResponse[]> => {
  const response = await axios.get(`${API_BASE_URL}/prequalified/${customerId}`);
  return response.data;
};

export const fileDispute = async (request: CreditDisputeRequest): Promise<any> => {
  const response = await axios.post(`${API_BASE_URL}/dispute`, request);
  return response.data;
};

export const getImprovementPlan = async (customerId: number, targetScore?: number): Promise<CreditImprovementPlan> => {
  const params = targetScore ? { targetScore } : {};
  const response = await axios.get(`${API_BASE_URL}/improvement-plan/${customerId}`, { params });
  return response.data;
};

export const simulateScoreImprovement = async (customerId: number, targetScore: number): Promise<CreditImprovementPlan> => {
  const response = await axios.post(`${API_BASE_URL}/simulate/${customerId}`, null, {
    params: { targetScore }
  });
  return response.data;
};

// Helper Functions
export const formatScore = (score: number): string => {
  return score.toString();
};

export const getScoreColor = (score: number): string => {
  if (score >= 800) return '#22c55e'; // green
  if (score >= 750) return '#84cc16'; // lime
  if (score >= 650) return '#eab308'; // yellow
  if (score >= 550) return '#f97316'; // orange
  return '#ef4444'; // red
};

export const getCategoryLabel = (category: CreditScoreCategory): string => {
  const labels: { [key in CreditScoreCategory]: string } = {
    [CreditScoreCategory.EXCELLENT]: 'Excellent',
    [CreditScoreCategory.VERY_GOOD]: 'Very Good',
    [CreditScoreCategory.GOOD]: 'Good',
    [CreditScoreCategory.FAIR]: 'Fair',
    [CreditScoreCategory.POOR]: 'Poor'
  };
  return labels[category];
};

export const getCategoryColor = (category: CreditScoreCategory): string => {
  const colors: { [key in CreditScoreCategory]: string } = {
    [CreditScoreCategory.EXCELLENT]: '#22c55e',
    [CreditScoreCategory.VERY_GOOD]: '#84cc16',
    [CreditScoreCategory.GOOD]: '#eab308',
    [CreditScoreCategory.FAIR]: '#f97316',
    [CreditScoreCategory.POOR]: '#ef4444'
  };
  return colors[category];
};

export const calculateScorePercentage = (score: number): number => {
  return ((score - 300) / 600) * 100; // 300-900 range
};

export const getEligibilityStatusColor = (status: EligibilityStatus): string => {
  const colors: { [key in EligibilityStatus]: string } = {
    [EligibilityStatus.ELIGIBLE]: '#22c55e',
    [EligibilityStatus.CONDITIONALLY_ELIGIBLE]: '#eab308',
    [EligibilityStatus.NOT_ELIGIBLE]: '#ef4444'
  };
  return colors[status];
};

export const getEligibilityStatusLabel = (status: EligibilityStatus): string => {
  const labels: { [key in EligibilityStatus]: string } = {
    [EligibilityStatus.ELIGIBLE]: 'Eligible',
    [EligibilityStatus.CONDITIONALLY_ELIGIBLE]: 'Conditionally Eligible',
    [EligibilityStatus.NOT_ELIGIBLE]: 'Not Eligible'
  };
  return labels[status];
};

export const getPriorityColor = (priority: RecommendationPriority): string => {
  const colors: { [key in RecommendationPriority]: string } = {
    [RecommendationPriority.CRITICAL]: '#ef4444',
    [RecommendationPriority.HIGH]: '#f97316',
    [RecommendationPriority.MEDIUM]: '#eab308',
    [RecommendationPriority.LOW]: '#22c55e'
  };
  return colors[priority];
};

export const getLoanTypeLabel = (loanType: LoanType): string => {
  const labels: { [key in LoanType]: string } = {
    [LoanType.PERSONAL]: 'Personal Loan',
    [LoanType.HOME]: 'Home Loan',
    [LoanType.CAR]: 'Car Loan',
    [LoanType.EDUCATION]: 'Education Loan',
    [LoanType.BUSINESS]: 'Business Loan',
    [LoanType.GOLD]: 'Gold Loan',
    [LoanType.AGRICULTURE]: 'Agriculture Loan'
  };
  return labels[loanType];
};

export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(amount);
};

export const formatPercentage = (value: number): string => {
  return `${value.toFixed(1)}%`;
};

export const calculateEMI = (principal: number, annualRate: number, tenureMonths: number): number => {
  const monthlyRate = annualRate / 12 / 100;
  const emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenureMonths) / 
              (Math.pow(1 + monthlyRate, tenureMonths) - 1);
  return Math.round(emi * 100) / 100;
};

export const getTrendIcon = (trend: string): string => {
  if (trend === 'IMPROVING') return '📈';
  if (trend === 'DECLINING') return '📉';
  if (trend === 'STABLE') return '➡️';
  return '🆕';
};

export const getTrendLabel = (trend: string): string => {
  const labels: { [key: string]: string } = {
    'IMPROVING': 'Improving',
    'DECLINING': 'Declining',
    'STABLE': 'Stable',
    'NEW': 'New Score'
  };
  return labels[trend] || trend;
};
