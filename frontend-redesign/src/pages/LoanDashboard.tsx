import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  CircularProgress,
  LinearProgress,
  Divider,
  IconButton,
  Tooltip,
} from '@mui/material';
import {
  AccountBalance as LoanIcon,
  Payment as PaymentIcon,
  TrendingDown as PrepaymentIcon,
  Build as RestructureIcon,
  CheckCircle as PaidIcon,
  Schedule as UpcomingIcon,
  Warning as OverdueIcon,
  Calculate as CalculateIcon,
  Close as CloseIcon,
  AttachMoney as MoneyIcon,
  CalendarToday as CalendarIcon,
  Info as InfoIcon,
} from '@mui/icons-material';
import {
  getEmiSchedule,
  recordEmiPayment,
  calculatePrepayment,
  processPrepayment,
  requestRestructure,
  calculateForeclosure,
  processForeclosure,
  formatCurrency,
  formatDate,
  EmiScheduleDto,
  PrepaymentRequest,
  RestructureRequest,
  ForeclosureRequest,
} from '../services/module12Service';

interface Loan {
  id: number;
  accountNumber: string;
  loanType: string;
  principalAmount: number;
  interestRate: number;
  tenure: number;
  emi: number;
  outstandingAmount: number;
  status: string;
  disbursementDate: string;
}

const LoanDashboard: React.FC = () => {
  const [loans, setLoans] = useState<Loan[]>([]);
  const [selectedLoan, setSelectedLoan] = useState<Loan | null>(null);
  const [emiSchedule, setEmiSchedule] = useState<EmiScheduleDto[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // Dialog states
  const [paymentDialog, setPaymentDialog] = useState(false);
  const [prepaymentDialog, setPrepaymentDialog] = useState(false);
  const [restructureDialog, setRestructureDialog] = useState(false);
  const [foreclosureDialog, setForeclosureDialog] = useState(false);

  // Form states
  const [selectedEmi, setSelectedEmi] = useState<EmiScheduleDto | null>(null);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [paymentReference, setPaymentReference] = useState('');
  const [prepaymentType, setPrepaymentType] = useState<'PARTIAL' | 'FULL'>('PARTIAL');
  const [prepaymentAmount, setPrepaymentAmount] = useState('');
  const [prepaymentPreview, setPrepaymentPreview] = useState<any>(null);
  const [restructureReason, setRestructureReason] = useState('');
  const [requestedTenure, setRequestedTenure] = useState('');
  const [foreclosureDetails, setForeclosureDetails] = useState<any>(null);

  const customerId = 1; // Replace with actual logged-in customer ID

  // Mock loan data - Replace with actual API call
  useEffect(() => {
    const mockLoans: Loan[] = [
      {
        id: 1,
        accountNumber: 'LN123456789',
        loanType: 'Home Loan',
        principalAmount: 5000000,
        interestRate: 8.5,
        tenure: 240,
        emi: 43391,
        outstandingAmount: 4500000,
        status: 'ACTIVE',
        disbursementDate: '2023-01-15',
      },
      {
        id: 2,
        accountNumber: 'LN987654321',
        loanType: 'Personal Loan',
        principalAmount: 500000,
        interestRate: 12.0,
        tenure: 60,
        emi: 11122,
        outstandingAmount: 350000,
        status: 'ACTIVE',
        disbursementDate: '2024-06-01',
      },
    ];
    setLoans(mockLoans);
    if (mockLoans.length > 0) {
      setSelectedLoan(mockLoans[0]);
    }
  }, []);

  // Fetch EMI schedule when loan is selected
  useEffect(() => {
    if (selectedLoan) {
      fetchEmiSchedule(selectedLoan.id);
    }
  }, [selectedLoan]);

  const fetchEmiSchedule = async (loanId: number) => {
    try {
      setLoading(true);
      setError(null);
      const schedule = await getEmiSchedule(loanId);
      setEmiSchedule(schedule);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handlePayEmi = async () => {
    if (!selectedEmi || !paymentAmount || !paymentReference) {
      setError('Please fill all payment details');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      await recordEmiPayment(selectedEmi.id, parseFloat(paymentAmount), paymentReference);
      setSuccess('EMI payment recorded successfully! You earned 500 reward points.');
      setPaymentDialog(false);
      if (selectedLoan) {
        fetchEmiSchedule(selectedLoan.id);
      }
      resetPaymentForm();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCalculatePrepayment = async () => {
    if (!selectedLoan || !prepaymentAmount) {
      setError('Please enter prepayment amount');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const preview = await calculatePrepayment(
        selectedLoan.id,
        prepaymentType,
        parseFloat(prepaymentAmount)
      );
      setPrepaymentPreview(preview);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleProcessPrepayment = async () => {
    if (!selectedLoan || !prepaymentAmount || !paymentReference) {
      setError('Please fill all prepayment details');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const request: PrepaymentRequest = {
        loanId: selectedLoan.id,
        customerId,
        prepaymentType,
        amount: parseFloat(prepaymentAmount),
        paymentReference,
      };
      const response = await processPrepayment(request);
      setSuccess(
        `Prepayment processed successfully! Interest saved: ${formatCurrency(
          response.interestSaved
        )}. Cashback awarded: 2% of prepayment amount.`
      );
      setPrepaymentDialog(false);
      if (selectedLoan) {
        fetchEmiSchedule(selectedLoan.id);
      }
      resetPrepaymentForm();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleRequestRestructure = async () => {
    if (!selectedLoan || !restructureReason) {
      setError('Please fill restructure details');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const request: RestructureRequest = {
        loanId: selectedLoan.id,
        customerId,
        reason: restructureReason as any,
        requestedTenure: requestedTenure ? parseInt(requestedTenure) : undefined,
      };
      await requestRestructure(request);
      setSuccess('Restructure request submitted successfully! Our team will review it shortly.');
      setRestructureDialog(false);
      resetRestructureForm();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCalculateForeclosure = async () => {
    if (!selectedLoan) return;

    try {
      setLoading(true);
      setError(null);
      const details = await calculateForeclosure(selectedLoan.id);
      setForeclosureDetails(details);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleProcessForeclosure = async () => {
    if (!selectedLoan || !paymentReference) {
      setError('Please enter payment reference');
      return;
    }

    try {
      setLoading(true);
      setError(null);
      const request: ForeclosureRequest = {
        loanId: selectedLoan.id,
        customerId,
        requestedDate: new Date().toISOString().split('T')[0],
        paymentReference,
      };
      const response = await processForeclosure(request);
      setSuccess(
        `Loan foreclosed successfully! Interest saved: ${formatCurrency(
          response.interestSaved
        )}. You earned 1000 bonus points!`
      );
      setForeclosureDialog(false);
      resetForeclosureForm();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const resetPaymentForm = () => {
    setSelectedEmi(null);
    setPaymentAmount('');
    setPaymentReference('');
  };

  const resetPrepaymentForm = () => {
    setPrepaymentAmount('');
    setPrepaymentPreview(null);
    setPaymentReference('');
  };

  const resetRestructureForm = () => {
    setRestructureReason('');
    setRequestedTenure('');
  };

  const resetForeclosureForm = () => {
    setForeclosureDetails(null);
    setPaymentReference('');
  };

  const getEmiStatusColor = (emi: EmiScheduleDto) => {
    if (emi.isPaid) return 'success';
    if (emi.daysOverdue > 0) return 'error';
    const dueDate = new Date(emi.dueDate);
    const today = new Date();
    const daysUntilDue = Math.ceil((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    if (daysUntilDue <= 3) return 'warning';
    return 'info';
  };

  const getEmiStatusIcon = (emi: EmiScheduleDto) => {
    if (emi.isPaid) return <PaidIcon />;
    if (emi.daysOverdue > 0) return <OverdueIcon />;
    return <UpcomingIcon />;
  };

  const calculateProgress = () => {
    if (!selectedLoan) return 0;
    const paidAmount = selectedLoan.principalAmount - selectedLoan.outstandingAmount;
    return (paidAmount / selectedLoan.principalAmount) * 100;
  };

  const getUpcomingEmis = () => {
    return emiSchedule.filter(emi => !emi.isPaid).slice(0, 5);
  };

  const getPaidEmisCount = () => {
    return emiSchedule.filter(emi => emi.isPaid).length;
  };

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          <LoanIcon sx={{ mr: 2, fontSize: 40, verticalAlign: 'middle' }} />
          Loan Management Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage your loans, track EMI payments, and explore prepayment options
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

      {/* Loan Selection */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {loans.map((loan) => (
          <Grid item xs={12} md={6} key={loan.id}>
            <Card
              sx={{
                cursor: 'pointer',
                border: selectedLoan?.id === loan.id ? '2px solid #1976d2' : '1px solid #e0e0e0',
                transition: 'all 0.3s',
                '&:hover': { boxShadow: 3 },
              }}
              onClick={() => setSelectedLoan(loan)}
            >
              <CardContent>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    {loan.loanType}
                  </Typography>
                  <Chip label={loan.status} color="success" size="small" />
                </Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Account: {loan.accountNumber}
                </Typography>
                <Divider sx={{ my: 2 }} />
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">
                      Principal
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(loan.principalAmount)}
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">
                      Outstanding
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold', color: '#d32f2f' }}>
                      {formatCurrency(loan.outstandingAmount)}
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">
                      EMI Amount
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {formatCurrency(loan.emi)}
                    </Typography>
                  </Grid>
                  <Grid item xs={6}>
                    <Typography variant="caption" color="text.secondary">
                      Interest Rate
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {loan.interestRate}% p.a.
                    </Typography>
                  </Grid>
                </Grid>
                <Box sx={{ mt: 2 }}>
                  <Typography variant="caption" color="text.secondary">
                    Repayment Progress
                  </Typography>
                  <LinearProgress
                    variant="determinate"
                    value={selectedLoan?.id === loan.id ? calculateProgress() : 0}
                    sx={{ mt: 1, height: 8, borderRadius: 4 }}
                  />
                  <Typography variant="caption" color="text.secondary">
                    {selectedLoan?.id === loan.id ? `${calculateProgress().toFixed(1)}% repaid` : ''}
                  </Typography>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {selectedLoan && (
        <>
          {/* Quick Actions */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold', mb: 2 }}>
                Quick Actions
              </Typography>
              <Grid container spacing={2}>
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="primary"
                    startIcon={<PaymentIcon />}
                    onClick={() => setPaymentDialog(true)}
                  >
                    Pay EMI
                  </Button>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="secondary"
                    startIcon={<PrepaymentIcon />}
                    onClick={() => setPrepaymentDialog(true)}
                  >
                    Prepayment
                  </Button>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="warning"
                    startIcon={<RestructureIcon />}
                    onClick={() => setRestructureDialog(true)}
                  >
                    Restructure
                  </Button>
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                  <Button
                    fullWidth
                    variant="contained"
                    color="error"
                    startIcon={<CloseIcon />}
                    onClick={() => {
                      setForeclosureDialog(true);
                      handleCalculateForeclosure();
                    }}
                  >
                    Foreclose
                  </Button>
                </Grid>
              </Grid>
            </CardContent>
          </Card>

          {/* Upcoming EMIs */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  Upcoming EMI Schedule
                </Typography>
                <Chip
                  label={`${getPaidEmisCount()} / ${emiSchedule.length} EMIs Paid`}
                  color="primary"
                  variant="outlined"
                />
              </Box>
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                  <CircularProgress />
                </Box>
              ) : (
                <TableContainer>
                  <Table>
                    <TableHead>
                      <TableRow>
                        <TableCell>EMI #</TableCell>
                        <TableCell>Due Date</TableCell>
                        <TableCell align="right">EMI Amount</TableCell>
                        <TableCell align="right">Principal</TableCell>
                        <TableCell align="right">Interest</TableCell>
                        <TableCell align="right">Balance</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell>Action</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {getUpcomingEmis().map((emi) => (
                        <TableRow key={emi.id}>
                          <TableCell>{emi.emiNumber}</TableCell>
                          <TableCell>{formatDate(emi.dueDate)}</TableCell>
                          <TableCell align="right">{formatCurrency(emi.emiAmount)}</TableCell>
                          <TableCell align="right">{formatCurrency(emi.principalComponent)}</TableCell>
                          <TableCell align="right">{formatCurrency(emi.interestComponent)}</TableCell>
                          <TableCell align="right">{formatCurrency(emi.outstandingBalance)}</TableCell>
                          <TableCell>
                            <Chip
                              icon={getEmiStatusIcon(emi)}
                              label={emi.isPaid ? 'Paid' : emi.daysOverdue > 0 ? `Overdue ${emi.daysOverdue}d` : 'Upcoming'}
                              color={getEmiStatusColor(emi)}
                              size="small"
                            />
                          </TableCell>
                          <TableCell>
                            {!emi.isPaid && (
                              <Button
                                size="small"
                                variant="outlined"
                                onClick={() => {
                                  setSelectedEmi(emi);
                                  setPaymentAmount(emi.emiAmount.toString());
                                  setPaymentDialog(true);
                                }}
                              >
                                Pay Now
                              </Button>
                            )}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              )}
            </CardContent>
          </Card>
        </>
      )}

      {/* Payment Dialog */}
      <Dialog open={paymentDialog} onClose={() => setPaymentDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            Record EMI Payment
            <IconButton onClick={() => setPaymentDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            {selectedEmi && (
              <Alert severity="info" sx={{ mb: 2 }}>
                <Typography variant="body2">
                  <strong>EMI #{selectedEmi.emiNumber}</strong> - Due: {formatDate(selectedEmi.dueDate)}
                </Typography>
                <Typography variant="body2">
                  Amount: {formatCurrency(selectedEmi.emiAmount)}
                  {selectedEmi.penaltyAmount > 0 && ` + Penalty: ${formatCurrency(selectedEmi.penaltyAmount)}`}
                </Typography>
              </Alert>
            )}
            <TextField
              fullWidth
              label="Payment Amount"
              type="number"
              value={paymentAmount}
              onChange={(e) => setPaymentAmount(e.target.value)}
              sx={{ mb: 2 }}
              InputProps={{ startAdornment: '₹' }}
            />
            <TextField
              fullWidth
              label="Payment Reference / Transaction ID"
              value={paymentReference}
              onChange={(e) => setPaymentReference(e.target.value)}
              placeholder="e.g., TXN123456789"
            />
            <Alert severity="success" sx={{ mt: 2 }}>
              <Typography variant="body2">
                💰 Pay on time and earn <strong>500 reward points</strong>!
              </Typography>
            </Alert>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPaymentDialog(false)}>Cancel</Button>
          <Button onClick={handlePayEmi} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Confirm Payment'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Prepayment Dialog */}
      <Dialog open={prepaymentDialog} onClose={() => setPrepaymentDialog(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            Loan Prepayment
            <IconButton onClick={() => setPrepaymentDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Prepayment Type</InputLabel>
              <Select
                value={prepaymentType}
                label="Prepayment Type"
                onChange={(e) => setPrepaymentType(e.target.value as 'PARTIAL' | 'FULL')}
              >
                <MenuItem value="PARTIAL">Partial (Reduce Tenure)</MenuItem>
                <MenuItem value="FULL">Full (Close Loan)</MenuItem>
              </Select>
            </FormControl>
            <TextField
              fullWidth
              label="Prepayment Amount"
              type="number"
              value={prepaymentAmount}
              onChange={(e) => setPrepaymentAmount(e.target.value)}
              sx={{ mb: 2 }}
              InputProps={{ startAdornment: '₹' }}
            />
            <Button
              fullWidth
              variant="outlined"
              startIcon={<CalculateIcon />}
              onClick={handleCalculatePrepayment}
              sx={{ mb: 2 }}
              disabled={loading}
            >
              Calculate Savings
            </Button>
            {prepaymentPreview && (
              <Card sx={{ mb: 2, bgcolor: '#e3f2fd' }}>
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    Prepayment Preview
                  </Typography>
                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Interest Saved
                      </Typography>
                      <Typography variant="h6" color="success.main">
                        {formatCurrency(prepaymentPreview.interestSaved || 0)}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Tenure Reduced
                      </Typography>
                      <Typography variant="h6" color="primary">
                        {prepaymentPreview.tenureReduced || 0} months
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Prepayment Charges
                      </Typography>
                      <Typography variant="body1">
                        {formatCurrency(prepaymentPreview.charges || 0)}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="body2" color="text.secondary">
                        Cashback (2%)
                      </Typography>
                      <Typography variant="body1" color="success.main">
                        {formatCurrency((parseFloat(prepaymentAmount) * 0.02) || 0)}
                      </Typography>
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            )}
            <TextField
              fullWidth
              label="Payment Reference / Transaction ID"
              value={paymentReference}
              onChange={(e) => setPaymentReference(e.target.value)}
              placeholder="e.g., TXN123456789"
            />
            <Alert severity="info" sx={{ mt: 2 }}>
              <Typography variant="body2">
                🎁 Earn <strong>2% cashback</strong> on prepayment amount (multiplied by your tier)!
              </Typography>
            </Alert>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPrepaymentDialog(false)}>Cancel</Button>
          <Button onClick={handleProcessPrepayment} variant="contained" disabled={loading || !prepaymentPreview}>
            {loading ? <CircularProgress size={24} /> : 'Process Prepayment'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Restructure Dialog */}
      <Dialog open={restructureDialog} onClose={() => setRestructureDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            Request Loan Restructuring
            <IconButton onClick={() => setRestructureDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            <FormControl fullWidth sx={{ mb: 2 }}>
              <InputLabel>Restructure Reason</InputLabel>
              <Select
                value={restructureReason}
                label="Restructure Reason"
                onChange={(e) => setRestructureReason(e.target.value)}
              >
                <MenuItem value="FINANCIAL_HARDSHIP">Financial Hardship</MenuItem>
                <MenuItem value="REDUCE_EMI">Reduce EMI Amount</MenuItem>
                <MenuItem value="SHORTEN_TENURE">Shorten Tenure</MenuItem>
                <MenuItem value="RATE_CHANGE">Interest Rate Change</MenuItem>
                <MenuItem value="EMERGENCY">Emergency</MenuItem>
                <MenuItem value="OTHER">Other</MenuItem>
              </Select>
            </FormControl>
            <TextField
              fullWidth
              label="Requested New Tenure (months)"
              type="number"
              value={requestedTenure}
              onChange={(e) => setRequestedTenure(e.target.value)}
              helperText="Optional: Leave blank for bank to suggest"
            />
            <Alert severity="warning" sx={{ mt: 2 }}>
              <Typography variant="body2">
                Restructuring charges: ₹5,000 (FREE for Platinum/Diamond tier customers)
              </Typography>
            </Alert>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRestructureDialog(false)}>Cancel</Button>
          <Button onClick={handleRequestRestructure} variant="contained" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Submit Request'}
          </Button>
        </DialogActions>
      </Dialog>

      {/* Foreclosure Dialog */}
      <Dialog open={foreclosureDialog} onClose={() => setForeclosureDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            Loan Foreclosure
            <IconButton onClick={() => setForeclosureDialog(false)}>
              <CloseIcon />
            </IconButton>
          </Box>
        </DialogTitle>
        <DialogContent>
          <Box sx={{ pt: 2 }}>
            {loading ? (
              <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                <CircularProgress />
              </Box>
            ) : foreclosureDetails ? (
              <>
                <Card sx={{ mb: 2, bgcolor: '#fff3e0' }}>
                  <CardContent>
                    <Typography variant="h6" gutterBottom>
                      Foreclosure Summary
                    </Typography>
                    <Grid container spacing={2}>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">
                          Outstanding Principal
                        </Typography>
                        <Typography variant="body1">
                          {formatCurrency(foreclosureDetails.outstandingPrincipal || 0)}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">
                          Pending Interest
                        </Typography>
                        <Typography variant="body1">
                          {formatCurrency(foreclosureDetails.pendingInterest || 0)}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">
                          Foreclosure Charges
                        </Typography>
                        <Typography variant="body1">
                          {formatCurrency(foreclosureDetails.charges || 0)}
                        </Typography>
                      </Grid>
                      <Grid item xs={6}>
                        <Typography variant="body2" color="text.secondary">
                          Interest Saved
                        </Typography>
                        <Typography variant="body1" color="success.main">
                          {formatCurrency(foreclosureDetails.interestSaved || 0)}
                        </Typography>
                      </Grid>
                      <Grid item xs={12}>
                        <Divider sx={{ my: 1 }} />
                        <Typography variant="h6" color="error">
                          Total Amount Due: {formatCurrency(foreclosureDetails.totalAmount || 0)}
                        </Typography>
                      </Grid>
                    </Grid>
                  </CardContent>
                </Card>
                <TextField
                  fullWidth
                  label="Payment Reference / Transaction ID"
                  value={paymentReference}
                  onChange={(e) => setPaymentReference(e.target.value)}
                  placeholder="e.g., TXN123456789"
                />
                <Alert severity="success" sx={{ mt: 2 }}>
                  <Typography variant="body2">
                    🎉 Close your loan and earn <strong>1000 bonus points</strong>!
                  </Typography>
                </Alert>
              </>
            ) : null}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setForeclosureDialog(false)}>Cancel</Button>
          <Button onClick={handleProcessForeclosure} variant="contained" color="error" disabled={loading}>
            {loading ? <CircularProgress size={24} /> : 'Confirm Foreclosure'}
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default LoanDashboard;
