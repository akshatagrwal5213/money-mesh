import api from './api'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  username: string
  roles: string[]
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
  fullName: string
  phone: string
}

export interface MfaVerifyRequest {
  username: string
  code: string
}

export const authService = {
  login: async (data: LoginRequest) => {
    const response = await api.post<LoginResponse>('/auth/login', data)
    return response.data
  },

  register: async (data: RegisterRequest) => {
    const response = await api.post('/auth/register', data)
    return response.data
  },

  logout: async () => {
    const response = await api.post('/auth/logout')
    return response.data
  },

  refreshToken: async (refreshToken: string) => {
    const response = await api.post('/auth/refresh', { refreshToken })
    return response.data
  },

  verifyMfa: async (data: MfaVerifyRequest) => {
    const response = await api.post('/auth/verify-mfa', data)
    return response.data
  },

  changePassword: async (currentPassword: string, newPassword: string) => {
    const response = await api.post('/auth/change-password', { 
      currentPassword, 
      newPassword 
    })
    return response.data
  }
}
