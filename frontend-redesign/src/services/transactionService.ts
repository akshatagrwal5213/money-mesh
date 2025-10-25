import api from './api'

export interface Transaction {
  id: number
  type: 'DEPOSIT' | 'WITHDRAWAL' | 'TRANSFER_IN' | 'TRANSFER_OUT'
  amount: number
  description: string
  timestamp: string
  status: string
  referenceNumber: string
  accountId: number
  balance?: number
}

export interface TransactionPage {
  content: Transaction[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface DepositRequest {
  accountNumber: string
  amount: number
  description?: string
}

export interface WithdrawalRequest {
  accountNumber: string
  amount: number
  description?: string
}

export interface TransferRequest {
  fromAccountNumber: string
  toAccountNumber: string
  amount: number
  description?: string
}

export interface PendingTransferRequest {
  fromAccountNumber: string
  toAccountNumber: string
  amount: number
  description?: string
}

export interface ConfirmTransferRequest {
  transferId: number
  otp: string
}

export const transactionService = {
  // Get transaction history for an account
  getTransactionHistory: async (accountNumber: string, page = 0, size = 20) => {
    const response = await api.get<TransactionPage>(
      `/transactions/${accountNumber}/history?page=${page}&size=${size}&sort=timestamp,desc`
    )
    return response.data
  },

  // Get transactions by date range
  getTransactionsByDateRange: async (accountNumber: string, startDate: string, endDate: string) => {
    const response = await api.get<Transaction[]>(
      `/transactions/${accountNumber}/range?startDate=${startDate}&endDate=${endDate}`
    )
    return response.data
  },

  // Deposit money
  deposit: async (data: DepositRequest) => {
    const response = await api.post('/transactions/deposit', data)
    return response.data
  },

  // Withdraw money
  withdraw: async (data: WithdrawalRequest) => {
    const response = await api.post('/transactions/withdraw', data)
    return response.data
  },

  // Transfer money (direct)
  transfer: async (data: TransferRequest) => {
    const response = await api.post('/transactions/transfer', data)
    return response.data
  },

  // Request OTP for transfer
  requestTransferOtp: async () => {
    const response = await api.post('/transactions/transfer/request-otp')
    return response.data
  },

  // Check if OTP is required
  checkOtpRequirement: async (amount: number) => {
    const response = await api.post<{ otpRequired: boolean }>(
      '/transactions/transfer/check-otp',
      { amount }
    )
    return response.data
  },

  // Initiate pending transfer
  initiatePendingTransfer: async (data: PendingTransferRequest) => {
    const response = await api.post('/transactions/transfer/pending', data)
    return response.data
  },

  // Confirm pending transfer with OTP
  confirmPendingTransfer: async (data: ConfirmTransferRequest) => {
    const response = await api.post('/transactions/transfer/confirm', data)
    return response.data
  }
}
