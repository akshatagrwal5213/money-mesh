import api from './api'

export interface Loan {
  id: number
  loanId: string
  loanType: 'PERSONAL' | 'HOME' | 'CAR' | 'EDUCATION' | 'BUSINESS' | 'GOLD' | 'AGRICULTURE'
  principalAmount: number
  interestRate: number
  tenure: number
  emiAmount: number
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DISBURSED' | 'ACTIVE' | 'CLOSED' | 'DEFAULTED' | 'WRITTEN_OFF' | 'FORECLOSED'
  remainingAmount: number
  paidAmount: number
  nextDueDate: string
  applicationDate: string
  approvalDate?: string
  disbursementDate?: string
  repaymentFrequency: 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY'
}

export interface LoanApplication {
  id: number
  loanType: string
  principalAmount: number
  tenure: number
  purpose: string
  status: string
  applicationDate: string
  customer: {
    name: string
    email: string
  }
}

export interface LoanApplicationRequest {
  loanType: 'PERSONAL' | 'HOME' | 'CAR' | 'EDUCATION' | 'BUSINESS' | 'GOLD' | 'AGRICULTURE'
  principalAmount: number
  tenure: number
  repaymentFrequency: 'MONTHLY' | 'QUARTERLY' | 'HALF_YEARLY' | 'YEARLY'
  purpose: string
  annualIncome: number
  employmentType: string
}

export interface EmiCalculationRequest {
  loanType: string
  principalAmount: number
  tenure: number
  repaymentFrequency: string
}

export interface EmiCalculationResponse {
  emiAmount: number
  totalInterest: number
  totalAmount: number
  interestRate: number
}

export interface LoanRepaymentRequest {
  accountId: number
  amount?: number
}

export const loanService = {
  // Get all loans for user
  getUserLoans: async () => {
    const response = await api.get<Loan[]>('/loans/user')
    return response.data
  },

  // Get loan details
  getLoanDetails: async (loanId: number) => {
    const response = await api.get<Loan>(`/loans/${loanId}`)
    return response.data
  },

  // Apply for loan
  applyForLoan: async (data: LoanApplicationRequest) => {
    const response = await api.post('/loans/apply', data)
    return response.data
  },

  // Calculate EMI
  calculateEmi: async (data: EmiCalculationRequest) => {
    const response = await api.post<EmiCalculationResponse>('/loans/calculate-emi', data)
    return response.data
  },

  // Pay EMI
  payEmi: async (loanId: number, data: LoanRepaymentRequest) => {
    const response = await api.post(`/loans/${loanId}/pay`, data)
    return response.data
  },

  // Get repayment history
  getRepaymentHistory: async (loanId: number) => {
    const response = await api.get(`/loans/${loanId}/repayments`)
    return response.data
  },

  // Admin: Get pending applications
  getPendingApplications: async () => {
    const response = await api.get<LoanApplication[]>('/loans/applications/pending')
    return response.data
  },

  // Admin: Get all applications
  getAllApplications: async () => {
    const response = await api.get<LoanApplication[]>('/loans/applications')
    return response.data
  },

  // Admin: Approve loan
  approveLoan: async (loanId: number) => {
    const response = await api.post(`/loans/${loanId}/approve`)
    return response.data
  },

  // Admin: Reject loan
  rejectLoan: async (loanId: number, remarks: string) => {
    const response = await api.post(`/loans/${loanId}/reject`, { remarks })
    return response.data
  },

  // Admin: Disburse loan
  disburseLoan: async (loanId: number, accountId: number) => {
    const response = await api.post(`/loans/${loanId}/disburse`, { accountId })
    return response.data
  }
}
