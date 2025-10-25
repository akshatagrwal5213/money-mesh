import axios from 'axios';

const API_URL = 'http://localhost:8080/api/tax';

// TypeScript Interfaces
export interface TaxCalculationRequest {
  grossIncome: number;
  financialYear: string;
  assessmentYear: string;
  regime: 'OLD_REGIME' | 'NEW_REGIME';
  deductions?: {
    deductionType: string;
    amount: number;
  }[];
  age?: number;
}

export interface TaxSlabBreakdown {
  slabDescription: string;
  incomeInSlab: number;
  taxRate: number;
  taxAmount: number;
}

export interface TaxCalculationResponse {
  grossIncome: number;
  totalDeductions: number;
  taxableIncome: number;
  taxBeforeRebate: number;
  rebate87A: number;
  taxAfterRebate: number;
  cess: number;
  totalTaxLiability: number;
  regime: string;
  slabBreakdown: TaxSlabBreakdown[];
  regimeComparison?: {
    oldRegimeTax: number;
    newRegimeTax: number;
    recommendation: string;
    savingsAmount: number;
  };
}

export interface TaxDeductionRequest {
  deductionType: string;
  amount: number;
  financialYear: string;
  description?: string;
  proofDocumentPath?: string;
}

export interface TaxDeduction {
  id: number;
  deductionType: string;
  amount: number;
  financialYear: string;
  description?: string;
  proofDocumentPath?: string;
  claimedDate: string;
}

export interface TaxPaymentRequest {
  paymentType: string;
  amount: number;
  financialYear: string;
  assessmentYear: string;
  challanNumber?: string;
  bankName?: string;
  paymentDate: string;
}

export interface TaxPayment {
  id: number;
  paymentType: string;
  amount: number;
  financialYear: string;
  assessmentYear: string;
  challanNumber?: string;
  bankName?: string;
  paymentDate: string;
}

export interface CapitalGainRequest {
  assetType: string;
  purchaseDate: string;
  saleDate: string;
  purchasePrice: number;
  salePrice: number;
  financialYear: string;
  indexationApplied?: boolean;
}

export interface CapitalGain {
  id: number;
  assetType: string;
  purchaseDate: string;
  saleDate: string;
  purchasePrice: number;
  salePrice: number;
  capitalGainType: string;
  holdingPeriodMonths: number;
  capitalGain: number;
  taxRate: number;
  taxAmount: number;
  financialYear: string;
  indexationApplied: boolean;
}

export interface TaxSummaryResponse {
  financialYear: string;
  assessmentYear: string;
  totalGrossIncome: number;
  totalDeductions: number;
  taxableIncome: number;
  estimatedTaxLiability: number;
  totalTaxPaid: number;
  taxBalance: number;
  documents: {
    documentType: string;
    uploadDate: string;
    filePath: string;
  }[];
  payments: {
    paymentType: string;
    amount: number;
    paymentDate: string;
  }[];
  taxSavingRecommendations: string[];
}

// API Functions
const getAuthHeader = () => {
  const token = localStorage.getItem('token');
  return { Authorization: `Bearer ${token}` };
};

export const taxService = {
  // Calculate tax
  calculateTax: async (request: TaxCalculationRequest): Promise<TaxCalculationResponse> => {
    const response = await axios.post(`${API_URL}/calculate`, request, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Add deduction
  addDeduction: async (request: TaxDeductionRequest): Promise<TaxDeduction> => {
    const response = await axios.post(`${API_URL}/deductions`, request, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Get deductions for a financial year
  getDeductions: async (financialYear: string): Promise<TaxDeduction[]> => {
    const response = await axios.get(`${API_URL}/deductions/${financialYear}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Record tax payment
  recordPayment: async (request: TaxPaymentRequest): Promise<TaxPayment> => {
    const response = await axios.post(`${API_URL}/payments`, request, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Get payments for a financial year
  getPayments: async (financialYear: string): Promise<TaxPayment[]> => {
    const response = await axios.get(`${API_URL}/payments/${financialYear}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Calculate capital gain
  calculateCapitalGain: async (request: CapitalGainRequest): Promise<CapitalGain> => {
    const response = await axios.post(`${API_URL}/capital-gains`, request, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Get capital gains for a financial year
  getCapitalGains: async (financialYear: string): Promise<CapitalGain[]> => {
    const response = await axios.get(`${API_URL}/capital-gains/${financialYear}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Get tax summary
  getTaxSummary: async (financialYear: string): Promise<TaxSummaryResponse> => {
    const response = await axios.get(`${API_URL}/summary/${financialYear}`, {
      headers: getAuthHeader(),
    });
    return response.data;
  },

  // Get current financial year
  getCurrentFinancialYear: async (): Promise<string> => {
    const response = await axios.get(`${API_URL}/current-year`);
    return response.data;
  },
};

// Helper functions
export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(amount);
};

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
};

export const getDeductionTypeLabel = (type: string): string => {
  const labels: { [key: string]: string } = {
    SECTION_80C_PPF: '80C - PPF',
    SECTION_80C_EPF: '80C - EPF',
    SECTION_80C_ELSS: '80C - ELSS',
    SECTION_80C_LIC: '80C - Life Insurance',
    SECTION_80C_NSC: '80C - NSC',
    SECTION_80C_TAX_SAVER_FD: '80C - Tax Saver FD',
    SECTION_80C_TUITION_FEE: '80C - Tuition Fee',
    SECTION_80C_HOME_LOAN_PRINCIPAL: '80C - Home Loan Principal',
    SECTION_80D_HEALTH_INSURANCE_SELF: '80D - Health Insurance (Self)',
    SECTION_80D_HEALTH_INSURANCE_PARENTS: '80D - Health Insurance (Parents)',
    SECTION_80D_PREVENTIVE_HEALTH_CHECKUP: '80D - Preventive Health Checkup',
    SECTION_80E_EDUCATION_LOAN: '80E - Education Loan Interest',
    SECTION_80G_DONATIONS: '80G - Donations',
    SECTION_80GG_HOUSE_RENT: '80GG - House Rent',
    HRA_EXEMPTION: 'HRA Exemption',
    SECTION_24_HOME_LOAN_INTEREST: '24 - Home Loan Interest',
    STANDARD_DEDUCTION: 'Standard Deduction',
    NPS_80CCD_1B: 'NPS - 80CCD(1B)',
  };
  return labels[type] || type;
};

export const getPaymentTypeLabel = (type: string): string => {
  const labels: { [key: string]: string } = {
    ADVANCE_TAX_Q1: 'Advance Tax - Q1',
    ADVANCE_TAX_Q2: 'Advance Tax - Q2',
    ADVANCE_TAX_Q3: 'Advance Tax - Q3',
    ADVANCE_TAX_Q4: 'Advance Tax - Q4',
    SELF_ASSESSMENT_TAX: 'Self Assessment Tax',
    TDS_SALARY: 'TDS - Salary',
    TDS_INTEREST: 'TDS - Interest Income',
    TDS_PROFESSIONAL_FEES: 'TDS - Professional Fees',
    TCS: 'TCS',
  };
  return labels[type] || type;
};

export const getAssetTypeLabel = (type: string): string => {
  const labels: { [key: string]: string } = {
    EQUITY_LISTED: 'Listed Equity',
    EQUITY_UNLISTED: 'Unlisted Equity',
    MUTUAL_FUND_EQUITY: 'Equity Mutual Fund',
    MUTUAL_FUND_DEBT: 'Debt Mutual Fund',
    PROPERTY_RESIDENTIAL: 'Residential Property',
    PROPERTY_COMMERCIAL: 'Commercial Property',
    GOLD: 'Gold',
    BONDS: 'Bonds',
  };
  return labels[type] || type;
};

export default taxService;
