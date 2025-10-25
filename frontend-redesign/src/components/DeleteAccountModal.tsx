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
  Radio,
  RadioGroup,
  FormControlLabel
} from '@mui/material';
import {
  Close as CloseIcon,
  AccountBalance as AccountIcon,
  Warning as WarningIcon
} from '@mui/icons-material';
import api from '../services/api';

interface Account {
  id: number;
  accountNumber: string;
  accountType: string;
  balance: number;
}

interface DeleteAccountModalProps {
  open: boolean;
  onClose: () => void;
  onAccountDeleted: () => void;
}

const DeleteAccountModal: React.FC<DeleteAccountModalProps> = ({ open, onClose, onAccountDeleted }) => {
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [selectedAccountId, setSelectedAccountId] = useState<number | null>(null);

  useEffect(() => {
    if (open) {
      loadAccounts();
    }
  }, [open]);

  const loadAccounts = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('[DeleteAccountModal] Loading accounts...');
      const response = await api.get('/accounts');
      console.log('[DeleteAccountModal] Accounts loaded:', response.data);
      setAccounts(response.data || []);
      if (response.data && response.data.length > 0) {
        setSelectedAccountId(response.data[0].id);
      }
    } catch (err: any) {
      console.error('[DeleteAccountModal] Failed to load accounts:', err);
      console.error('[DeleteAccountModal] Error response:', err.response?.data);
      setError(`Failed to load accounts: ${err.response?.data?.message || err.message || 'Unknown error'}`);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!selectedAccountId) {
      alert('Please select an account to delete');
      return;
    }

    const selectedAccount = accounts.find(acc => acc.id === selectedAccountId);
    if (!selectedAccount) return;

    // Check if account has balance
    if (selectedAccount.balance > 0) {
      alert(
        '🚫 Cannot Delete Account with Balance\n\n' +
        `Account Number: ${selectedAccount.accountNumber}\n` +
        `Current Balance: ₹${selectedAccount.balance.toLocaleString()}\n\n` +
        '❌ This account cannot be deleted because it has a remaining balance.\n\n' +
        '📋 To delete this account, please:\n' +
        '1. Transfer funds to another account, OR\n' +
        '2. Withdraw all funds to your external bank\n\n' +
        'Once the balance is ₹0.00, you can delete this account.'
      );
      return;
    }

    // First confirmation
    const firstConfirm = confirm(
      `⚠️ WARNING: Delete Account?\n\n` +
      `Account Number: ${selectedAccount.accountNumber}\n` +
      `Type: ${selectedAccount.accountType}\n` +
      `Balance: ₹${selectedAccount.balance.toLocaleString()}\n\n` +
      'This will permanently delete:\n' +
      '• This bank account\n' +
      '• All associated cards\n' +
      '• Transaction history for this account\n' +
      '• Any pending transfers\n\n' +
      'This action CANNOT be undone!\n\n' +
      'Click OK to continue or Cancel to keep this account.'
    );

    if (!firstConfirm) return;

    // Second confirmation - type account number
    const typedAccountNumber = prompt(
      '🔴 FINAL WARNING\n\n' +
      `To permanently delete this account, type the account number exactly:\n\n` +
      `"${selectedAccount.accountNumber}"\n\n` +
      '(Must match exactly)'
    );

    if (typedAccountNumber === null) {
      // User clicked cancel
      return;
    }

    if (typedAccountNumber.trim() !== selectedAccount.accountNumber) {
      alert(`❌ Account number did not match.\n\nYou entered: "${typedAccountNumber}"\nExpected: "${selectedAccount.accountNumber}"\n\nAccount deletion cancelled.`);
      return;
    }

    // Third confirmation - password
    const password = prompt(
      '🔴 FINAL VERIFICATION\n\n' +
      'Enter your password to confirm account deletion:'
    );

    if (!password) {
      alert('Password required. Account deletion cancelled.');
      return;
    }

    try {
      setDeleting(true);
      await api.delete(`/account/${selectedAccountId}`, {
        data: { password: password }
      });

      alert('Account deleted successfully!');
      onAccountDeleted();
      onClose();
    } catch (error: any) {
      console.error('Failed to delete account:', error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        alert('Incorrect password. Account deletion cancelled.');
      } else {
        alert(error.response?.data?.message || 'Failed to delete account. Please contact support.');
      }
    } finally {
      setDeleting(false);
    }
  };

  const formatBalance = (balance: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2
    }).format(balance);
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
          <Typography variant="h6">Delete Bank Account</Typography>
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

        {!loading && !error && accounts.length === 0 && (
          <Alert severity="info">
            No bank accounts found. You don't have any accounts to delete.
          </Alert>
        )}

        {!loading && !error && accounts.length > 0 && (
          <>
            <Alert severity="warning" sx={{ mb: 3 }} icon={<WarningIcon />}>
              <Typography variant="body2" fontWeight="600">
                Warning: Account deletion is permanent and cannot be undone!
              </Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>
                Select the account you want to delete. All associated data (cards, transactions, etc.) will also be permanently deleted.
              </Typography>
            </Alert>

            <Typography variant="subtitle1" fontWeight="600" sx={{ mb: 2 }}>
              Select Account to Delete:
            </Typography>

            <RadioGroup
              value={selectedAccountId}
              onChange={(e) => setSelectedAccountId(Number(e.target.value))}
            >
              <Box display="flex" flexDirection="column" gap={2}>
                {accounts.map((account) => (
                  <Card 
                    key={account.id} 
                    variant="outlined"
                    sx={{ 
                      cursor: 'pointer',
                      border: selectedAccountId === account.id ? '2px solid' : '1px solid',
                      borderColor: selectedAccountId === account.id ? 'primary.main' : 'divider',
                      '&:hover': {
                        boxShadow: 2
                      }
                    }}
                    onClick={() => setSelectedAccountId(account.id)}
                  >
                    <CardContent>
                      <Box display="flex" alignItems="center" gap={2}>
                        <FormControlLabel
                          value={account.id}
                          control={<Radio />}
                          label=""
                          sx={{ m: 0 }}
                        />
                        
                        <Box color="primary.main">
                          <AccountIcon fontSize="large" />
                        </Box>
                        
                        <Box flex={1}>
                          <Typography variant="subtitle1" fontWeight="600">
                            {account.accountNumber}
                          </Typography>
                          <Box display="flex" alignItems="center" gap={1} mt={0.5}>
                            <Chip 
                              label={account.accountType} 
                              size="small" 
                              color="primary"
                              variant="outlined"
                            />
                            <Typography variant="body2" color="text.secondary">
                              •
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              Balance: {formatBalance(account.balance)}
                            </Typography>
                          </Box>
                        </Box>
                      </Box>
                    </CardContent>
                  </Card>
                ))}
              </Box>
            </RadioGroup>
          </>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>Cancel</Button>
        <Button 
          variant="contained" 
          color="error"
          onClick={handleDeleteAccount}
          disabled={deleting || !selectedAccountId || accounts.length === 0}
        >
          {deleting ? 'Deleting...' : 'Delete Selected Account'}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default DeleteAccountModal;
