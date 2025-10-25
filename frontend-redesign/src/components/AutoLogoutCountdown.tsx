import React, { useState, useEffect } from 'react';
import { Box, Tooltip, Typography, Chip } from '@mui/material';
import { Timer as TimerIcon } from '@mui/icons-material';

interface AutoLogoutCountdownProps {
  getRemainingTime: (() => number | null) | undefined;
  enabled: boolean;
}

const AutoLogoutCountdown: React.FC<AutoLogoutCountdownProps> = ({ getRemainingTime, enabled }) => {
  const [remainingSeconds, setRemainingSeconds] = useState<number | null>(null);

  useEffect(() => {
    if (!enabled || !getRemainingTime) {
      setRemainingSeconds(null);
      return;
    }

    // Update every second
    const interval = setInterval(() => {
      const remaining = getRemainingTime();
      setRemainingSeconds(remaining);
    }, 1000);

    return () => clearInterval(interval);
  }, [enabled, getRemainingTime]);

  if (!enabled || remainingSeconds === null || remainingSeconds <= 0) {
    return null;
  }

  const minutes = Math.floor(remainingSeconds / 60);
  const seconds = remainingSeconds % 60;
  const displayTime = `${minutes}:${seconds.toString().padStart(2, '0')}`;

  // Color based on time remaining
  let color: 'success' | 'warning' | 'error' = 'success';
  if (remainingSeconds < 60) {
    color = 'error';
  } else if (remainingSeconds < 300) {
    color = 'warning';
  }

  return (
    <Tooltip title={`Auto-logout in ${displayTime}. Timer resets with any activity.`}>
      <Chip
        icon={<TimerIcon />}
        label={displayTime}
        color={color}
        size="small"
        sx={{ 
          fontFamily: 'monospace',
          fontWeight: 'bold',
          cursor: 'help'
        }}
      />
    </Tooltip>
  );
};

export default AutoLogoutCountdown;
