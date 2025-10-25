import api from './api'

export interface UpiPaymentRequest {
  upiId: string
  receiverUpiId: string
  amount: number
  provider: 'GOOGLE_PAY' | 'PHONEPE' | 'PAYTM' | 'AMAZON_PAY' | 'BHIM' | 'WHATSAPP_PAY' | 'BANK_UPI' | 'OTHER'
  description?: string
  remarks?: string
}

export interface UpiPaymentResponse {
  upiTransactionId: string
  upiId: string
  receiverUpiId: string
  amount: number
  provider: string
  status: 'INITIATED' | 'PENDING' | 'PROCESSING' | 'SUCCESS' | 'FAILED' | 'CANCELLED' | 'REFUNDED' | 'EXPIRED'
  referenceNumber: string
  description: string
  transactionDate: string
  message: string
}

export interface QrCodeRequest {
  type: 'STATIC' | 'DYNAMIC' | 'MERCHANT' | 'PERSONAL'
  amount?: number
  merchantName?: string
  merchantVpa?: string
  validityMinutes?: number
  maxUsageLimit?: number
}

export interface QrCodeResponse {
  qrCodeId: string
  type: string
  qrData: string
  qrImageBase64?: string
  amount?: number
  merchantName: string
  generatedDate: string
  expiryDate?: string
  isActive: boolean
  usageCount: number
  message: string
}

export interface BillPaymentRequest {
  billType: 'ELECTRICITY' | 'WATER' | 'GAS' | 'MOBILE' | 'BROADBAND' | 'DTH' | 'INSURANCE' | 'CREDIT_CARD' | 'LOAN_EMI' | 'MUNICIPAL_TAX' | 'EDUCATION' | 'SUBSCRIPTION' | 'OTHER'
  billerName: string
  billerCode: string
  consumerNumber: string
  billAmount: number
  convenienceFee?: number
  paymentMethod: 'UPI' | 'QR_CODE' | 'NEFT' | 'RTGS' | 'IMPS' | 'DEBIT_CARD' | 'CREDIT_CARD' | 'NET_BANKING'
  dueDate?: string
  remarks?: string
  enableAutoPay?: boolean
}

export interface BillPaymentResponse {
  billPaymentId: string
  billType: string
  billerName: string
  consumerNumber: string
  billAmount: number
  convenienceFee: number
  totalAmount: number
  paymentStatus: string
  paymentMethod: string
  dueDate?: string
  paymentDate: string
  referenceNumber: string
  transactionId: string
  receiptNumber: string
  message: string
}

export const paymentService = {
  // UPI Payments
  initiateUpiPayment: async (data: UpiPaymentRequest) => {
    const response = await api.post<UpiPaymentResponse>('/payments/upi/initiate', data)
    return response.data
  },

  getUpiTransactionDetails: async (transactionId: string) => {
    const response = await api.get<UpiPaymentResponse>(`/payments/upi/${transactionId}`)
    return response.data
  },

  getUpiTransactionHistory: async (page = 0, size = 20) => {
    const response = await api.get(`/payments/upi/history?page=${page}&size=${size}`)
    return response.data
  },

  verifyUpiId: async (upiId: string) => {
    const response = await api.get<{ valid: boolean }>(`/payments/upi/verify/${upiId}`)
    return response.data
  },

  // QR Code
  generateQrCode: async (data: QrCodeRequest) => {
    const response = await api.post<QrCodeResponse>('/payments/qr/generate', data)
    return response.data
  },

  getQrCodeDetails: async (qrCodeId: string) => {
    const response = await api.get<QrCodeResponse>(`/payments/qr/${qrCodeId}`)
    return response.data
  },

  getQrCodeHistory: async (page = 0, size = 20) => {
    const response = await api.get(`/payments/qr/history?page=${page}&size=${size}`)
    return response.data
  },

  getActiveQrCodes: async () => {
    const response = await api.get<QrCodeResponse[]>('/payments/qr/active')
    return response.data
  },

  deactivateQrCode: async (qrCodeId: string) => {
    const response = await api.put(`/payments/qr/${qrCodeId}/deactivate`)
    return response.data
  },

  scanQrCode: async (qrCodeId: string) => {
    const response = await api.post<QrCodeResponse>(`/payments/qr/${qrCodeId}/scan`)
    return response.data
  },

  // Bill Payments
  payBill: async (data: BillPaymentRequest) => {
    const response = await api.post<BillPaymentResponse>('/payments/bills/pay', data)
    return response.data
  },

  getBillPaymentDetails: async (billPaymentId: string) => {
    const response = await api.get<BillPaymentResponse>(`/payments/bills/${billPaymentId}`)
    return response.data
  },

  getBillPaymentHistory: async (page = 0, size = 20) => {
    const response = await api.get(`/payments/bills/history?page=${page}&size=${size}`)
    return response.data
  },

  getBillPaymentsByType: async (billType: string) => {
    const response = await api.get<BillPaymentResponse[]>(`/payments/bills/type/${billType}`)
    return response.data
  },

  getBillPaymentsByStatus: async (status: string) => {
    const response = await api.get<BillPaymentResponse[]>(`/payments/bills/status/${status}`)
    return response.data
  },

  getAutoPayBills: async () => {
    const response = await api.get<BillPaymentResponse[]>('/payments/bills/autopay')
    return response.data
  },

  toggleAutoPay: async (billPaymentId: string, enabled: boolean) => {
    const response = await api.put(`/payments/bills/${billPaymentId}/autopay`, { enabled })
    return response.data
  }
}
