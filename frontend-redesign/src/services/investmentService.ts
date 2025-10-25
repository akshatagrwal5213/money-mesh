import api from './api';

// Types/Interfaces

export enum InvestmentStatus {
  ACTIVE = 'ACTIVE',
  MATURED = 'MATURED',
  CLOSED = 'CLOSED',
  PENDING = 'PENDING',
  CANCELLED = 'CANCELLED',
  SUSPENDED = 'SUSPENDED'
}

export enum MaturityAction {
  CREDIT_TO_ACCOUNT = 'CREDIT_TO_ACCOUNT',
  RENEW_PRINCIPAL = 'RENEW_PRINCIPAL',
  RENEW_PRINCIPAL_AND_INTEREST = 'RENEW_PRINCIPAL_AND_INTEREST'
}

export enum SipFrequency {
  DAILY = 'DAILY',
  WEEKLY = 'WEEKLY',
  MONTHLY = 'MONTHLY',
  QUARTERLY = 'QUARTERLY',
  YEARLY = 'YEARLY'
}

export enum RiskLevel {
  VERY_LOW = 'VERY_LOW',
  LOW = 'LOW',
  MODERATE = 'MODERATE',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH'
}

export enum FundCategory {
  EQUITY = 'EQUITY',
  DEBT = 'DEBT',
  HYBRID = 'HYBRID',
  INDEX = 'INDEX',
  LIQUID = 'LIQUID',
  ELSS = 'ELSS',
  GOLD = 'GOLD',
  INTERNATIONAL = 'INTERNATIONAL'
}

export interface FixedDeposit {
  id: number;
  fdNumber: string;
  accountNumber: string;
  principalAmount: number;
  interestRate: number;
  tenureMonths: number;
  startDate: string;
  maturityDate: string;
  maturityAmount: number;
  interestEarned: number;
  status: InvestmentStatus;
  maturityAction: MaturityAction;
  autoRenew: boolean;
  createdAt: string;
  closedAt?: string;
}

export interface RecurringDeposit {
  id: number;
  rdNumber: string;
  accountNumber: string;
  monthlyInstallment: number;
  interestRate: number;
  tenureMonths: number;
  startDate: string;
  maturityDate: string;
  totalDeposited: number;
  maturityAmount: number;
  interestEarned: number;
  installmentsPaid: number;
  lastInstallmentDate?: string;
  nextInstallmentDate: string;
  status: InvestmentStatus;
  maturityAction: MaturityAction;
  autoDebit: boolean;
  createdAt: string;
}

export interface MutualFund {
  id: number;
  fundCode: string;
  fundName: string;
  amc: string;
  category: FundCategory;
  riskLevel: RiskLevel;
  currentNav: number;
  previousNav?: number;
  aum: number;
  expenseRatio: number;
  returns1Year?: number;
  returns3Year?: number;
  returns5Year?: number;
  minInvestment: number;
  minSipAmount: number;
  sipAvailable: boolean;
  lumpsumAvailable: boolean;
  exitLoadDays?: number;
  exitLoadPercentage?: number;
  isActive: boolean;
}

export interface MutualFundHolding {
  id: number;
  folioNumber: string;
  mutualFund: MutualFund;
  units: number;
  totalInvested: number;
  averageNav: number;
  currentValue: number;
  totalGainLoss: number;
  returnPercentage: number;
  status: InvestmentStatus;
  createdAt: string;
}

export interface SipInvestment {
  id: number;
  sipNumber: string;
  accountNumber: string;
  mutualFund: MutualFund;
  installmentAmount: number;
  frequency: SipFrequency;
  startDate: string;
  endDate?: string;
  totalInstallments?: number;
  installmentsExecuted: number;
  nextInstallmentDate: string;
  lastInstallmentDate?: string;
  totalInvested: number;
  totalUnits: number;
  status: InvestmentStatus;
  autoDebit: boolean;
  createdAt: string;
  cancelledAt?: string;
}

export interface InvestmentPortfolio {
  totalInvestmentValue: number;
  totalInvested: number;
  totalGainLoss: number;
  overallReturnPercentage: number;
  fdValue: number;
  rdValue: number;
  mutualFundValue: number;
  sipValue: number;
  totalFixedDeposits: number;
  totalRecurringDeposits: number;
  totalMutualFunds: number;
  totalActiveSips: number;
  riskProfile?: RiskLevel;
}

export interface FixedDepositRequest {
  accountNumber: string;
  principalAmount: number;
  tenureMonths: number;
  maturityAction: MaturityAction;
  autoRenew: boolean;
}

export interface RecurringDepositRequest {
  accountNumber: string;
  monthlyInstallment: number;
  tenureMonths: number;
  maturityAction: MaturityAction;
  autoDebit: boolean;
}

export interface MutualFundInvestmentRequest {
  fundCode: string;
  accountNumber: string;
  amount: number;
  folioNumber?: string;
}

export interface SipRequest {
  fundCode: string;
  accountNumber: string;
  installmentAmount: number;
  frequency: SipFrequency;
  startDate: string;
  endDate?: string;
  totalInstallments?: number;
  folioNumber?: string;
  autoDebit: boolean;
}

// Fixed Deposit Operations

export const createFixedDeposit = async (request: FixedDepositRequest): Promise<FixedDeposit> => {
  const response = await api.post('/investments/fd/create', request);
  return response.data;
};

export const getMyFixedDeposits = async (): Promise<FixedDeposit[]> => {
  const response = await api.get('/investments/fd/my-deposits');
  return response.data;
};

export const getFixedDepositDetails = async (fdNumber: string): Promise<FixedDeposit> => {
  const response = await api.get(`/investments/fd/${fdNumber}`);
  return response.data;
};

export const closeFixedDeposit = async (fdNumber: string): Promise<FixedDeposit> => {
  const response = await api.post(`/investments/fd/${fdNumber}/close`);
  return response.data;
};

// Recurring Deposit Operations

export const createRecurringDeposit = async (request: RecurringDepositRequest): Promise<RecurringDeposit> => {
  const response = await api.post('/investments/rd/create', request);
  return response.data;
};

export const getMyRecurringDeposits = async (): Promise<RecurringDeposit[]> => {
  const response = await api.get('/investments/rd/my-deposits');
  return response.data;
};

export const payRDInstallment = async (rdNumber: string): Promise<RecurringDeposit> => {
  const response = await api.post(`/investments/rd/${rdNumber}/pay-installment`);
  return response.data;
};

// Mutual Fund Operations

export const getAllMutualFunds = async (): Promise<MutualFund[]> => {
  const response = await api.get('/investments/mutual-funds');
  return response.data;
};

export const getMutualFundDetails = async (fundCode: string): Promise<MutualFund> => {
  const response = await api.get(`/investments/mutual-funds/${fundCode}`);
  return response.data;
};

export const investInMutualFund = async (request: MutualFundInvestmentRequest): Promise<MutualFundHolding> => {
  const response = await api.post('/investments/mutual-funds/invest', request);
  return response.data;
};

export const getMyMutualFundHoldings = async (): Promise<MutualFundHolding[]> => {
  const response = await api.get('/investments/mutual-funds/my-holdings');
  return response.data;
};

// SIP Operations

export const createSip = async (request: SipRequest): Promise<SipInvestment> => {
  const response = await api.post('/investments/sip/create', request);
  return response.data;
};

export const getMySips = async (): Promise<SipInvestment[]> => {
  const response = await api.get('/investments/sip/my-sips');
  return response.data;
};

export const cancelSip = async (sipNumber: string): Promise<SipInvestment> => {
  const response = await api.post(`/investments/sip/${sipNumber}/cancel`);
  return response.data;
};

// Portfolio Operations

export const getMyPortfolio = async (): Promise<InvestmentPortfolio> => {
  const response = await api.get('/investments/portfolio');
  return response.data;
};

export default {
  // Fixed Deposits
  createFixedDeposit,
  getMyFixedDeposits,
  getFixedDepositDetails,
  closeFixedDeposit,
  
  // Recurring Deposits
  createRecurringDeposit,
  getMyRecurringDeposits,
  payRDInstallment,
  
  // Mutual Funds
  getAllMutualFunds,
  getMutualFundDetails,
  investInMutualFund,
  getMyMutualFundHoldings,
  
  // SIP
  createSip,
  getMySips,
  cancelSip,
  
  // Portfolio
  getMyPortfolio
};
