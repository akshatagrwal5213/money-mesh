import api from './api'

export interface Account {
  id: number
  accountNumber: string
  accountType: string
  balance: number
  status: string
  nickname?: string
  customer: {
    id: number
    name: string
    email: string
    phone: string
  }
  createdAt: string
}

export interface CreateAccountRequest {
  accountType: 'SAVINGS' | 'CURRENT' | 'SALARY' | 'FIXED_DEPOSIT'
  initialDeposit: number
  nickname?: string
}

export interface UpdateNicknameRequest {
  nickname: string
}

export const accountService = {
  // Get all accounts for the logged-in user
  getAccounts: async () => {
    const response = await api.get<Account[]>('/accounts/user')
    return response.data
  },

  // Get account details
  getAccountDetails: async (accountNumber: string) => {
    const response = await api.get<Account>(`/accounts/${accountNumber}`)
    return response.data
  },

  // Create new account
  createAccount: async (data: CreateAccountRequest) => {
    const response = await api.post<Account>('/accounts/create', data)
    return response.data
  },

  // Update account nickname
  updateNickname: async (accountId: number, data: UpdateNicknameRequest) => {
    const response = await api.put<Account>(`/accounts/${accountId}/nickname`, data)
    return response.data
  },

  // Close account
  closeAccount: async (accountId: number) => {
    const response = await api.delete(`/accounts/${accountId}/close`)
    return response.data
  },

  // Get account balance
  getBalance: async (accountNumber: string) => {
    const response = await api.get<{ balance: number }>(`/accounts/${accountNumber}/balance`)
    return response.data
  }
}
