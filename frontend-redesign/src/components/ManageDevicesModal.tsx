import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Box,
  Typography,
  CircularProgress,
  Alert,
  Button,
  Card,
  CardContent,
  Chip,
  Divider
} from '@mui/material';
import {
  Close as CloseIcon,
  Laptop as LaptopIcon,
  PhoneAndroid as PhoneIcon,
  Tablet as TabletIcon,
  Computer as ComputerIcon,
  LocationOn as LocationIcon,
  Schedule as ScheduleIcon,
  CheckCircle as CurrentDeviceIcon
} from '@mui/icons-material';
import { auditService, DeviceSession } from '../services/auditService';

interface ManageDevicesModalProps {
  open: boolean;
  onClose: () => void;
}

const ManageDevicesModal: React.FC<ManageDevicesModalProps> = ({ open, onClose }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [devices, setDevices] = useState<DeviceSession[]>([]);
  const [revoking, setRevoking] = useState<number | null>(null);

  useEffect(() => {
    if (open) {
      loadDevices();
    }
  }, [open]);

  const loadDevices = async () => {
    try {
      setLoading(true);
      setError('');
      const sessions = await auditService.getDeviceSessions();
      setDevices(sessions);
    } catch (err: any) {
      console.error('Failed to load devices:', err);
      setError(err.response?.data?.message || 'Failed to load device sessions');
    } finally {
      setLoading(false);
    }
  };

  const handleRevokeDevice = async (sessionId: number, deviceName: string) => {
    if (!confirm(`Revoke access from "${deviceName}"? This device will be logged out immediately.`)) {
      return;
    }

    try {
      setRevoking(sessionId);
      await auditService.revokeDeviceSession(sessionId);
      alert('Device session revoked successfully!');
      await loadDevices(); // Reload the list
    } catch (err: any) {
      console.error('Failed to revoke device:', err);
      alert(err.response?.data?.message || 'Failed to revoke device session');
    } finally {
      setRevoking(null);
    }
  };

  const handleRevokeAllOthers = async () => {
    const count = devices.filter(d => !d.isCurrentDevice).length;
    if (count === 0) {
      alert('No other devices to revoke.');
      return;
    }

    if (!confirm(`Revoke access from all ${count} other device(s)? They will be logged out immediately.`)) {
      return;
    }

    try {
      setLoading(true);
      const result = await auditService.revokeAllOtherSessions();
      alert(`${result.revokedCount} device session(s) revoked successfully!`);
      await loadDevices();
    } catch (err: any) {
      console.error('Failed to revoke devices:', err);
      alert(err.response?.data?.message || 'Failed to revoke device sessions');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const getDeviceIcon = (deviceName: string) => {
    const lower = deviceName.toLowerCase();
    if (lower.includes('android') || lower.includes('iphone') || lower.includes('ios')) {
      return <PhoneIcon />;
    }
    if (lower.includes('ipad') || lower.includes('tablet')) {
      return <TabletIcon />;
    }
    if (lower.includes('windows') || lower.includes('macos') || lower.includes('linux')) {
      return <LaptopIcon />;
    }
    return <ComputerIcon />;
  };

  return (
    <Dialog 
      open={open} 
      onClose={onClose}
      maxWidth="md"
      fullWidth
    >
      <DialogTitle>
        <Box display="flex" justifyContent="space-between" alignItems="center">
          <Typography variant="h6">Manage Devices</Typography>
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <DialogContent>
        {loading && (
          <Box display="flex" justifyContent="center" alignItems="center" minHeight="200px">
            <CircularProgress />
          </Box>
        )}

        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        {!loading && !error && devices.length === 0 && (
          <Alert severity="info">
            No active device sessions found.
          </Alert>
        )}

        {!loading && !error && devices.length > 0 && (
          <>
            <Alert severity="info" sx={{ mb: 2 }} icon={false}>
              <Typography variant="body2">
                These devices are currently signed in to your account. 
                If you don't recognize a device, revoke its access immediately.
              </Typography>
            </Alert>

            <Box display="flex" flexDirection="column" gap={2}>
              {devices.map((device) => (
                <Card key={device.id} variant="outlined">
                  <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="flex-start">
                      <Box display="flex" gap={2} flex={1}>
                        <Box color="primary.main" sx={{ mt: 0.5 }}>
                          {getDeviceIcon(device.deviceName)}
                        </Box>
                        
                        <Box flex={1}>
                          <Box display="flex" alignItems="center" gap={1} mb={1}>
                            <Typography variant="subtitle1" fontWeight="600">
                              {device.deviceName}
                            </Typography>
                            {device.isCurrentDevice && (
                              <Chip
                                icon={<CurrentDeviceIcon />}
                                label="Current Device"
                                color="success"
                                size="small"
                              />
                            )}
                          </Box>

                          <Box display="flex" flexDirection="column" gap={0.5}>
                            <Box display="flex" alignItems="center" gap={1}>
                              <LocationIcon fontSize="small" color="action" />
                              <Typography variant="body2" color="text.secondary">
                                {device.ipAddress}
                                {device.location && ` • ${device.location}`}
                              </Typography>
                            </Box>

                            <Box display="flex" alignItems="center" gap={1}>
                              <ScheduleIcon fontSize="small" color="action" />
                              <Typography variant="body2" color="text.secondary">
                                Last active: {formatDate(device.lastActivityTime)}
                              </Typography>
                            </Box>

                            <Typography variant="caption" color="text.secondary">
                              Logged in: {formatDate(device.loginTime)}
                            </Typography>
                          </Box>
                        </Box>
                      </Box>

                      {!device.isCurrentDevice && (
                        <Button
                          variant="outlined"
                          color="error"
                          size="small"
                          onClick={() => handleRevokeDevice(device.id, device.deviceName)}
                          disabled={revoking === device.id}
                        >
                          {revoking === device.id ? 'Revoking...' : 'Revoke'}
                        </Button>
                      )}
                    </Box>
                  </CardContent>
                </Card>
              ))}
            </Box>

            {devices.filter(d => !d.isCurrentDevice).length > 0 && (
              <>
                <Divider sx={{ my: 3 }} />
                <Box display="flex" justifyContent="center">
                  <Button
                    variant="outlined"
                    color="error"
                    onClick={handleRevokeAllOthers}
                    disabled={loading}
                  >
                    Revoke All Other Devices
                  </Button>
                </Box>
              </>
            )}
          </>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default ManageDevicesModal;
