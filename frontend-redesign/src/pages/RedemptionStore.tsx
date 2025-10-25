import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  CircularProgress,
  Chip,
  IconButton,
  Paper,
  Divider,
  CardActionArea,
  Avatar,
  Stepper,
  Step,
  StepLabel,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from '@mui/material';
import {
  AccountBalance as BankIcon,
  Receipt as BillIcon,
  Payment as PaymentIcon,
  CardGiftcard as VoucherIcon,
  Favorite as DonationIcon,
  ShoppingCart as ProductIcon,
  Flight as TravelIcon,
  CreditCard as StatementIcon,
  Close as CloseIcon,
  Check as CheckIcon,
  ArrowForward as NextIcon,
  Stars as PointsIcon,
  MonetizationOn as CashIcon,
  Info as InfoIcon,
} from '@mui/icons-material';
import {
  getRewardPoints,
  redeemPoints,
  getRedemptionHistory,
  formatCurrency,
  pointsToCash,
  formatDate,
  RedemptionRequest,
  RedemptionResponse,
} from '../services/module12Service';

interface RedemptionOption {
  type: 'CASH' | 'BILL_PAYMENT' | 'LOAN_EMI' | 'VOUCHER' | 'DONATION' | 'PRODUCT' | 'TRAVEL' | 'STATEMENT_CREDIT';
  title: string;
  description: string;
  icon: React.ReactNode;
  minPoints: number;
  color: string;
  gradient: string;
}

const RedemptionStore: React.FC = () => {
  const [pointsData, setPointsData] = useState<any>(null);
  const [history, setHistory] = useState<RedemptionResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Dialog states
  const [redemptionDialog, setRedemptionDialog] = useState(false);
  const [selectedOption, setSelectedOption] = useState<RedemptionOption | null>(null);
  const [activeStep, setActiveStep] = useState(0);

  // Form states
  const [pointsToRedeem, setPointsToRedeem] = useState('');
  const [accountNumber, setAccountNumber] = useState('');
  const [voucherType, setVoucherType] = useState('');
  const [remarks, setRemarks] = useState('');

  const customerId = 1; // Replace with actual logged-in customer ID

  const redemptionOptions: RedemptionOption[] = [
    {
      type: 'CASH',
      title: 'Bank Transfer',
      description: 'Direct credit to your bank account (1 point = ₹0.25)',
      icon: <BankIcon sx={{ fontSize: 40 }} />,
      minPoints: 400,
      color: '#4caf50',
      gradient: 'linear-gradient(135deg, #4caf50 0%, #388e3c 100%)',
    },
    {
      type: 'BILL_PAYMENT',
      title: 'Pay Bills',
      description: 'Use points to pay utility bills instantly',
      icon: <BillIcon sx={{ fontSize: 40 }} />,
      minPoints: 200,
      color: '#2196f3',
      gradient: 'linear-gradient(135deg, #2196f3 0%, #1976d2 100%)',
    },
    {
      type: 'LOAN_EMI',
      title: 'Pay Loan EMI',
      description: 'Apply points towards your loan EMI payment',
      icon: <PaymentIcon sx={{ fontSize: 40 }} />,
      minPoints: 1000,
      color: '#ff9800',
      gradient: 'linear-gradient(135deg, #ff9800 0%, #f57c00 100%)',
    },
    {
      type: 'VOUCHER',
      title: 'Gift Vouchers',
      description: 'Amazon, Flipkart, Swiggy, Zomato vouchers',
      icon: <VoucherIcon sx={{ fontSize: 40 }} />,
      minPoints: 200,
      color: '#e91e63',
      gradient: 'linear-gradient(135deg, #e91e63 0%, #c2185b 100%)',
    },
    {
      type: 'DONATION',
      title: 'Charity Donation',
      description: 'Donate to NGOs and social causes',
      icon: <DonationIcon sx={{ fontSize: 40 }} />,
      minPoints: 100,
      color: '#f44336',
      gradient: 'linear-gradient(135deg, #f44336 0%, #d32f2f 100%)',
    },
    {
      type: 'PRODUCT',
      title: 'Product Catalog',
      description: 'Redeem for electronics, appliances, and more',
      icon: <ProductIcon sx={{ fontSize: 40 }} />,
      minPoints: 500,
      color: '#9c27b0',
      gradient: 'linear-gradient(135deg, #9c27b0 0%, #7b1fa2 100%)',
    },
    {
      type: 'TRAVEL',
      title: 'Travel Bookings',
      description: 'Flight tickets, hotel stays, and vacation packages',
      icon: <TravelIcon sx={{ fontSize: 40 }} />,
      minPoints: 2000,
      color: '#00bcd4',
      gradient: 'linear-gradient(135deg, #00bcd4 0%, #0097a7 100%)',
    },
    {
      type: 'STATEMENT_CREDIT',
      title: 'Statement Credit',
      description: 'Credit card statement adjustment',
      icon: <StatementIcon sx={{ fontSize: 40 }} />,
      minPoints: 500,
      color: '#607d8b',
      gradient: 'linear-gradient(135deg, #607d8b 0%, #455a64 100%)',
    },
  ];

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [points, redemptionHistory] = await Promise.all([
        getRewardPoints(customerId),
        getRedemptionHistory(customerId),
      ]);
      setPointsData(points);
      setHistory(redemptionHistory);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenRedemption = (option: RedemptionOption) => {
    if (pointsData && pointsData.activePoints < option.minPoints) {
      setError(`You need at least ${option.minPoints} points to redeem this option.`);
      return;
    }
    setSelectedOption(option);
    setRedemptionDialog(true);
    setActiveStep(0);
  };

  const handleNext = () => {
    setActiveStep((prev) => prev + 1);
  };

  const handleBack = () => {
    setActiveStep((prev) => prev - 1);
  };

  const handleRedemption = async () => {
    if (!selectedOption || !pointsToRedeem) {
      setError('Please enter points to redeem');
      return;
    }

    const points = parseInt(pointsToRedeem);
    if (points < selectedOption.minPoints) {
      setError(`Minimum ${selectedOption.minPoints} points required`);
      return;
    }

    if (points > (pointsData?.activePoints || 0)) {
      setError('Insufficient points');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const request: RedemptionRequest = {
        customerId,
        redemptionType: selectedOption.type,
        pointsToRedeem: points,
        accountNumber: accountNumber || undefined,
        voucherType: voucherType || undefined,
        remarks: remarks || undefined,
      };

      const response = await redeemPoints(customerId, request);
      setSuccess(
        `Redemption successful! ${formatCurrency(response.cashValue)} will be credited soon.`
      );
      setRedemptionDialog(false);
      await fetchData();
      resetForm();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setSelectedOption(null);
    setPointsToRedeem('');
    setAccountNumber('');
    setVoucherType('');
    setRemarks('');
    setActiveStep(0);
  };

  const calculateCashValue = () => {
    const points = parseInt(pointsToRedeem) || 0;
    return pointsToCash(points);
  };

  const canProceedToNext = () => {
    if (activeStep === 0) {
      return pointsToRedeem && parseInt(pointsToRedeem) >= (selectedOption?.minPoints || 0);
    }
    if (activeStep === 1) {
      if (selectedOption?.type === 'CASH' || selectedOption?.type === 'BILL_PAYMENT') {
        return accountNumber.length > 0;
      }
      if (selectedOption?.type === 'VOUCHER') {
        return voucherType.length > 0;
      }
      return true;
    }
    return true;
  };

  const steps = ['Enter Points', 'Provide Details', 'Confirm'];

  const renderStepContent = () => {
    switch (activeStep) {
      case 0:
        return (
          <Box>
            <Alert severity="info" sx={{ mb: 3 }}>
              <Typography variant="body2">
                You have <strong>{pointsData?.activePoints?.toLocaleString() || 0} points</strong> available for redemption
              </Typography>
              <Typography variant="body2">
                Minimum: {selectedOption?.minPoints} points (
                {formatCurrency(pointsToCash(selectedOption?.minPoints || 0))})
              </Typography>
            </Alert>

            <TextField
              fullWidth
              label="Points to Redeem"
              type="number"
              value={pointsToRedeem}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPointsToRedeem(e.target.value)}
              inputProps={{
                min: selectedOption?.minPoints,
                max: pointsData?.activePoints,
              }}
              helperText={`Cash Value: ${formatCurrency(calculateCashValue())}`}
            />
          </Box>
        );

      case 1:
        return (
          <Box>
            {(selectedOption?.type === 'CASH' || selectedOption?.type === 'BILL_PAYMENT' || 
              selectedOption?.type === 'STATEMENT_CREDIT') && (
              <TextField
                fullWidth
                label="Account Number"
                value={accountNumber}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => setAccountNumber(e.target.value)}
                helperText={
                  selectedOption?.type === 'CASH'
                    ? 'Bank account for credit'
                    : selectedOption?.type === 'BILL_PAYMENT'
                    ? 'Biller account number'
                    : 'Credit card number'
                }
                sx={{ mb: 2 }}
              />
            )}

            {selectedOption?.type === 'VOUCHER' && (
              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Select Voucher Type</InputLabel>
                <Select
                  value={voucherType}
                  label="Select Voucher Type"
                  onChange={(e) => setVoucherType(e.target.value)}
                >
                  <MenuItem value="AMAZON">Amazon</MenuItem>
                  <MenuItem value="FLIPKART">Flipkart</MenuItem>
                  <MenuItem value="SWIGGY">Swiggy</MenuItem>
                  <MenuItem value="ZOMATO">Zomato</MenuItem>
                  <MenuItem value="MYNTRA">Myntra</MenuItem>
                  <MenuItem value="BOOKMYSHOW">BookMyShow</MenuItem>
                </Select>
              </FormControl>
            )}

            {selectedOption?.type === 'DONATION' && (
              <FormControl fullWidth sx={{ mb: 2 }}>
                <InputLabel>Select Organization</InputLabel>
                <Select
                  value={remarks}
                  label="Select Organization"
                  onChange={(e) => setRemarks(e.target.value)}
                >
                  <MenuItem value="RED_CROSS">Indian Red Cross</MenuItem>
                  <MenuItem value="CRY">CRY - Child Rights</MenuItem>
                  <MenuItem value="HELPAGE">HelpAge India</MenuItem>
                  <MenuItem value="AKSHAYA_PATRA">Akshaya Patra</MenuItem>
                  <MenuItem value="GIVE_INDIA">GiveIndia</MenuItem>
                </Select>
              </FormControl>
            )}

            <TextField
              fullWidth
              label="Additional Notes (Optional)"
              multiline
              rows={3}
              value={remarks}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setRemarks(e.target.value)}
              placeholder="Any specific instructions..."
            />
          </Box>
        );

      case 2:
        return (
          <Box>
            <Paper sx={{ p: 3, bgcolor: '#f5f5f5' }}>
              <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
                Redemption Summary
              </Typography>
              <Divider sx={{ my: 2 }} />
              <Grid container spacing={2}>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Redemption Type
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                    {selectedOption?.title}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Points to Redeem
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                    {parseInt(pointsToRedeem).toLocaleString()} points
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Cash Value
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                    {formatCurrency(calculateCashValue())}
                  </Typography>
                </Grid>
                <Grid item xs={6}>
                  <Typography variant="body2" color="text.secondary">
                    Remaining Points
                  </Typography>
                  <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                    {((pointsData?.activePoints || 0) - parseInt(pointsToRedeem)).toLocaleString()}
                  </Typography>
                </Grid>
                {accountNumber && (
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Account Number
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {accountNumber}
                    </Typography>
                  </Grid>
                )}
                {voucherType && (
                  <Grid item xs={12}>
                    <Typography variant="body2" color="text.secondary">
                      Voucher Type
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {voucherType}
                    </Typography>
                  </Grid>
                )}
              </Grid>
            </Paper>

            <Alert severity="warning" sx={{ mt: 2 }}>
              <Typography variant="body2">
                Please verify all details. Redemptions cannot be reversed once processed.
              </Typography>
            </Alert>
          </Box>
        );

      default:
        return null;
    }
  };

  if (loading && !pointsData) {
    return (
      <Container maxWidth="xl" sx={{ mt: 4, display: 'flex', justifyContent: 'center', minHeight: '60vh' }}>
        <CircularProgress size={60} />
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          <PointsIcon sx={{ mr: 2, fontSize: 40, verticalAlign: 'middle' }} />
          Redemption Store
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Convert your reward points into exciting benefits
        </Typography>
      </Box>

      {/* Alerts */}
      {error && (
        <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert severity="success" onClose={() => setSuccess(null)} sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      {/* Points Summary */}
      <Card sx={{ mb: 4, background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)', color: 'white' }}>
        <CardContent>
          <Grid container spacing={3} alignItems="center">
            <Grid item xs={12} md={4}>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.3)', width: 60, height: 60, mr: 2 }}>
                  <PointsIcon sx={{ fontSize: 30 }} />
                </Avatar>
                <Box>
                  <Typography variant="caption" sx={{ opacity: 0.9 }}>
                    Available Points
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                    {pointsData?.activePoints?.toLocaleString() || 0}
                  </Typography>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} md={4}>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.3)', width: 60, height: 60, mr: 2 }}>
                  <CashIcon sx={{ fontSize: 30 }} />
                </Avatar>
                <Box>
                  <Typography variant="caption" sx={{ opacity: 0.9 }}>
                    Cash Value
                  </Typography>
                  <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
                    {formatCurrency(pointsData?.pointValue || 0)}
                  </Typography>
                </Box>
              </Box>
            </Grid>
            <Grid item xs={12} md={4}>
              <Box>
                <Typography variant="body2" sx={{ opacity: 0.9, mb: 1 }}>
                  💡 1 Point = ₹0.25
                </Typography>
                <Typography variant="caption" sx={{ opacity: 0.8 }}>
                  Minimum redemption: 100 points (₹25)
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Redemption Options */}
      <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold', mb: 3 }}>
        Choose Redemption Option
      </Typography>
      <Grid container spacing={3} sx={{ mb: 4 }}>
        {redemptionOptions.map((option, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card
              sx={{
                height: '100%',
                transition: 'all 0.3s',
                '&:hover': {
                  transform: 'translateY(-8px)',
                  boxShadow: 6,
                },
              }}
            >
              <CardActionArea
                onClick={() => handleOpenRedemption(option)}
                disabled={pointsData && pointsData.activePoints < option.minPoints}
              >
                <CardContent>
                  <Box
                    sx={{
                      background: option.gradient,
                      color: 'white',
                      p: 2,
                      borderRadius: 2,
                      display: 'flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      mb: 2,
                      height: 100,
                    }}
                  >
                    {option.icon}
                  </Box>
                  <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
                    {option.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mb: 2, minHeight: 40 }}>
                    {option.description}
                  </Typography>
                  <Chip
                    label={`Min: ${option.minPoints} points`}
                    size="small"
                    sx={{
                      bgcolor: `${option.color}20`,
                      color: option.color,
                      fontWeight: 'bold',
                    }}
                  />
                  {pointsData && pointsData.activePoints < option.minPoints && (
                    <Chip
                      label="Insufficient points"
                      size="small"
                      color="error"
                      sx={{ ml: 1 }}
                    />
                  )}
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Redemption History */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold', mb: 2 }}>
            Redemption History
          </Typography>
          {history.length > 0 ? (
            <Grid container spacing={2}>
              {history.map((redemption, index) => (
                <Grid item xs={12} md={6} key={index}>
                  <Paper sx={{ p: 2, border: '1px solid #e0e0e0' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                      <Box>
                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold' }}>
                          {redemption.redemptionType.replace(/_/g, ' ')}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(redemption.redemptionDate)}
                        </Typography>
                      </Box>
                      <Chip
                        label={redemption.status}
                        size="small"
                        color={
                          redemption.status === 'COMPLETED'
                            ? 'success'
                            : redemption.status === 'PROCESSING'
                            ? 'info'
                            : redemption.status === 'PENDING'
                            ? 'warning'
                            : 'error'
                        }
                      />
                    </Box>
                    <Divider sx={{ my: 1 }} />
                    <Grid container spacing={1}>
                      <Grid item xs={6}>
                        <Typography variant="caption" color="text.secondary">
                          Points Redeemed
                        </Typography>
                        <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                          {redemption.pointsRedeemed.toLocaleString()}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="caption" color="text.secondary">
                          Cash Value
                        </Typography>
                        <Typography variant="body2" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                          {formatCurrency(redemption.cashValue)}
                        </Typography>
                      </Grid>
                    </Grid>
                    {redemption.transactionReference && (
                      <Typography variant="caption" color="text.secondary" display="block" sx={{ mt: 1 }}>
                        Ref: {redemption.transactionReference}
                      </Typography>
                    )}
                  </Paper>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Box sx={{ textAlign: 'center', py: 4 }}>
              <InfoIcon sx={{ fontSize: 60, color: '#e0e0e0', mb: 2 }} />
              <Typography variant="body1" color="text.secondary">
                No redemption history yet. Start redeeming your points!
              </Typography>
            </Box>
          )}
        </CardContent>
      </Card>

      {/* Redemption Dialog */}
      <Dialog
        open={redemptionDialog}
        onClose={() => setRedemptionDialog(false)}
        maxWidth="md"
        fullWidth
      >
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Box
                sx={{
                  background: selectedOption?.gradient,
                  color: 'white',
                  p: 1,
                  borderRadius: 1,
                  mr: 2,
                  display: 'flex',
                  fontSize: 30,
                }}
              >
                {selectedOption?.icon}
              </Box>
              <Box>
                <Typography variant="h6">{selectedOption?.title}</Typography>
                <Typography variant="caption" color="text.secondary">
                  {selectedOption?.description}
                </Typography>
              </Box>
            </Box>
            <IconButton onClick={() => setRedemptionDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
              {steps.map((label) => (
                <Step key={label}>
                  <StepLabel>{label}</StepLabel>
                </Step>
              ))}
            </Stepper>

            {renderStepContent()}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRedemptionDialog(false)}>Cancel</Button>
          {activeStep > 0 && (
            <Button onClick={handleBack}>Back</Button>
          )}
          {activeStep < steps.length - 1 ? (
            <Button
              onClick={handleNext}
              variant="contained"
              disabled={!canProceedToNext()}
              endIcon={<NextIcon />}
            >
              Next
            </Button>
          ) : (
            <Button
              onClick={handleRedemption}
              variant="contained"
              color="success"
              disabled={loading}
              startIcon={loading ? <CircularProgress size={20} /> : <CheckIcon />}
            >
              {loading ? 'Processing...' : 'Confirm Redemption'}
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default RedemptionStore;
