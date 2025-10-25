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
  Stepper,
  Step,
  StepLabel,
  Alert,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Radio,
  RadioGroup,
  FormControlLabel,
  FormLabel,
  Checkbox,
  Grid,
  InputAdornment,
  IconButton,
  LinearProgress,
  Chip,
  Paper,
  Divider,
} from '@mui/material';
import {
  Visibility,
  VisibilityOff,
  Person as PersonIcon,
  Email as EmailIcon,
  Phone as PhoneIcon,
  Home as HomeIcon,
  CreditCard as CardIcon,
  AccountBalance as BankIcon,
  Check as CheckIcon,
} from '@mui/icons-material';
import {
  RegistrationRequest,
  completeRegistration,
  validatePAN,
  validateAadhar,
  validatePhone,
  validateEmail,
  validatePassword,
  calculateAge,
  formatCurrency,
  indianStates,
} from '../services/registrationService';

const RegistrationPage: React.FC = () => {
  const navigate = useNavigate();
  
  // Step management
  const [activeStep, setActiveStep] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  
  // Form data
  const [formData, setFormData] = useState<RegistrationRequest>({
    username: '',
    password: '',
    fullName: '',
    email: '',
    phone: '',
    dateOfBirth: '2000-01-01', // Default date to avoid empty selects
    gender: 'MALE',
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
    country: 'India',
    panCard: '',
    aadharCard: '',
    accountType: 'SAVINGS',
    initialDeposit: 1000,
    nomineName: '',
    nomineeRelation: '',
    termsAccepted: false,
    skipAccount: false, // NEW: Allow users to skip account creation
  });
  
  // UI states
  const [showPassword, setShowPassword] = useState(false);
  const [passwordConfirm, setPasswordConfirm] = useState('');
  const [fieldErrors, setFieldErrors] = useState<{ [key: string]: string }>({});
  
  const steps = [
    'Account Credentials',
    'Personal Information',
    'Contact & Address',
    'Identity Verification',
    'Account Setup',
    'Review & Confirm',
  ];
  
  // Handle input changes
  const handleChange = (field: keyof RegistrationRequest, value: any) => {
    setFormData({ ...formData, [field]: value });
    // Clear field error when user types
    if (fieldErrors[field]) {
      setFieldErrors({ ...fieldErrors, [field]: '' });
    }
  };
  
  // Validation functions for each step
  const validateStep = (step: number): boolean => {
    const errors: { [key: string]: string } = {};
    
    switch (step) {
      case 0: // Account Credentials
        if (!formData.username || formData.username.length < 4) {
          errors.username = 'Username must be at least 4 characters';
        }
        if (!/^[a-zA-Z0-9_]+$/.test(formData.username)) {
          errors.username = 'Username can only contain letters, numbers, and underscores';
        }
        
        const passwordValidation = validatePassword(formData.password);
        if (!passwordValidation.isValid) {
          errors.password = passwordValidation.errors[0];
        }
        
        if (formData.password !== passwordConfirm) {
          errors.passwordConfirm = 'Passwords do not match';
        }
        break;
        
      case 1: // Personal Information
        if (!formData.fullName || formData.fullName.length < 3) {
          errors.fullName = 'Full name must be at least 3 characters';
        }
        if (!formData.dateOfBirth) {
          errors.dateOfBirth = 'Date of birth is required';
        } else {
          const age = calculateAge(formData.dateOfBirth);
          if (age < 18) {
            errors.dateOfBirth = 'You must be at least 18 years old';
          }
          if (age > 100) {
            errors.dateOfBirth = 'Please enter a valid date of birth';
          }
        }
        break;
        
      case 2: // Contact & Address
        if (!validateEmail(formData.email)) {
          errors.email = 'Please enter a valid email address';
        }
        if (!validatePhone(formData.phone)) {
          errors.phone = 'Please enter a valid 10-digit phone number';
        }
        if (!formData.addressLine1) {
          errors.addressLine1 = 'Address is required';
        }
        if (!formData.city) {
          errors.city = 'City is required';
        }
        if (!formData.state) {
          errors.state = 'State is required';
        }
        if (!formData.postalCode || !/^\d{6}$/.test(formData.postalCode)) {
          errors.postalCode = 'Please enter a valid 6-digit PIN code';
        }
        break;
        
      case 3: // Identity Verification
        if (!validatePAN(formData.panCard)) {
          errors.panCard = 'Please enter a valid PAN card number (e.g., ABCDE1234F)';
        }
        if (!validateAadhar(formData.aadharCard)) {
          errors.aadharCard = 'Please enter a valid 12-digit Aadhar number';
        }
        break;
        
      case 4: // Account Setup
        // Only validate if user is not skipping account creation
        if (!formData.skipAccount) {
          if (formData.initialDeposit < 1000) {
            errors.initialDeposit = 'Minimum initial deposit is ₹1,000';
          }
          if (formData.initialDeposit > 10000000) {
            errors.initialDeposit = 'Maximum initial deposit is ₹1,00,00,000';
          }
        }
        break;
        
      case 5: // Review & Confirm
        if (!formData.termsAccepted) {
          errors.termsAccepted = 'You must accept the terms and conditions';
        }
        break;
    }
    
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };
  
  const handleNext = () => {
    if (validateStep(activeStep)) {
      setError(null);
      setActiveStep((prev) => prev + 1);
    } else {
      setError('Please fix the errors before proceeding');
    }
  };
  
  const handleBack = () => {
    setError(null);
    setActiveStep((prev) => prev - 1);
  };
  
  const handleSubmit = async () => {
    if (!validateStep(activeStep)) {
      setError('Please fix the errors before submitting');
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      const response = await completeRegistration(formData);
      
      setSuccess(true);
      
      // Show success message and redirect after 3 seconds
      setTimeout(() => {
        if (response.hasAccount) {
          navigate('/login', {
            state: {
              message: `Account created successfully! Your account number is ${response.accountNumber}`,
              username: formData.username,
            },
          });
        } else {
          navigate('/login', {
            state: {
              message: 'Profile created successfully! You can add an account after logging in.',
              username: formData.username,
            },
          });
        }
      }, 3000);
      
    } catch (err: any) {
      setError(err.message || 'Registration failed. Please try again.');
      setLoading(false);
    }
  };
  
  // Render step content
  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Create Your Account Credentials
            </Typography>
            
            <TextField
              fullWidth
              label="Username"
              value={formData.username}
              onChange={(e) => handleChange('username', e.target.value)}
              error={!!fieldErrors.username}
              helperText={fieldErrors.username || 'Choose a unique username (min 4 characters)'}
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
              helperText={fieldErrors.password || 'Min 8 characters, with uppercase, lowercase, number, and special character'}
              sx={{ mb: 3 }}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                      {showPassword ? <VisibilityOff /> : <Visibility />}
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
            
            <TextField
              fullWidth
              label="Confirm Password"
              type={showPassword ? 'text' : 'password'}
              value={passwordConfirm}
              onChange={(e) => setPasswordConfirm(e.target.value)}
              error={!!fieldErrors.passwordConfirm}
              helperText={fieldErrors.passwordConfirm}
              sx={{ mb: 2 }}
            />
            
            {formData.password && (
              <Box sx={{ mt: 2 }}>
                <Typography variant="caption" color="text.secondary" display="block" sx={{ mb: 1 }}>
                  Password Strength:
                </Typography>
                <LinearProgress
                  variant="determinate"
                  value={calculatePasswordStrength(formData.password)}
                  sx={{
                    height: 8,
                    borderRadius: 4,
                    backgroundColor: '#e0e0e0',
                    '& .MuiLinearProgress-bar': {
                      backgroundColor: getPasswordStrengthColor(calculatePasswordStrength(formData.password)),
                    },
                  }}
                />
              </Box>
            )}
          </Box>
        );
        
      case 1:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Personal Information
            </Typography>
            
            <TextField
              fullWidth
              label="Full Name"
              value={formData.fullName}
              onChange={(e) => handleChange('fullName', e.target.value)}
              error={!!fieldErrors.fullName}
              helperText={fieldErrors.fullName || 'As per your government ID'}
              sx={{ mb: 3 }}
            />
            
            <Typography variant="body2" color="text.secondary" sx={{ mb: 1, fontWeight: 'bold' }}>
              Date of Birth *
            </Typography>
            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid item xs={4}>
                <FormControl fullWidth error={!!fieldErrors.dateOfBirth}>
                  <InputLabel>Day</InputLabel>
                  <Select
                    value={formData.dateOfBirth.split('-')[2] || ''}
                    label="Day"
                    onChange={(e) => {
                      const [year, month] = formData.dateOfBirth.split('-');
                      const day = e.target.value.padStart(2, '0');
                      handleChange('dateOfBirth', `${year || '2000'}-${month || '01'}-${day}`);
                    }}
                  >
                    {Array.from({ length: 31 }, (_, i) => i + 1).map((day) => (
                      <MenuItem key={day} value={day.toString().padStart(2, '0')}>
                        {day}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={4}>
                <FormControl fullWidth error={!!fieldErrors.dateOfBirth}>
                  <InputLabel>Month</InputLabel>
                  <Select
                    value={formData.dateOfBirth.split('-')[1] || ''}
                    label="Month"
                    onChange={(e) => {
                      const [year, , day] = formData.dateOfBirth.split('-');
                      const month = e.target.value;
                      handleChange('dateOfBirth', `${year || '2000'}-${month}-${day || '01'}`);
                    }}
                  >
                    {[
                      { value: '01', label: 'January' },
                      { value: '02', label: 'February' },
                      { value: '03', label: 'March' },
                      { value: '04', label: 'April' },
                      { value: '05', label: 'May' },
                      { value: '06', label: 'June' },
                      { value: '07', label: 'July' },
                      { value: '08', label: 'August' },
                      { value: '09', label: 'September' },
                      { value: '10', label: 'October' },
                      { value: '11', label: 'November' },
                      { value: '12', label: 'December' },
                    ].map((month) => (
                      <MenuItem key={month.value} value={month.value}>
                        {month.label}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={4}>
                <FormControl fullWidth error={!!fieldErrors.dateOfBirth}>
                  <InputLabel>Year</InputLabel>
                  <Select
                    value={formData.dateOfBirth.split('-')[0] || ''}
                    label="Year"
                    onChange={(e) => {
                      const [, month, day] = formData.dateOfBirth.split('-');
                      const year = e.target.value;
                      handleChange('dateOfBirth', `${year}-${month || '01'}-${day || '01'}`);
                    }}
                  >
                    {Array.from({ length: 100 }, (_, i) => new Date().getFullYear() - 18 - i).map((year) => (
                      <MenuItem key={year} value={year.toString()}>
                        {year}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
            {fieldErrors.dateOfBirth && (
              <Typography variant="caption" color="error" display="block" sx={{ mt: -2, mb: 2 }}>
                {fieldErrors.dateOfBirth}
              </Typography>
            )}
            {!fieldErrors.dateOfBirth && formData.dateOfBirth && (
              <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: -2, mb: 2 }}>
                You must be 18 years or older. Age: {calculateAge(formData.dateOfBirth)} years
              </Typography>
            )}
            
            <FormControl component="fieldset" sx={{ mb: 2 }}>
              <FormLabel component="legend">Gender</FormLabel>
              <RadioGroup
                row
                value={formData.gender}
                onChange={(e) => handleChange('gender', e.target.value)}
              >
                <FormControlLabel value="MALE" control={<Radio />} label="Male" />
                <FormControlLabel value="FEMALE" control={<Radio />} label="Female" />
                <FormControlLabel value="OTHER" control={<Radio />} label="Other" />
              </RadioGroup>
            </FormControl>
          </Box>
        );
        
      case 2:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Contact & Address Details
            </Typography>
            
            <TextField
              fullWidth
              label="Email Address"
              type="email"
              value={formData.email}
              onChange={(e) => handleChange('email', e.target.value)}
              error={!!fieldErrors.email}
              helperText={fieldErrors.email}
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
              onChange={(e) => handleChange('phone', e.target.value)}
              error={!!fieldErrors.phone}
              helperText={fieldErrors.phone || '10-digit mobile number'}
              sx={{ mb: 3 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <PhoneIcon />
                  </InputAdornment>
                ),
              }}
            />
            
            <Divider sx={{ my: 3 }} />
            
            <TextField
              fullWidth
              label="Address Line 1"
              value={formData.addressLine1}
              onChange={(e) => handleChange('addressLine1', e.target.value)}
              error={!!fieldErrors.addressLine1}
              helperText={fieldErrors.addressLine1}
              sx={{ mb: 2 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <HomeIcon />
                  </InputAdornment>
                ),
              }}
            />
            
            <TextField
              fullWidth
              label="Address Line 2 (Optional)"
              value={formData.addressLine2}
              onChange={(e) => handleChange('addressLine2', e.target.value)}
              sx={{ mb: 3 }}
            />
            
            <Grid container spacing={2} sx={{ mb: 3 }}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="City"
                  value={formData.city}
                  onChange={(e) => handleChange('city', e.target.value)}
                  error={!!fieldErrors.city}
                  helperText={fieldErrors.city}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <FormControl fullWidth error={!!fieldErrors.state}>
                  <InputLabel>State</InputLabel>
                  <Select
                    value={formData.state}
                    label="State"
                    onChange={(e) => handleChange('state', e.target.value)}
                  >
                    {indianStates.map((state) => (
                      <MenuItem key={state} value={state}>
                        {state}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>
            </Grid>
            
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="PIN Code"
                  value={formData.postalCode}
                  onChange={(e) => handleChange('postalCode', e.target.value)}
                  error={!!fieldErrors.postalCode}
                  helperText={fieldErrors.postalCode}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  fullWidth
                  label="Country"
                  value={formData.country}
                  onChange={(e) => handleChange('country', e.target.value)}
                  disabled
                />
              </Grid>
            </Grid>
          </Box>
        );
        
      case 3:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Identity Verification
            </Typography>
            
            <Alert severity="info" sx={{ mb: 3 }}>
              KYC documents are required as per RBI guidelines
            </Alert>
            
            <TextField
              fullWidth
              label="PAN Card Number"
              value={formData.panCard}
              onChange={(e) => handleChange('panCard', e.target.value.toUpperCase())}
              error={!!fieldErrors.panCard}
              helperText={fieldErrors.panCard || 'Format: ABCDE1234F'}
              sx={{ mb: 3 }}
              inputProps={{ maxLength: 10 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <CardIcon />
                  </InputAdornment>
                ),
              }}
            />
            
            <TextField
              fullWidth
              label="Aadhar Card Number"
              value={formData.aadharCard}
              onChange={(e) => handleChange('aadharCard', e.target.value.replace(/\D/g, ''))}
              error={!!fieldErrors.aadharCard}
              helperText={fieldErrors.aadharCard || '12-digit Aadhar number'}
              sx={{ mb: 2 }}
              inputProps={{ maxLength: 12 }}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <CardIcon />
                  </InputAdornment>
                ),
              }}
            />
          </Box>
        );
        
      case 4:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Account Setup
            </Typography>
            
            {formData.skipAccount ? (
              <Alert severity="info" sx={{ mb: 3 }}>
                <Typography variant="body1" sx={{ fontWeight: 'bold', mb: 1 }}>
                  Account Creation Skipped
                </Typography>
                <Typography variant="body2">
                  You can explore the platform and create an account later from your dashboard.
                  No worries - it only takes a minute when you're ready!
                </Typography>
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => handleChange('skipAccount', false)}
                  sx={{ mt: 2 }}
                >
                  Create Account Now Instead
                </Button>
              </Alert>
            ) : (
              <>
                <Alert severity="success" sx={{ mb: 3 }}>
                  <Typography variant="body2">
                    <strong>Optional:</strong> You can skip account creation and add it later from your dashboard.
                  </Typography>
                </Alert>
                
                <FormControl component="fieldset" sx={{ mb: 3 }}>
                  <FormLabel component="legend">Account Type</FormLabel>
                  <RadioGroup
                    value={formData.accountType}
                    onChange={(e) => handleChange('accountType', e.target.value)}
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
                            4% interest rate, perfect for personal savings
                          </Typography>
                        </Box>
                      }
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
                            No interest, ideal for business transactions
                          </Typography>
                        </Box>
                      }
                    />
                  </RadioGroup>
                </FormControl>
                
                <TextField
                  fullWidth
                  label="Initial Deposit"
                  type="number"
                  value={formData.initialDeposit}
                  onChange={(e) => handleChange('initialDeposit', Number(e.target.value))}
                  error={!!fieldErrors.initialDeposit}
                  helperText={fieldErrors.initialDeposit || `Minimum: ₹1,000 | You're depositing: ${formatCurrency(formData.initialDeposit)}`}
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
                
                <Typography variant="subtitle2" gutterBottom sx={{ mb: 2 }}>
                  Nominee Details (Optional)
                </Typography>
                
                <TextField
                  fullWidth
                  label="Nominee Name"
                  value={formData.nomineName}
                  onChange={(e) => handleChange('nomineName', e.target.value)}
                  sx={{ mb: 2 }}
                />
                
                <TextField
                  fullWidth
                  label="Relationship with Nominee"
                  value={formData.nomineeRelation}
                  onChange={(e) => handleChange('nomineeRelation', e.target.value)}
                  helperText="e.g., Father, Mother, Spouse, Child"
                  sx={{ mb: 3 }}
                />
                
                <Divider sx={{ my: 3 }} />
                
                <Button
                  fullWidth
                  variant="outlined"
                  color="secondary"
                  onClick={() => handleChange('skipAccount', true)}
                  sx={{ py: 1.5 }}
                >
                  Skip Account Creation (Add Later)
                </Button>
              </>
            )}
          </Box>
        );
        
      case 5:
        return (
          <Box>
            <Typography variant="h6" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
              Review Your Information
            </Typography>
            
            <Paper sx={{ p: 3, mb: 3, backgroundColor: '#f5f5f5' }}>
              <Grid container spacing={2}>
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Account Credentials
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                    Username: {formData.username}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Full Name
                  </Typography>
                  <Typography variant="body1">{formData.fullName}</Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Date of Birth
                  </Typography>
                  <Typography variant="body1">
                    {new Date(formData.dateOfBirth).toLocaleDateString()} ({calculateAge(formData.dateOfBirth)} years)
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
                  <Typography variant="subtitle2" color="text.secondary">
                    Address
                  </Typography>
                  <Typography variant="body1">
                    {formData.addressLine1}
                    {formData.addressLine2 && `, ${formData.addressLine2}`}, {formData.city}, {formData.state} - {formData.postalCode}
                  </Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    PAN Card
                  </Typography>
                  <Typography variant="body1">{formData.panCard}</Typography>
                </Grid>
                
                <Grid item xs={12} sm={6}>
                  <Typography variant="subtitle2" color="text.secondary">
                    Aadhar Card
                  </Typography>
                  <Typography variant="body1">{formData.aadharCard.replace(/(\d{4})(\d{4})(\d{4})/, '$1 $2 $3')}</Typography>
                </Grid>
                
                {!formData.skipAccount ? (
                  <>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="subtitle2" color="text.secondary">
                        Account Type
                      </Typography>
                      <Chip
                        label={formData.accountType}
                        color={formData.accountType === 'SAVINGS' ? 'success' : 'primary'}
                        size="small"
                      />
                    </Grid>
                    
                    <Grid item xs={12} sm={6}>
                      <Typography variant="subtitle2" color="text.secondary">
                        Initial Deposit
                      </Typography>
                      <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                        {formatCurrency(formData.initialDeposit)}
                      </Typography>
                    </Grid>
                  </>
                ) : (
                  <Grid item xs={12}>
                    <Alert severity="info">
                      <Typography variant="body2">
                        <strong>Account Creation:</strong> Skipped - You can add an account later from your dashboard
                      </Typography>
                    </Alert>
                  </Grid>
                )}
                
                {formData.nomineName && (
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" color="text.secondary">
                      Nominee
                    </Typography>
                    <Typography variant="body1">
                      {formData.nomineName} ({formData.nomineeRelation})
                    </Typography>
                  </Grid>
                )}
              </Grid>
            </Paper>
            
            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.termsAccepted}
                  onChange={(e) => handleChange('termsAccepted', e.target.checked)}
                />
              }
              label={
                <Typography variant="body2">
                  I accept the{' '}
                  <a href="#" style={{ color: '#1976d2' }}>
                    Terms and Conditions
                  </a>{' '}
                  and{' '}
                  <a href="#" style={{ color: '#1976d2' }}>
                    Privacy Policy
                  </a>
                </Typography>
              }
            />
            {fieldErrors.termsAccepted && (
              <Typography variant="caption" color="error" display="block" sx={{ ml: 4 }}>
                {fieldErrors.termsAccepted}
              </Typography>
            )}
          </Box>
        );
        
      default:
        return null;
    }
  };
  
  // Helper functions
  const calculatePasswordStrength = (password: string): number => {
    let strength = 0;
    if (password.length >= 8) strength += 20;
    if (password.length >= 12) strength += 20;
    if (/[a-z]/.test(password)) strength += 20;
    if (/[A-Z]/.test(password)) strength += 20;
    if (/[0-9]/.test(password)) strength += 10;
    if (/[!@#$%^&*]/.test(password)) strength += 10;
    return strength;
  };
  
  const getPasswordStrengthColor = (strength: number): string => {
    if (strength < 40) return '#f44336';
    if (strength < 60) return '#ff9800';
    if (strength < 80) return '#ffc107';
    return '#4caf50';
  };
  
  if (success) {
    return (
      <Container maxWidth="sm" sx={{ mt: 8 }}>
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <Box
              sx={{
                width: 80,
                height: 80,
                borderRadius: '50%',
                backgroundColor: '#4caf50',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 24px',
              }}
            >
              <CheckIcon sx={{ fontSize: 50, color: 'white' }} />
            </Box>
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#4caf50' }}>
              Registration Successful!
            </Typography>
            <Typography variant="body1" color="text.secondary" sx={{ mb: 3 }}>
              Your account has been created successfully.
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Redirecting to login page...
            </Typography>
          </CardContent>
        </Card>
      </Container>
    );
  }
  
  return (
    <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
      <Card>
        <CardContent sx={{ p: 4 }}>
          {/* Header */}
          <Box sx={{ textAlign: 'center', mb: 4 }}>
            <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
              <BankIcon sx={{ fontSize: 40, verticalAlign: 'middle', mr: 2 }} />
              Create Your Account
            </Typography>
            <Typography variant="body1" color="text.secondary">
              Join MoneyMesh Banking - Your trusted financial partner
            </Typography>
          </Box>
          
          {/* Stepper */}
          <Stepper activeStep={activeStep} sx={{ mb: 4 }} alternativeLabel>
            {steps.map((label) => (
              <Step key={label}>
                <StepLabel>{label}</StepLabel>
              </Step>
            ))}
          </Stepper>
          
          {/* Error Alert */}
          {error && (
            <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 3 }}>
              {error}
            </Alert>
          )}
          
          {/* Step Content */}
          <Box sx={{ mb: 4 }}>{renderStepContent()}</Box>
          
          {/* Navigation Buttons */}
          <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
            <Button onClick={() => navigate('/login')} disabled={loading}>
              Cancel
            </Button>
            <Box sx={{ display: 'flex', gap: 2 }}>
              {activeStep > 0 && (
                <Button onClick={handleBack} disabled={loading}>
                  Back
                </Button>
              )}
              {activeStep < steps.length - 1 ? (
                <Button variant="contained" onClick={handleNext} disabled={loading}>
                  Next
                </Button>
              ) : (
                <Button
                  variant="contained"
                  color="success"
                  onClick={handleSubmit}
                  disabled={loading || !formData.termsAccepted}
                >
                  {loading ? 'Creating Account...' : 'Create Account'}
                </Button>
              )}
            </Box>
          </Box>
          
          {/* Already have account */}
          <Box sx={{ textAlign: 'center', mt: 3 }}>
            <Typography variant="body2" color="text.secondary">
              Already have an account?{' '}
              <Button onClick={() => navigate('/login')} sx={{ textTransform: 'none' }}>
                Login here
              </Button>
            </Typography>
          </Box>
        </CardContent>
      </Card>
    </Container>
  );
};

export default RegistrationPage;
