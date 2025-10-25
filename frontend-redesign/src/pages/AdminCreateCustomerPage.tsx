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
  Stepper,
  Step,
  StepLabel,
  FormControlLabel,
  Checkbox,
  Grid,
  InputAdornment,
  IconButton,
  Divider,
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  ArrowForward as ForwardIcon,
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  AccountBalance as BankIcon,
  Visibility,
  VisibilityOff,
  Check as CheckIcon,
} from '@mui/icons-material';
import axios from 'axios';

interface CustomerAccountForm {
  fullName: string;
  email: string;
  phone: string;
  createLoginCredentials: boolean;
  username: string;
  password: string;
  initialDeposit: number;
}

const AdminCreateCustomerPage: React.FC = () => {
  const navigate = useNavigate();
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const [formData, setFormData] = useState<CustomerAccountForm>({
    fullName: '',
    email: '',
    phone: '',
    createLoginCredentials: true,
    username: '',
    password: '',
    initialDeposit: 1000,
  });

  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  const [responseData, setResponseData] = useState<any>(null);

  const steps = ['Customer Details', 'Login Credentials (Optional)', 'Account Setup', 'Review & Confirm'];

  const handleChange = (field: keyof CustomerAccountForm, value: any) => {
    setFormData({ ...formData, [field]: value });
    if (fieldErrors[field]) {
      setFieldErrors({ ...fieldErrors, [field]: '' });
    }
  };

  const validateStep = (step: number): boolean => {
    const errors: { [key: string]: string } = {};

    switch (step) {
      case 0: // Customer Details
        if (!formData.fullName || formData.fullName.length < 3) {
          errors.fullName = 'Full name must be at least 3 characters';
        }
        if (!formData.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
          errors.email = 'Please enter a valid email address';
        }
        if (!formData.phone || !/^\d{10}$/.test(formData.phone)) {
          errors.phone = 'Please enter a valid 10-digit phone number';
        }
        break;

      case 1: // Login Credentials
        if (formData.createLoginCredentials) {
          if (!formData.username || formData.username.length < 4) {
            errors.username = 'Username must be at least 4 characters';
          }
          if (!formData.password || formData.password.length < 6) {
            errors.password = 'Password must be at least 6 characters';
          }
        }
        break;

      case 2: // Account Setup
        if (formData.initialDeposit < 0) {
          errors.initialDeposit = 'Initial deposit cannot be negative';
        }
        break;
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleNext = () => {
    if (validateStep(activeStep)) {
      setActiveStep((prev) => prev + 1);
      setError(null);
    }
  };

  const handleBack = () => {
    setActiveStep((prev) => prev - 1);
    setError(null);
  };

  const handleSubmit = async () => {
    if (!validateStep(activeStep)) {
      setError('Please fix the errors before submitting');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const token = localStorage.getItem('token');

      const requestData = {
        fullName: formData.fullName,
        email: formData.email,
        phone: formData.phone,
        username: formData.createLoginCredentials ? formData.username : null,
        password: formData.createLoginCredentials ? formData.password : null,
        initialDeposit: formData.initialDeposit,
      };

      const response = await axios.post(
        'http://localhost:8080/api/admin/customers/create-with-account',
        requestData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setResponseData(response.data);
      setSuccess(true);

    } catch (err: any) {
      console.error('Customer creation error:', err);
      setError(err.response?.data?.error || 'Failed to create customer and account. Please try again.');
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
    }).format(amount);
  };

  if (success && responseData) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 5 }}>
            <CheckIcon sx={{ fontSize: 80, color: '#4caf50', mb: 2 }} />
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#4caf50' }}>
              Customer & Account Created Successfully!
            </Typography>

            <Box sx={{ mt: 4, p: 3, backgroundColor: '#f5f5f5', borderRadius: 2, textAlign: 'left' }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Customer Name
                  </Typography>
                  <Typography variant="h6">{responseData.customerName}</Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Customer ID
                  </Typography>
                  <Typography variant="body1">#{responseData.customerId}</Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Account Number
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
                    {responseData.accountNumber}
                  </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Initial Balance
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                    {formatCurrency(responseData.balance)}
                  </Typography>
                </Grid>

                {responseData.loginEnabled && (
                  <>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="subtitle2" color="text.secondary">
                        Username
                      </Typography>
                      <Typography variant="body1">{responseData.username}</Typography>
                    </Grid>
                    <Grid item xs={12}>
                      <Alert severity="info" sx={{ mt: 1 }}>
                        Login credentials created. Customer can now log in to the banking system.
                      </Alert>
                    </Grid>
                  </>
                )}

                {!responseData.loginEnabled && (
                  <Grid item xs={12}>
                    <Alert severity="warning">
                      No login credentials created. Customer can only access via admin assistance.
                    </Alert>
                  </Grid>
                )}
              </Grid>
            </Box>

            <Box sx={{ mt: 4, display: 'flex', gap: 2, justifyContent: 'center' }}>
              <Button
                variant="contained"
                onClick={() => navigate('/admin/customers')}
                size="large"
              >
                Back to Customers
              </Button>
              <Button
                variant="outlined"
                onClick={() => {
                  setSuccess(false);
                  setResponseData(null);
                  setActiveStep(0);
                  setFormData({
                    fullName: '',
                    email: '',
                    phone: '',
                    createLoginCredentials: true,
                    username: '',
                    password: '',
                    initialDeposit: 1000,
                  });
                }}
                size="large"
              >
                Create Another Customer
              </Button>
            </Box>
          </CardContent>
        </Card>
      </Container>
    );
  }

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Customer Information
            </Typography>

            <TextField
              fullWidth
              label="Full Name"
              value={formData.fullName}
              onChange={(e) => handleChange('fullName', e.target.value)}
              error={!!fieldErrors.fullName}
              helperText={fieldErrors.fullName || 'Enter customer\'s full legal name'}
              sx={{ mb: 3 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PersonIcon />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              fullWidth
              label="Email Address"
              type="email"
              value={formData.email}
              onChange={(e) => handleChange('email', e.target.value)}
              error={!!fieldErrors.email}
              helperText={fieldErrors.email || 'Customer\'s email for communication'}
              sx={{ mb: 3 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <EmailIcon />
                  </InputAdornment>
                ),
              }}
            />

            <TextField
              fullWidth
              label="Phone Number"
              value={formData.phone}
              onChange={(e) => handleChange('phone', e.target.value.replace(/\D/g, ''))}
              error={!!fieldErrors.phone}
              helperText={fieldErrors.phone || '10-digit mobile number'}
              sx={{ mb: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PhoneIcon />
                  </InputAdornment>
                ),
              }}
            />
          </Box>
        );

      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 2, fontWeight: 'bold' }}>
              Login Credentials
            </Typography>

            <Alert severity="info" sx={{ mb: 3 }}>
              If you create login credentials, the customer will be able to access the banking system online.
              You can skip this if the customer will only use in-person banking.
            </Alert>

            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.createLoginCredentials}
                  onChange={(e) => handleChange('createLoginCredentials', e.target.checked)}
                />
              }
              label="Create login credentials for online banking"
              sx={{ mb: 3 }}
            />

            {formData.createLoginCredentials && (
              <>
                <TextField
                  fullWidth
                  label="Username"
                  value={formData.username}
                  onChange={(e) => handleChange('username', e.target.value)}
                  error={!!fieldErrors.username}
                  helperText={fieldErrors.username || 'Minimum 4 characters'}
                  sx={{ mb: 3 }}
                  InputProps={{
                    startAdornment: (
                      <InputAdornment position="start">
                        <PersonIcon />
                      </InputAdornment>
                    ),
                  }}
                />

                <TextField
                  fullWidth
                  label="Password"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={(e) => handleChange('password', e.target.value)}
                  error={!!fieldErrors.password}
                  helperText={fieldErrors.password || 'Minimum 6 characters'}
                  InputProps={{
                    endAdornment: (
                      <InputAdornment position="end">
                        <IconButton
                          onClick={() => setShowPassword(!showPassword)}
                          edge="end"
                        >
                          {showPassword ? <VisibilityOff /> : <Visibility />}
                        </IconButton>
                      </InputAdornment>
                    ),
                  }}
                />
              </>
            )}
          </Box>
        );

      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Initial Account Setup
            </Typography>

            <Alert severity="success" sx={{ mb: 3 }}>
              A savings account will be created automatically for this customer.
            </Alert>

            <TextField
              fullWidth
              label="Initial Deposit"
              type="number"
              value={formData.initialDeposit}
              onChange={(e) => handleChange('initialDeposit', Number(e.target.value))}
              error={!!fieldErrors.initialDeposit}
              helperText={
                fieldErrors.initialDeposit ||
                `Initial deposit amount: ${formatCurrency(formData.initialDeposit)}`
              }
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <BankIcon />
                  </InputAdornment>
                ),
              }}
            />
          </Box>
        );

      case 3:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Review & Confirm
            </Typography>

            <Card sx={{ p: 3, backgroundColor: '#f5f5f5' }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Customer Name
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                    {formData.fullName}
                  </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Email
                  </Typography>
                  <Typography variant="body1">{formData.email}</Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Phone
                  </Typography>
                  <Typography variant="body1">{formData.phone}</Typography>
                </Grid>

                <Grid item xs={12}>
                  <Divider sx={{ my: 1 }} />
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Online Banking
                  </Typography>
                  <Typography variant="body1">
                    {formData.createLoginCredentials ? 'Enabled' : 'Disabled'}
                  </Typography>
                </Grid>

                {formData.createLoginCredentials && (
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Username
                    </Typography>
                    <Typography variant="body1">{formData.username}</Typography>
                  </Grid>
                )}

                <Grid item xs={12}>
                  <Divider sx={{ my: 1 }} />
                </Grid>

                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Initial Deposit
                  </Typography>
                  <Typography variant="h5" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                    {formatCurrency(formData.initialDeposit)}
                  </Typography>
                </Grid>
              </Grid>
            </Card>
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Button
        startIcon={<BackIcon />}
        onClick={() => navigate('/admin/customers')}
        sx={{ mb: 2 }}
      >
        Back to Customers
      </Button>

      <Card>
        <CardContent sx={{ p: 4 }}>
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <BankIcon sx={{ fontSize: 50, color: '#1976d2', mb: 2 }} />
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold' }}>
              Create New Customer & Account
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Onboard a new customer and create their first account
            </Typography>
          </Box>

          <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>

          {error && (
            <Alert severity="error" sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}

          {renderStepContent()}

          <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 4 }}>
            <Button
              onClick={handleBack}
              disabled={activeStep === 0 || loading}
              startIcon={<BackIcon />}
            >
              Back
            </Button>

            {activeStep === steps.length - 1 ? (
              <Button
                variant="contained"
                onClick={handleSubmit}
                disabled={loading}
                size="large"
                startIcon={<CheckIcon />}
              >
                {loading ? 'Creating...' : 'Create Customer & Account'}
              </Button>
            ) : (
              <Button
                variant="contained"
                onClick={handleNext}
                endIcon={<ForwardIcon />}
              >
                Next
              </Button>
            )}
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default AdminCreateCustomerPage;
