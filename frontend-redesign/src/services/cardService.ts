import api from './api'

export interface Card {
  id: number
  cardNumber: string
  cardType: 'DEBIT' | 'CREDIT'
  status: 'ACTIVE' | 'BLOCKED' | 'EXPIRED' | 'PENDING_ACTIVATION' | 'CANCELLED' | 'LOST' | 'STOLEN'
  provider: 'VISA' | 'MASTERCARD' | 'RUPAY' | 'AMEX'
  holderName: string
  expiryDate: string
  issuedDate: string
  dailyLimit: number
  monthlyLimit: number
  contactlessEnabled: boolean
  internationalEnabled: boolean
  account: {
    id: number
    accountNumber: string
    balance: number
  }
}

export interface IssueCardRequest {
  accountId: number
  cardType: 'DEBIT' | 'CREDIT'
  provider: 'VISA' | 'MASTERCARD' | 'RUPAY' | 'AMEX'
  dailyLimit?: number
  monthlyLimit?: number
}

export interface ActivateCardRequest {
  cardNumber: string
  cvv: string
  pin: string
}

export interface BlockCardRequest {
  reason: string
}

export interface UpdateLimitsRequest {
  dailyLimit: number
  monthlyLimit: number
}

export interface ChangePinRequest {
  oldPin: string
  newPin: string
}

export const cardService = {
  // Get all cards for the logged-in user
  getAllCards: async () => {
    const response = await api.get<Card[]>('/cards/user')
    return response.data
  },

  // Get card details
  getCardDetails: async (cardId: number) => {
    const response = await api.get<Card>(`/cards/${cardId}`)
    return response.data
  },

  // Issue new card
  issueCard: async (data: IssueCardRequest) => {
    const response = await api.post<Card>('/cards/issue', data)
    return response.data
  },

  // Activate card
  activateCard: async (cardId: number, data: ActivateCardRequest) => {
    const response = await api.post(`/cards/${cardId}/activate`, data)
    return response.data
  },

  // Block card
  blockCard: async (cardId: number, data: BlockCardRequest) => {
    const response = await api.post(`/cards/${cardId}/block`, data)
    return response.data
  },

  // Unblock card
  unblockCard: async (cardId: number) => {
    const response = await api.post(`/cards/${cardId}/unblock`)
    return response.data
  },

  // Update card limits
  updateLimits: async (cardId: number, data: UpdateLimitsRequest) => {
    const response = await api.put(`/cards/${cardId}/limits`, data)
    return response.data
  },

  // Toggle contactless
  toggleContactless: async (cardId: number, enabled: boolean) => {
    const response = await api.put(`/cards/${cardId}/contactless`, { enabled })
    return response.data
  },

  // Toggle international usage
  toggleInternational: async (cardId: number, enabled: boolean) => {
    const response = await api.put(`/cards/${cardId}/international`, { enabled })
    return response.data
  },

  // Change PIN
  changePin: async (cardId: number, data: ChangePinRequest) => {
    const response = await api.post(`/cards/${cardId}/change-pin`, data)
    return response.data
  },

  // Get card transactions
  getCardTransactions: async (cardId: number) => {
    const response = await api.get(`/cards/${cardId}/transactions`)
    return response.data
  }
}
