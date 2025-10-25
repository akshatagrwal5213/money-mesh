import axios from 'axios';

const API_BASE_URL = '/api';

// Get auth token from localStorage
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return token ? { Authorization: `Bearer ${token}` } : {};
};

// ==================== ANALYTICS ====================

export interface AnalyticsRequest {
  startDate: string;
  endDate: string;
  groupBy?: string;
}

export interface DailyTransaction {
  date: string;
  amount: number;
  count: number;
}

export interface AnalyticsResponse {
  totalIncome: number;
  totalExpenses: number;
  netSavings: number;
  totalTransfers: number;
  averageTransactionAmount: number;
  transactionCount: number;
  topCategory: string;
  topCategoryAmount: number;
  categoryBreakdown: { [key: string]: number };
  dailyTransactions: DailyTransaction[];
}

export interface TransactionAnalytics {
  id: number;
  customerId: number;
  periodStart: string;
  periodEnd: string;
  totalIncome: number;
  totalExpenses: number;
  totalTransfers: number;
  averageTransactionAmount: number;
  transactionCount: number;
  topCategory: string;
  topCategoryAmount: number;
  createdAt: string;
}

export const getTransactionAnalytics = async (request: AnalyticsRequest): Promise<AnalyticsResponse> => {
  const response = await axios.post(`${API_BASE_URL}/analytics/transaction-analytics`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const saveAnalytics = async (periodStart: string, periodEnd: string): Promise<TransactionAnalytics> => {
  const response = await axios.post(
    `${API_BASE_URL}/analytics/save-analytics?periodStart=${periodStart}&periodEnd=${periodEnd}`,
    {},
    { headers: getAuthHeader() }
  );
  return response.data;
};

export const getAllAnalytics = async (): Promise<TransactionAnalytics[]> => {
  const response = await axios.get(`${API_BASE_URL}/analytics/all`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getAccountSummary = async (): Promise<any> => {
  const response = await axios.get(`${API_BASE_URL}/analytics/account-summary`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

// ==================== BUDGETS ====================

export interface BudgetRequest {
  name: string;
  category: string;
  budgetAmount: number;
  period: 'WEEKLY' | 'MONTHLY' | 'QUARTERLY' | 'YEARLY' | 'CUSTOM';
  startDate?: string;
  endDate?: string;
  alertThreshold?: number;
}

export interface Budget {
  id: number;
  customerId: number;
  name: string;
  category: string;
  budgetAmount: number;
  spentAmount: number;
  period: string;
  startDate: string;
  endDate: string;
  alertThreshold: number;
  isActive: boolean;
  alertSent: boolean;
  createdAt: string;
  updatedAt: string;
}

export const createBudget = async (request: BudgetRequest): Promise<Budget> => {
  const response = await axios.post(`${API_BASE_URL}/budgets`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getAllBudgets = async (): Promise<Budget[]> => {
  const response = await axios.get(`${API_BASE_URL}/budgets`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getActiveBudgets = async (): Promise<Budget[]> => {
  const response = await axios.get(`${API_BASE_URL}/budgets/active`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getBudgetById = async (budgetId: number): Promise<Budget> => {
  const response = await axios.get(`${API_BASE_URL}/budgets/${budgetId}`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const updateBudget = async (budgetId: number, request: BudgetRequest): Promise<Budget> => {
  const response = await axios.put(`${API_BASE_URL}/budgets/${budgetId}`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const addExpense = async (budgetId: number, amount: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/budgets/${budgetId}/add-expense?amount=${amount}`, {}, {
    headers: getAuthHeader(),
  });
};

export const deleteBudget = async (budgetId: number): Promise<void> => {
  await axios.delete(`${API_BASE_URL}/budgets/${budgetId}`, {
    headers: getAuthHeader(),
  });
};

export const deactivateBudget = async (budgetId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/budgets/${budgetId}/deactivate`, {}, {
    headers: getAuthHeader(),
  });
};

export const resetBudget = async (budgetId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/budgets/${budgetId}/reset`, {}, {
    headers: getAuthHeader(),
  });
};

// ==================== FINANCIAL GOALS ====================

export interface GoalRequest {
  name: string;
  description?: string;
  type: 'EMERGENCY_FUND' | 'RETIREMENT' | 'HOME_PURCHASE' | 'CAR_PURCHASE' | 'EDUCATION' | 'VACATION' | 'WEDDING' | 'DEBT_PAYOFF' | 'INVESTMENT' | 'CUSTOM';
  targetAmount: number;
  targetDate: string;
  monthlyContribution?: number;
  isAutomated?: boolean;
  linkedAccountId?: number;
}

export interface GoalContributionRequest {
  goalId: number;
  amount: number;
  accountId: number;
}

export interface FinancialGoal {
  id: number;
  customerId: number;
  name: string;
  description: string;
  type: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string;
  monthlyContribution: number;
  status: 'IN_PROGRESS' | 'ON_TRACK' | 'BEHIND' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';
  isAutomated: boolean;
  linkedAccountId?: number;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

export const createGoal = async (request: GoalRequest): Promise<FinancialGoal> => {
  const response = await axios.post(`${API_BASE_URL}/goals`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getAllGoals = async (): Promise<FinancialGoal[]> => {
  const response = await axios.get(`${API_BASE_URL}/goals`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getActiveGoals = async (): Promise<FinancialGoal[]> => {
  const response = await axios.get(`${API_BASE_URL}/goals/active`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const getGoalById = async (goalId: number): Promise<FinancialGoal> => {
  const response = await axios.get(`${API_BASE_URL}/goals/${goalId}`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const updateGoal = async (goalId: number, request: GoalRequest): Promise<FinancialGoal> => {
  const response = await axios.put(`${API_BASE_URL}/goals/${goalId}`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const contributeToGoal = async (request: GoalContributionRequest): Promise<FinancialGoal> => {
  const response = await axios.post(`${API_BASE_URL}/goals/contribute`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const deleteGoal = async (goalId: number): Promise<void> => {
  await axios.delete(`${API_BASE_URL}/goals/${goalId}`, {
    headers: getAuthHeader(),
  });
};

export const pauseGoal = async (goalId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/goals/${goalId}/pause`, {}, {
    headers: getAuthHeader(),
  });
};

export const resumeGoal = async (goalId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/goals/${goalId}/resume`, {}, {
    headers: getAuthHeader(),
  });
};

export const cancelGoal = async (goalId: number): Promise<void> => {
  await axios.post(`${API_BASE_URL}/goals/${goalId}/cancel`, {}, {
    headers: getAuthHeader(),
  });
};

// ==================== USER PREFERENCES ====================

export interface PreferencesRequest {
  emailNotifications?: boolean;
  smsNotifications?: boolean;
  pushNotifications?: boolean;
  transactionAlerts?: boolean;
  budgetAlerts?: boolean;
  investmentAlerts?: boolean;
  promotionalAlerts?: boolean;
  theme?: string;
  language?: string;
  currency?: string;
  timezone?: string;
  dateFormat?: string;
  twoFactorEnabled?: boolean;
  biometricEnabled?: boolean;
  autoLogoutMinutes?: number;
  loginAlerts?: boolean;
  shareAnalytics?: boolean;
  marketingEmails?: boolean;
}

export interface UserPreferences {
  id: number;
  customerId: number;
  emailNotifications: boolean;
  smsNotifications: boolean;
  pushNotifications: boolean;
  transactionAlerts: boolean;
  budgetAlerts: boolean;
  investmentAlerts: boolean;
  promotionalAlerts: boolean;
  theme: string;
  language: string;
  currency: string;
  timezone: string;
  dateFormat: string;
  twoFactorEnabled: boolean;
  biometricEnabled: boolean;
  autoLogoutMinutes: number;
  loginAlerts: boolean;
  shareAnalytics: boolean;
  marketingEmails: boolean;
  createdAt: string;
  updatedAt: string;
}

export const getPreferences = async (): Promise<UserPreferences> => {
  const response = await axios.get(`${API_BASE_URL}/preferences`, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const updatePreferences = async (request: PreferencesRequest): Promise<UserPreferences> => {
  const response = await axios.put(`${API_BASE_URL}/preferences`, request, {
    headers: getAuthHeader(),
  });
  return response.data;
};

export const resetPreferences = async (): Promise<void> => {
  await axios.post(`${API_BASE_URL}/preferences/reset`, {}, {
    headers: getAuthHeader(),
  });
};

// ==================== HELPER FUNCTIONS ====================

export const calculateBudgetProgress = (budget: Budget): number => {
  if (budget.budgetAmount === 0) return 0;
  return (budget.spentAmount / budget.budgetAmount) * 100;
};

export const calculateGoalProgress = (goal: FinancialGoal): number => {
  if (goal.targetAmount === 0) return 0;
  return (goal.currentAmount / goal.targetAmount) * 100;
};

export const getRemainingAmount = (goal: FinancialGoal): number => {
  return goal.targetAmount - goal.currentAmount;
};

export const getDaysRemaining = (targetDate: string): number => {
  const today = new Date();
  const target = new Date(targetDate);
  const diff = target.getTime() - today.getTime();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
};
