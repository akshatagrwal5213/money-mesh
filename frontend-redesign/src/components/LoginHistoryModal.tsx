import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
  Box,
  Typography,
  CircularProgress,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Tooltip
} from '@mui/material';
import {
  Close as CloseIcon,
  CheckCircle as SuccessIcon,
  Error as ErrorIcon,
  Warning as WarningIcon,
  Computer as DeviceIcon,
  LocationOn as LocationIcon
} from '@mui/icons-material';
import { auditService, LoginHistoryEntry } from '../services/auditService';

interface LoginHistoryModalProps {
  open: boolean;
  onClose: () => void;
}

const LoginHistoryModal: React.FC<LoginHistoryModalProps> = ({ open, onClose }) => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [loginHistory, setLoginHistory] = useState<LoginHistoryEntry[]>([]);

  useEffect(() => {
    if (open) {
      loadLoginHistory();
    }
  }, [open]);

  const loadLoginHistory = async () => {
    try {
      setLoading(true);
      setError('');
      const history = await auditService.getLoginHistory();
      setLoginHistory(history);
    } catch (err: any) {
      console.error('Failed to load login history:', err);
      setError(err.response?.data?.message || 'Failed to load login history');
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (timestamp: string) => {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  };

  const parseBrowser = (userAgent: string): string => {
    if (!userAgent) return 'Unknown';
    if (userAgent.includes('Chrome')) return 'Chrome';
    if (userAgent.includes('Firefox')) return 'Firefox';
    if (userAgent.includes('Safari')) return 'Safari';
    if (userAgent.includes('Edge')) return 'Edge';
    if (userAgent.includes('Opera')) return 'Opera';
    return 'Other';
  };

  const parseOS = (userAgent: string): string => {
    if (!userAgent) return 'Unknown';
    if (userAgent.includes('Windows')) return 'Windows';
    if (userAgent.includes('Mac')) return 'macOS';
    if (userAgent.includes('Linux')) return 'Linux';
    if (userAgent.includes('Android')) return 'Android';
    if (userAgent.includes('iOS')) return 'iOS';
    return 'Other';
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return <SuccessIcon color="success" />;
      case 'FAILURE':
        return <ErrorIcon color="error" />;
      case 'WARNING':
        return <WarningIcon color="warning" />;
      default:
        return null;
    }
  };

  const getStatusColor = (status: string): 'success' | 'error' | 'warning' | 'default' => {
    switch (status) {
      case 'SUCCESS':
        return 'success';
      case 'FAILURE':
        return 'error';
      case 'WARNING':
        return 'warning';
      default:
        return 'default';
    }
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
          <Typography variant="h6">Login History</Typography>
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

        {!loading && !error && loginHistory.length === 0 && (
          <Alert severity="info">
            No login history found.
          </Alert>
        )}

        {!loading && !error && loginHistory.length > 0 && (
          <TableContainer component={Paper} variant="outlined">
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell><strong>Status</strong></TableCell>
                  <TableCell><strong>Date & Time</strong></TableCell>
                  <TableCell><strong>IP Address</strong></TableCell>
                  <TableCell><strong>Device</strong></TableCell>
                  <TableCell><strong>Details</strong></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {loginHistory.map((entry) => (
                  <TableRow key={entry.id} hover>
                    <TableCell>
                      <Tooltip title={entry.status}>
                        <Chip
                          icon={getStatusIcon(entry.status) || undefined}
                          label={entry.status}
                          color={getStatusColor(entry.status)}
                          size="small"
                        />
                      </Tooltip>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {formatDate(entry.timestamp)}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box display="flex" alignItems="center" gap={0.5}>
                        <LocationIcon fontSize="small" color="action" />
                        <Typography variant="body2">
                          {entry.ipAddress || 'Unknown'}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Box display="flex" alignItems="center" gap={0.5}>
                        <DeviceIcon fontSize="small" color="action" />
                        <Tooltip title={entry.userAgent || 'Unknown'}>
                          <Typography variant="body2" noWrap maxWidth={150}>
                            {parseBrowser(entry.userAgent)} on {parseOS(entry.userAgent)}
                          </Typography>
                        </Tooltip>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {entry.details || '-'}
                      </Typography>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        )}

        {!loading && !error && loginHistory.length > 0 && (
          <Box mt={2}>
            <Alert severity="info" icon={false}>
              <Typography variant="caption">
                Showing {loginHistory.length} login attempt(s). 
                If you notice any suspicious activity, please change your password immediately.
              </Typography>
            </Alert>
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default LoginHistoryModal;
