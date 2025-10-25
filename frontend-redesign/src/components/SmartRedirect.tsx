import React, { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Box, CircularProgress, Typography } from '@mui/material';

/**
 * SmartRedirect: Intelligently redirects users based on their role
 * - Admins → /admin/customers
 * - Customers → /dashboard
 */
const SmartRedirect: React.FC = () => {
  const { isAuthenticated, isAdmin, user } = useAuth();

  // Show loading while checking authentication
  if (!user && isAuthenticated === undefined) {
    return (
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center',
          minHeight: '100vh',
          gap: 2,
        }}
      >
        <CircularProgress size={60} />
        <Typography variant="body1" color="text.secondary">
          Loading...
        </Typography>
      </Box>
    );
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Smart redirect based on role
  if (isAdmin) {
    return <Navigate to="/dashboard" replace />;
  } else {
    return <Navigate to="/dashboard" replace />;
  }
};

export default SmartRedirect;
