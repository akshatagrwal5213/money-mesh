import api from './api';

export interface LoginHistoryEntry {
  id: number;
  timestamp: string;
  ipAddress: string;
  userAgent: string;
  status: 'SUCCESS' | 'FAILURE' | 'WARNING';
  details?: string;
}

export interface AuditHistoryEntry extends LoginHistoryEntry {
  action: string;
}

export interface DeviceSession {
  id: number;
  deviceName: string;
  ipAddress: string;
  location?: string;
  loginTime: string;
  lastActivityTime: string;
  isCurrentDevice: boolean;
}

export const auditService = {
  getLoginHistory: async (): Promise<LoginHistoryEntry[]> => {
    const response = await api.get('/audit/login-history');
    return response.data;
  },

  getAuditHistory: async (): Promise<AuditHistoryEntry[]> => {
    const response = await api.get('/audit/history');
    return response.data;
  },

  getDeviceSessions: async (): Promise<DeviceSession[]> => {
    const response = await api.get('/devices');
    return response.data;
  },

  revokeDeviceSession: async (sessionId: number): Promise<void> => {
    await api.delete(`/devices/${sessionId}`);
  },

  revokeAllOtherSessions: async (): Promise<{ revokedCount: number }> => {
    const response = await api.post('/devices/revoke-all');
    return response.data;
  }
};
