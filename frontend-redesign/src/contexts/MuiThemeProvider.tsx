import React, { useMemo } from 'react';
import { ThemeProvider as MuiThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { useTheme } from './ThemeContext';

export const CustomMuiThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { theme } = useTheme();
  
  const muiTheme = useMemo(
    () =>
      createTheme({
        palette: {
          mode: theme === 'DARK' ? 'dark' : 'light',
          primary: {
            main: theme === 'DARK' ? '#6366f1' : '#1976d2',
            light: theme === 'DARK' ? '#818cf8' : '#42a5f5',
            dark: theme === 'DARK' ? '#4f46e5' : '#1565c0',
          },
          secondary: {
            main: theme === 'DARK' ? '#a855f7' : '#9c27b0',
          },
          background: {
            default: theme === 'DARK' ? '#111827' : '#f5f5f5',
            paper: theme === 'DARK' ? '#1f2937' : '#ffffff',
          },
          text: {
            primary: theme === 'DARK' ? '#f9fafb' : '#000000',
            secondary: theme === 'DARK' ? '#d1d5db' : '#666666',
          },
          divider: theme === 'DARK' ? '#374151' : '#e0e0e0',
          error: {
            main: theme === 'DARK' ? '#ef4444' : '#dc2626',
          },
          success: {
            main: theme === 'DARK' ? '#22c55e' : '#16a34a',
          },
          warning: {
            main: theme === 'DARK' ? '#f97316' : '#ea580c',
          },
          action: {
            hover: theme === 'DARK' ? '#21262d' : '#f1f3f5',
            selected: theme === 'DARK' ? '#30363d' : '#e3f2fd',
            focus: theme === 'DARK' ? '#21262d' : '#f1f3f5',
          },
        },
        components: {
          MuiButtonBase: {
            defaultProps: {
              disableRipple: true, // Disable ripple effect globally
            },
            styleOverrides: {
              root: {
                '&:focus': {
                  outline: 'none',
                },
                '&:focus-visible': {
                  outline: 'none',
                },
              },
            },
          },
          MuiDrawer: {
            styleOverrides: {
              paper: {
                backgroundColor: theme === 'DARK' ? '#161b22' : '#ffffff',
                color: theme === 'DARK' ? '#f0f6fc' : '#000000',
                borderRight: `1px solid ${theme === 'DARK' ? '#30363d' : '#e0e0e0'}`,
              },
            },
          },
          MuiAppBar: {
            styleOverrides: {
              root: {
                backgroundColor: theme === 'DARK' ? '#161b22' : '#1976d2',
                color: '#ffffff',
              },
            },
          },
          MuiListItemButton: {
            styleOverrides: {
              root: {
                color: theme === 'DARK' ? '#c9d1d9' : '#000000',
                transition: 'background-color 0.2s ease, color 0.2s ease',
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#21262d !important' : '#f1f3f5 !important',
                  color: theme === 'DARK' ? '#f0f6fc !important' : '#000000 !important',
                },
                '&:focus': {
                  outline: 'none',
                  backgroundColor: theme === 'DARK' ? '#21262d' : '#f1f3f5',
                },
                '&:active': {
                  outline: 'none',
                  backgroundColor: theme === 'DARK' ? '#30363d' : '#e3f2fd',
                },
                '&.Mui-selected': {
                  backgroundColor: theme === 'DARK' ? '#30363d !important' : '#e3f2fd !important',
                  color: theme === 'DARK' ? '#f0f6fc !important' : '#1976d2 !important',
                  '&:hover': {
                    backgroundColor: theme === 'DARK' ? '#30363d !important' : '#bbdefb !important',
                    color: theme === 'DARK' ? '#f0f6fc !important' : '#1976d2 !important',
                  },
                  '&:focus': {
                    backgroundColor: theme === 'DARK' ? '#30363d' : '#e3f2fd',
                  },
                },
                '& .MuiListItemIcon-root': {
                  color: theme === 'DARK' ? '#c9d1d9' : 'inherit',
                },
                '&.Mui-selected .MuiListItemIcon-root': {
                  color: theme === 'DARK' ? '#f9fafb' : '#1976d2',
                },
                '&:hover .MuiListItemIcon-root': {
                  color: theme === 'DARK' ? '#f9fafb' : '#000000',
                },
              },
            },
          },
          MuiPaper: {
            styleOverrides: {
              root: {
                backgroundImage: 'none',
              },
            },
          },
          MuiCard: {
            styleOverrides: {
              root: {
                backgroundColor: theme === 'DARK' ? '#1f2937' : '#ffffff',
                backgroundImage: 'none',
              },
            },
          },
          MuiTextField: {
            styleOverrides: {
              root: {
                '& .MuiOutlinedInput-root': {
                  backgroundColor: theme === 'DARK' ? '#374151' : '#ffffff',
                  '& fieldset': {
                    borderColor: theme === 'DARK' ? '#4b5563' : '#d1d5db',
                  },
                  '&:hover fieldset': {
                    borderColor: theme === 'DARK' ? '#6b7280' : '#9ca3af',
                  },
                },
              },
            },
          },
          MuiMenu: {
            styleOverrides: {
              paper: {
                backgroundColor: theme === 'DARK' ? '#1f2937' : '#ffffff',
              },
            },
          },
          MuiMenuItem: {
            styleOverrides: {
              root: {
                color: theme === 'DARK' ? '#f9fafb' : '#000000',
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#374151' : '#f5f5f5',
                  color: theme === 'DARK' ? '#f9fafb' : '#000000',
                },
                '&.Mui-selected': {
                  backgroundColor: theme === 'DARK' ? '#4b5563' : '#e3f2fd',
                  color: theme === 'DARK' ? '#f9fafb' : '#1976d2',
                  '&:hover': {
                    backgroundColor: theme === 'DARK' ? '#4b5563' : '#bbdefb',
                    color: theme === 'DARK' ? '#f9fafb' : '#1976d2',
                  },
                },
              },
            },
          },
          MuiButton: {
            styleOverrides: {
              root: {
                textTransform: 'none',
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#4f46e5' : '#1565c0',
                },
              },
              contained: {
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#4f46e5' : '#1565c0',
                  color: '#ffffff',
                },
              },
              outlined: {
                borderColor: theme === 'DARK' ? '#4b5563' : '#d1d5db',
                color: theme === 'DARK' ? '#f9fafb' : '#000000',
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#374151' : '#f5f5f5',
                  borderColor: theme === 'DARK' ? '#6b7280' : '#9ca3af',
                },
              },
            },
          },
          MuiIconButton: {
            styleOverrides: {
              root: {
                color: theme === 'DARK' ? '#f9fafb' : 'inherit',
                '&:hover': {
                  backgroundColor: theme === 'DARK' ? '#374151' : 'rgba(0, 0, 0, 0.04)',
                },
              },
            },
          },
        },
        typography: {
          fontFamily: 'Arial, sans-serif',
        },
      }),
    [theme]
  );

  return (
    <MuiThemeProvider theme={muiTheme}>
      <CssBaseline />
      {children}
    </MuiThemeProvider>
  );
};
