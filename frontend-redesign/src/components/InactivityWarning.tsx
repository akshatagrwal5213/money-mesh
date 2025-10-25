import React, { useState, useEffect } from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from '@mui/material';
import { Warning as WarningIcon } from '@mui/icons-material';

interface InactivityWarningProps {
  remainingSeconds: number;
  onStayActive: () => void;
  warningThresholdSeconds?: number;
}

/**
 * Component that shows a warning dialog when the user is about to be logged out due to inactivity
 */
export const InactivityWarning: React.FC<InactivityWarningProps> = ({
  remainingSeconds,
  onStayActive,
  warningThresholdSeconds = 60, // Show warning when 60 seconds remain
}) => {
  const [open, setOpen] = useState(false);

  useEffect(() => {
    // Show warning if remaining time is less than threshold
    if (remainingSeconds > 0 && remainingSeconds <= warningThresholdSeconds) {
      setOpen(true);
    } else {
      setOpen(false);
    }
  }, [remainingSeconds, warningThresholdSeconds]);

  const handleStayActive = () => {
    setOpen(false);
    onStayActive();
  };

  const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    if (mins > 0) {
      return `${mins}:${secs.toString().padStart(2, '0')}`;
    }
    return `${secs}s`;
  };

  return (
    <Dialog
      open={open}
      onClose={handleStayActive}
      aria-labelledby="inactivity-warning-dialog"
      PaperProps={{
        sx: {
          minWidth: '400px',
          borderTop: '4px solid #f97316',
        },
      }}
    >
      <DialogTitle
        id="inactivity-warning-dialog"
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 1,
        }}
      >
        <WarningIcon sx={{ color: '#f97316' }} />
        Session Expiring Soon
      </DialogTitle>
      <DialogContent>
        <Typography variant="body1" gutterBottom>
          Your session will expire in <strong>{formatTime(remainingSeconds)}</strong> due to inactivity.
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Click "Stay Active" to continue your session, or you will be logged out automatically.
        </Typography>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleStayActive} variant="contained" color="primary" autoFocus>
          Stay Active
        </Button>
      </DialogActions>
    </Dialog>
  );
};
