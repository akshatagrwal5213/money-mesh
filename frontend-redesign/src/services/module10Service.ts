import axios from 'axios';

const API_BASE_URL = '/api/wealth';

// ==================== TypeScript Interfaces ====================

export interface WealthProfile {
  id?: number;
  riskProfile: 'CONSERVATIVE' | 'MODERATE' | 'AGGRESSIVE' | 'VERY_AGGRESSIVE';
  age: number;
  retirementAge: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  emergencyFundMonths: number;
  rebalanceStrategy: 'MONTHLY' | 'QUARTERLY' | 'SEMI_ANNUALLY' | 'ANNUALLY' | 'THRESHOLD_BASED';
  targetEquityPercentage: number;
  targetDebtPercentage: number;
  targetGoldPercentage: number;
  targetCashPercentage: number;
  targetRealEstatePercentage: number;
  targetAlternativePercentage: number;
  profileCreatedDate?: string;
  lastUpdatedDate?: string;
}

export interface AssetAllocation {
  equityValue?: number;
  debtValue?: number;
  goldValue?: number;
  cashValue?: number;
  realEstateValue?: number;
  alternativeValue?: number;
  equityPercentage: number;
  debtPercentage: number;
  goldPercentage: number;
  cashPercentage: number;
  realEstatePercentage: number;
  alternativePercentage: number;
}

export interface RebalanceRecommendation {
  assetClass: string;
  action: string;
  currentPercentage: number;
  targetPercentage: number;
  amountToAdjust: number;
}

export interface PortfolioAnalysis {
  analysisDate: string;
  totalNetWorth: number;
  diversificationScore: number;
  riskScore: number;
  needsRebalancing: boolean;
  deviationFromTarget: number;
  currentAllocation: AssetAllocation;
  targetAllocation?: AssetAllocation;
  rebalancingRecommendations: RebalanceRecommendation[];
}

export interface RetirementPlanRequest {
  currentAge: number;
  retirementAge: number;
  currentSavings: number;
  monthlyInvestment: number;
  expectedReturn: number;
  inflationRate: number;
  desiredMonthlyIncome: number;
  lifeExpectancy: number;
}

export interface RetirementPlan {
  currentAge: number;
  retirementAge: number;
  yearsToRetirement: number;
  projectedCorpus: number;
  corpusRequired: number;
  onTrack: boolean;
  shortfall: number;
  surplus: number;
  recommendedMonthlySip: number;
  recommendations: string[];
}

export interface FinancialHealth {
  scoreDate: string;
  overallScore: number;
  category: 'EXCELLENT' | 'GOOD' | 'FAIR' | 'POOR' | 'CRITICAL';
  savingsScore: number;
  debtScore: number;
  emergencyFundScore: number;
  investmentScore: number;
  insuranceScore: number;
  retirementScore: number;
  savingsRate: number;
  debtToIncomeRatio: number;
  emergencyFundMonths: number;
  investmentDiversity: number;
  improvementRecommendations: string[];
  previousOverallScore?: number;
  scoreImprovement?: number;
}

export interface InvestmentRecommendation {
  id: number;
  recommendationType: string;
  title: string;
  description: string;
  suggestedAssetClass?: string;
  suggestedAmount?: number;
  productName?: string;
  priority: number;
  potentialImpact: number;
  isActive: boolean;
  isImplemented: boolean;
  generatedDate: string;
  expiryDate?: string;
  reasoning?: string;
  actionItems?: string;
}

export interface WealthDashboard {
  profile: WealthProfile | null;
  portfolio: PortfolioAnalysis | null;
  health: FinancialHealth | null;
  recommendations: InvestmentRecommendation[];
  profileMessage?: string;
  healthMessage?: string;
}

// ==================== API Functions ====================

// Wealth Profile
export const createOrUpdateWealthProfile = async (
  profile: WealthProfile
): Promise<WealthProfile> => {
  const response = await axios.post(`${API_BASE_URL}/profile`, profile);
  return response.data;
};

export const getWealthProfile = async (): Promise<WealthProfile> => {
  const response = await axios.get(`${API_BASE_URL}/profile`);
  return response.data;
};

// Portfolio Analysis
export const analyzePortfolio = async (): Promise<PortfolioAnalysis> => {
  const response = await axios.get(`${API_BASE_URL}/portfolio-analysis`);
  return response.data;
};

export const getNetWorthSummary = async (): Promise<{
  totalNetWorth: number;
  currentAllocation: AssetAllocation;
  targetAllocation?: AssetAllocation;
  diversificationScore: number;
  riskScore: number;
  needsRebalancing: boolean;
}> => {
  const response = await axios.get(`${API_BASE_URL}/net-worth`);
  return response.data;
};

// Retirement Planning
export const createRetirementPlan = async (
  request: RetirementPlanRequest
): Promise<RetirementPlan> => {
  const response = await axios.post(`${API_BASE_URL}/retirement-plan`, request);
  return response.data;
};

export const getLatestRetirementPlan = async (): Promise<RetirementPlan> => {
  const response = await axios.get(`${API_BASE_URL}/retirement-plan`);
  return response.data.plan;
};

// Financial Health
export const calculateFinancialHealth = async (): Promise<FinancialHealth> => {
  const response = await axios.get(`${API_BASE_URL}/financial-health`);
  return response.data;
};

// Investment Recommendations
export const getRecommendations = async (): Promise<InvestmentRecommendation[]> => {
  const response = await axios.get(`${API_BASE_URL}/recommendations`);
  return response.data;
};

// Dashboard
export const getWealthDashboard = async (): Promise<WealthDashboard> => {
  const response = await axios.get(`${API_BASE_URL}/dashboard`);
  return response.data;
};

// ==================== Helper Functions ====================

export const formatCurrency = (amount: number | undefined): string => {
  if (amount === undefined || amount === null) return '₹0';
  return `₹${amount.toLocaleString('en-IN', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 0
  })}`;
};

export const formatCurrencyWithDecimals = (amount: number | undefined): string => {
  if (amount === undefined || amount === null) return '₹0.00';
  return `₹${amount.toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })}`;
};

export const formatPercentage = (value: number | undefined): string => {
  if (value === undefined || value === null) return '0%';
  return `${value.toFixed(1)}%`;
};

export const formatScore = (score: number | undefined): string => {
  if (score === undefined || score === null) return '0';
  return score.toFixed(0);
};

export const getScoreColor = (score: number): string => {
  if (score >= 80) return '#10b981'; // green
  if (score >= 60) return '#3b82f6'; // blue
  if (score >= 40) return '#f59e0b'; // yellow
  if (score >= 20) return '#f97316'; // orange
  return '#ef4444'; // red
};

export const getCategoryColor = (category: string): string => {
  switch (category) {
    case 'EXCELLENT':
      return '#10b981'; // green
    case 'GOOD':
      return '#3b82f6'; // blue
    case 'FAIR':
      return '#f59e0b'; // yellow
    case 'POOR':
      return '#f97316'; // orange
    case 'CRITICAL':
      return '#ef4444'; // red
    default:
      return '#6b7280'; // gray
  }
};

export const getCategoryLabel = (category: string): string => {
  return category.charAt(0).toUpperCase() + category.slice(1).toLowerCase();
};

export const getRiskProfileLabel = (riskProfile: string): string => {
  switch (riskProfile) {
    case 'CONSERVATIVE':
      return 'Conservative';
    case 'MODERATE':
      return 'Moderate';
    case 'AGGRESSIVE':
      return 'Aggressive';
    case 'VERY_AGGRESSIVE':
      return 'Very Aggressive';
    default:
      return riskProfile;
  }
};

export const getRebalanceStrategyLabel = (strategy: string): string => {
  switch (strategy) {
    case 'MONTHLY':
      return 'Monthly';
    case 'QUARTERLY':
      return 'Quarterly';
    case 'SEMI_ANNUALLY':
      return 'Semi-Annually';
    case 'ANNUALLY':
      return 'Annually';
    case 'THRESHOLD_BASED':
      return 'Threshold Based';
    default:
      return strategy;
  }
};

export const getAssetClassColor = (assetClass: string): string => {
  switch (assetClass) {
    case 'EQUITY':
      return '#3b82f6'; // blue
    case 'DEBT':
      return '#10b981'; // green
    case 'GOLD':
      return '#f59e0b'; // yellow
    case 'CASH':
      return '#6b7280'; // gray
    case 'REAL_ESTATE':
      return '#8b5cf6'; // purple
    case 'ALTERNATIVE':
      return '#ec4899'; // pink
    default:
      return '#6b7280';
  }
};

export const getPriorityLabel = (priority: number): string => {
  switch (priority) {
    case 1:
      return 'Critical';
    case 2:
      return 'High';
    case 3:
      return 'Medium';
    case 4:
      return 'Low';
    case 5:
      return 'Optional';
    default:
      return 'Unknown';
  }
};

export const getPriorityColor = (priority: number): string => {
  switch (priority) {
    case 1:
      return '#ef4444'; // red
    case 2:
      return '#f97316'; // orange
    case 3:
      return '#f59e0b'; // yellow
    case 4:
      return '#3b82f6'; // blue
    case 5:
      return '#6b7280'; // gray
    default:
      return '#6b7280';
  }
};

export const calculateSavingsRate = (income: number, expenses: number): number => {
  if (income <= 0) return 0;
  return ((income - expenses) / income) * 100;
};

export const calculateYearsToRetirement = (currentAge: number, retirementAge: number): number => {
  return Math.max(0, retirementAge - currentAge);
};

export const calculateRetirementReadiness = (projected: number, required: number): number => {
  if (required <= 0) return 100;
  return Math.min(100, (projected / required) * 100);
};
