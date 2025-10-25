import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  TextField,
  Button,
  Alert,
  FormControl,
  FormLabel,
  Radio,
  RadioGroup,
  FormControlLabel,
  InputAdornment,
  Divider,
  Paper,
  Grid,
} from '@mui/material';
import {
  AccountBalance as BankIcon,
  ArrowBack as BackIcon,
} from '@mui/icons-material';
import axios from 'axios';

interface AccountCreationRequest {
  accountType: 'SAVINGS' | 'CURRENT';
  initialDeposit: number;
  nomineName?: string;
  nomineeRelation?: string;
}

const AddAccountPage: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  
  const [formData, setFormData] = useState<AccountCreationRequest>({
    accountType: 'SAVINGS',
    initialDeposit: 1000,
    nomineName: '',
    nomineeRelation: '',
  });
  
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  
  const handleChange = (field: keyof AccountCreationRequest, value: any) => {
    setFormData({ ...formData, [field]: value });
    if (fieldErrors[field]) {
      setFieldErrors({ ...fieldErrors, [field]: '' });
    }
  };
  
  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
    }).format(amount);
  };
  
  const validate = (): boolean => {
    const errors: { [key: string]: string } = {};
    
    if (formData.initialDeposit < 1000) {
      errors.initialDeposit = 'Minimum initial deposit is ₹1,000';
    }
    if (formData.initialDeposit > 10000000) {
      errors.initialDeposit = 'Maximum initial deposit is ₹1,00,00,000';
    }
    
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validate()) {
      setError('Please fix the errors before submitting');
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem('token');
      
      const response = await axios.post(
        'http://localhost:8080/api/accounts',
        {
          accountType: formData.accountType,
          balance: formData.initialDeposit,
          nomineName: formData.nomineName || undefined,
          nomineeRelation: formData.nomineeRelation || undefined,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      
      setSuccess(true);
      
      // Redirect to dashboard after 2 seconds
      setTimeout(() => {
        navigate('/dashboard', {
          state: {
            message: `Account created successfully! Your account number is ${response.data.accountNumber}`,
          },
        });
      }, 2000);
      
    } catch (err: any) {
      console.error('Account creation error:', err);
      setError(err.response?.data?.error || 'Failed to create account. Please try again.');
      setLoading(false);
    }
  };
  
  if (success) {
    return (
      <Container maxWidth="sm" sx={{ mt: 8 }}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 5 }}>
            <BankIcon sx={{ fontSize: 60, color: '#4caf50', mb: 2 }} />
            <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#4caf50' }}>
              Account Created Successfully!
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Redirecting to your dashboard...
            </Typography>
          </CardContent>
        </Card>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Button
        startIcon={<BackIcon />}
        onClick={() => navigate('/dashboard')}
        sx={{ mb: 2 }}
      >
        Back to Dashboard
      </Button>
      
      <Card>
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <BankIcon sx={{ fontSize: 50, color: '#1976d2', mb: 2 }} />
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold' }}>
              Create Your Bank Account
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Choose your account type and make your initial deposit to get started
            </Typography>
          </Box>
          
          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}
          
          <form onSubmit={handleSubmit}>
            <Paper sx={{ p: 3, mb: 3, backgroundColor: '#f5f5f5' }}>
              <FormControl component="fieldset" sx={{ width: '100%' }}>
                <FormLabel component="legend" sx={{ mb: 2, fontWeight: 'bold' }}>
                  Account Type
                </FormLabel>
                <RadioGroup
                  value={formData.accountType}
                  onChange={(e) => handleChange('accountType', e.target.value as 'SAVINGS' | 'CURRENT')}
                >
                  <FormControlLabel
                    value="SAVINGS"
                    control={<Radio />}
                    label={
                      <Box>
                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                          Savings Account
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          4% interest rate per annum, perfect for personal savings and growth
                        </Typography>
                      </Box>
                    }
                    sx={{ mb: 2, p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}
                  />
                  <FormControlLabel
                    value="CURRENT"
                    control={<Radio />}
                    label={
                      <Box>
                        <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                          Current Account
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          No interest, unlimited transactions, ideal for business use
                        </Typography>
                      </Box>
                    }
                    sx={{ p: 2, border: '1px solid #e0e0e0', borderRadius: 1 }}
                  />
                </RadioGroup>
              </FormControl>
            </Paper>
            
            <TextField
              fullWidth
              label="Initial Deposit"
              type="number"
              value={formData.initialDeposit}
              onChange={(e) => handleChange('initialDeposit', Number(e.target.value))}
              error={!!fieldErrors.initialDeposit}
              helperText={
                fieldErrors.initialDeposit || 
                `Minimum: ₹1,000 | Amount: ${formatCurrency(formData.initialDeposit)}`
              }
              sx={{ mb: 3 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <BankIcon />
                  </InputAdornment>
                ),
              }}
            />
            
            <Divider sx={{ my: 3 }} />
            
            <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
              Nominee Details (Optional)
            </Typography>
            
            <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
              Add a nominee to ensure your funds are transferred to your loved ones in case of unforeseen circumstances.
            </Typography>
            
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Nominee Name"
                  value={formData.nomineName}
                  onChange={(e) => handleChange('nomineName', e.target.value)}
                  placeholder="e.g., John Doe"
                />
              </Grid>
              
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Relationship with Nominee"
                  value={formData.nomineeRelation}
                  onChange={(e) => handleChange('nomineeRelation', e.target.value)}
                  placeholder="e.g., Father, Mother, Spouse"
                />
              </Grid>
            </Grid>
            
            <Box sx={{ mt: 4 }}>
              <Button
                type="submit"
                variant="contained"
                size="large"
                fullWidth
                disabled={loading}
                sx={{ py: 1.5, fontSize: '1.1rem' }}
              >
                {loading ? 'Creating Account...' : 'Create Account'}
              </Button>
              
              <Button
                variant="text"
                fullWidth
                onClick={() => navigate('/dashboard')}
                sx={{ mt: 2 }}
              >
                Maybe Later
              </Button>
            </Box>
          </form>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AddAccountPage;
