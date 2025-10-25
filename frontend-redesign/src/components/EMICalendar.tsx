import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  CircularProgress,
  Paper,
  Tooltip,
  Divider,
} from '@mui/material';
import {
  ChevronLeft as PrevIcon,
  ChevronRight as NextIcon,
  CheckCircle as PaidIcon,
  Schedule as UpcomingIcon,
  Warning as OverdueIcon,
  Today as TodayIcon,
  CalendarMonth as CalendarIcon,
  Payment as PaymentIcon,
  Info as InfoIcon,
  Close as CloseIcon,
} from '@mui/icons-material';
import {
  getEmiSchedule,
  recordEmiPayment,
  formatCurrency,
  formatDate,
  EmiScheduleDto,
} from '../services/module12Service';

interface EMICalendarProps {
  loanId: number;
  onPaymentSuccess?: () => void;
}

const EMICalendar: React.FC<EMICalendarProps> = ({ loanId, onPaymentSuccess }) => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [emiSchedule, setEmiSchedule] = useState<EmiScheduleDto[]>([]);
  const [selectedEmi, setSelectedEmi] = useState<EmiScheduleDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  
  // Payment dialog
  const [paymentDialog, setPaymentDialog] = useState(false);
  const [paymentAmount, setPaymentAmount] = useState('');
  const [paymentReference, setPaymentReference] = useState('');

  useEffect(() => {
    if (loanId) {
      fetchEmiSchedule();
    }
  }, [loanId]);

  const fetchEmiSchedule = async () => {
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
      await fetchEmiSchedule();
      if (onPaymentSuccess) {
        onPaymentSuccess();
      }
      setPaymentAmount('');
      setPaymentReference('');
      setSelectedEmi(null);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // Calendar functions
  const getDaysInMonth = (date: Date) => {
    return new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
  };

  const getFirstDayOfMonth = (date: Date) => {
    return new Date(date.getFullYear(), date.getMonth(), 1).getDay();
  };

  const goToPreviousMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() - 1, 1));
  };

  const goToNextMonth = () => {
    setCurrentDate(new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 1));
  };

  const goToToday = () => {
    setCurrentDate(new Date());
  };

  const getEmiForDate = (date: Date): EmiScheduleDto | undefined => {
    const dateString = date.toISOString().split('T')[0];
    return emiSchedule.find((emi) => emi.dueDate === dateString);
  };

  const isToday = (date: Date): boolean => {
    const today = new Date();
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  };

  const getEmiStatus = (emi: EmiScheduleDto) => {
    if (emi.isPaid) return 'paid';
    if (emi.daysOverdue > 0) return 'overdue';
    const dueDate = new Date(emi.dueDate);
    const today = new Date();
    if (dueDate.toDateString() === today.toDateString()) return 'due-today';
    if (dueDate < today) return 'overdue';
    return 'upcoming';
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'paid':
        return '#4caf50';
      case 'overdue':
        return '#f44336';
      case 'due-today':
        return '#ff9800';
      case 'upcoming':
        return '#2196f3';
      default:
        return '#9e9e9e';
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'paid':
        return <PaidIcon sx={{ fontSize: 20 }} />;
      case 'overdue':
        return <OverdueIcon sx={{ fontSize: 20 }} />;
      case 'due-today':
        return <TodayIcon sx={{ fontSize: 20 }} />;
      case 'upcoming':
        return <UpcomingIcon sx={{ fontSize: 20 }} />;
      default:
        return null;
    }
  };

  const renderCalendar = () => {
    const daysInMonth = getDaysInMonth(currentDate);
    const firstDay = getFirstDayOfMonth(currentDate);
    const days = [];

    // Empty cells for days before month starts
    for (let i = 0; i < firstDay; i++) {
      days.push(
        <Grid item xs={12 / 7} key={`empty-${i}`}>
          <Box sx={{ height: 100 }} />
        </Grid>
      );
    }

    // Days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      const date = new Date(currentDate.getFullYear(), currentDate.getMonth(), day);
      const emi = getEmiForDate(date);
      const today = isToday(date);
      const status = emi ? getEmiStatus(emi) : null;

      days.push(
        <Grid item xs={12 / 7} key={day}>
          <Paper
            sx={{
              height: 100,
              p: 1,
              cursor: emi ? 'pointer' : 'default',
              border: today ? '2px solid #1976d2' : '1px solid #e0e0e0',
              bgcolor: emi ? `${getStatusColor(status || '')}15` : 'white',
              transition: 'all 0.2s',
              '&:hover': emi
                ? {
                    boxShadow: 3,
                    transform: 'translateY(-2px)',
                  }
                : {},
            }}
            onClick={() => {
              if (emi) {
                setSelectedEmi(emi);
              }
            }}
          >
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 0.5 }}>
              <Typography
                variant="body2"
                sx={{
                  fontWeight: today ? 'bold' : 'normal',
                  color: today ? '#1976d2' : 'text.primary',
                }}
              >
                {day}
              </Typography>
              {emi && (
                <Box sx={{ color: getStatusColor(status || '') }}>
                  {getStatusIcon(status || '')}
                </Box>
              )}
            </Box>
            {emi && (
              <Box>
                <Typography variant="caption" display="block" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                  EMI #{emi.emiNumber}
                </Typography>
                <Typography variant="caption" display="block" sx={{ fontSize: '0.7rem' }}>
                  {formatCurrency(emi.emiAmount)}
                </Typography>
                {emi.daysOverdue > 0 && (
                  <Chip
                    label={`${emi.daysOverdue}d late`}
                    size="small"
                    sx={{
                      height: 16,
                      fontSize: '0.65rem',
                      mt: 0.5,
                      bgcolor: '#f44336',
                      color: 'white',
                    }}
                  />
                )}
              </Box>
            )}
          </Paper>
        </Grid>
      );
    }

    return days;
  };

  const monthYear = currentDate.toLocaleDateString('en-US', { month: 'long', year: 'numeric' });

  const upcomingEmis = emiSchedule
    .filter((emi) => !emi.isPaid && new Date(emi.dueDate) >= new Date())
    .slice(0, 3);

  const overdueEmis = emiSchedule.filter((emi) => !emi.isPaid && emi.daysOverdue > 0);

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 3 }}>
        <Typography variant="h5" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          <CalendarIcon sx={{ mr: 1, verticalAlign: 'middle' }} />
          EMI Payment Calendar
        </Typography>
        <Typography variant="body2" color="text.secondary">
          View your payment schedule and track EMI status
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

      {loading && !emiSchedule.length ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
          <CircularProgress />
        </Box>
      ) : (
        <Grid container spacing={3}>
          {/* Main Calendar */}
          <Grid item xs={12} lg={8}>
            <Card>
              <CardContent>
                {/* Calendar Controls */}
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                  <IconButton onClick={goToPreviousMonth}>
                    <PrevIcon />
                  </IconButton>
                  <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                      {monthYear}
                    </Typography>
                    <Button size="small" variant="outlined" startIcon={<TodayIcon />} onClick={goToToday}>
                      Today
                    </Button>
                  </Box>
                  <IconButton onClick={goToNextMonth}>
                    <NextIcon />
                  </IconButton>
                </Box>

                {/* Legend */}
                <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
                  <Chip icon={<PaidIcon />} label="Paid" size="small" sx={{ bgcolor: '#4caf5015' }} />
                  <Chip icon={<UpcomingIcon />} label="Upcoming" size="small" sx={{ bgcolor: '#2196f315' }} />
                  <Chip icon={<TodayIcon />} label="Due Today" size="small" sx={{ bgcolor: '#ff980015' }} />
                  <Chip icon={<OverdueIcon />} label="Overdue" size="small" sx={{ bgcolor: '#f4433615' }} />
                </Box>

                {/* Day Headers */}
                <Grid container sx={{ mb: 1 }}>
                  {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((day) => (
                    <Grid item xs={12 / 7} key={day}>
                      <Typography
                        variant="body2"
                        align="center"
                        sx={{ fontWeight: 'bold', color: 'text.secondary' }}
                      >
                        {day}
                      </Typography>
                    </Grid>
                  ))}
                </Grid>

                {/* Calendar Grid */}
                <Grid container spacing={1}>
                  {renderCalendar()}
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Sidebar */}
          <Grid item xs={12} lg={4}>
            {/* Overdue Alerts */}
            {overdueEmis.length > 0 && (
              <Card sx={{ mb: 3, border: '2px solid #f44336' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <OverdueIcon sx={{ color: '#f44336', mr: 1 }} />
                    <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#f44336' }}>
                      Overdue EMIs ({overdueEmis.length})
                    </Typography>
                  </Box>
                  {overdueEmis.map((emi) => (
                    <Paper key={emi.id} sx={{ p: 2, mb: 2, bgcolor: '#ffebee' }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                        EMI #{emi.emiNumber}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Due: {formatDate(emi.dueDate)}
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold', mb: 1 }}>
                        Amount: {formatCurrency(emi.emiAmount)}
                      </Typography>
                      <Chip
                        label={`${emi.daysOverdue} days overdue`}
                        size="small"
                        color="error"
                        sx={{ mr: 1, mb: 1 }}
                      />
                      {emi.penaltyAmount > 0 && (
                        <Chip
                          label={`Penalty: ${formatCurrency(emi.penaltyAmount)}`}
                          size="small"
                          color="warning"
                          sx={{ mb: 1 }}
                        />
                      )}
                      <Button
                        fullWidth
                        size="small"
                        variant="contained"
                        color="error"
                        startIcon={<PaymentIcon />}
                        onClick={() => {
                          setSelectedEmi(emi);
                          setPaymentAmount((emi.emiAmount + emi.penaltyAmount).toString());
                          setPaymentDialog(true);
                        }}
                      >
                        Pay Now
                      </Button>
                    </Paper>
                  ))}
                </CardContent>
              </Card>
            )}

            {/* Upcoming EMIs */}
            <Card sx={{ mb: 3 }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                  <UpcomingIcon sx={{ color: '#2196f3', mr: 1 }} />
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    Upcoming EMIs
                  </Typography>
                </Box>
                {upcomingEmis.length > 0 ? (
                  upcomingEmis.map((emi) => (
                    <Paper key={emi.id} sx={{ p: 2, mb: 2, bgcolor: '#e3f2fd' }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                        EMI #{emi.emiNumber}
                      </Typography>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Due: {formatDate(emi.dueDate)}
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold', mb: 1 }}>
                        {formatCurrency(emi.emiAmount)}
                      </Typography>
                      <Button
                        fullWidth
                        size="small"
                        variant="outlined"
                        startIcon={<PaymentIcon />}
                        onClick={() => {
                          setSelectedEmi(emi);
                          setPaymentAmount(emi.emiAmount.toString());
                          setPaymentDialog(true);
                        }}
                      >
                        Pay Early
                      </Button>
                    </Paper>
                  ))
                ) : (
                  <Typography variant="body2" color="text.secondary" align="center">
                    No upcoming EMIs
                  </Typography>
                )}
              </CardContent>
            </Card>

            {/* EMI Details (if selected) */}
            {selectedEmi && !paymentDialog && (
              <Card sx={{ border: '2px solid #1976d2' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                      <InfoIcon sx={{ color: '#1976d2', mr: 1 }} />
                      <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                        EMI Details
                      </Typography>
                    </Box>
                    <IconButton size="small" onClick={() => setSelectedEmi(null)}>
                      <CloseIcon fontSize="small" />
                    </IconButton>
                  </Box>

                  <Typography variant="subtitle1" sx={{ fontWeight: 'bold', mb: 2 }}>
                    EMI #{selectedEmi.emiNumber}
                  </Typography>

                  <Box sx={{ mb: 2 }}>
                    <Typography variant="body2" color="text.secondary">
                      Due Date
                    </Typography>
                    <Typography variant="body1" sx={{ fontWeight: 'bold' }}>
                      {formatDate(selectedEmi.dueDate)}
                    </Typography>
                  </Box>

                  <Divider sx={{ my: 2 }} />

                  <Grid container spacing={2}>
                    <Grid item xs={6}>
                      <Typography variant="caption" color="text.secondary">
                        EMI Amount
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                        {formatCurrency(selectedEmi.emiAmount)}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="caption" color="text.secondary">
                        Principal
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                        {formatCurrency(selectedEmi.principalComponent)}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="caption" color="text.secondary">
                        Interest
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                        {formatCurrency(selectedEmi.interestComponent)}
                      </Typography>
                    </Grid>
                    <Grid item xs={6}>
                      <Typography variant="caption" color="text.secondary">
                        Balance
                      </Typography>
                      <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                        {formatCurrency(selectedEmi.outstandingBalance)}
                      </Typography>
                    </Grid>
                  </Grid>

                  {selectedEmi.isPaid ? (
                    <Alert severity="success" sx={{ mt: 2 }}>
                      <Typography variant="body2">
                        Paid on {selectedEmi.paidDate ? formatDate(selectedEmi.paidDate) : 'N/A'}
                      </Typography>
                    </Alert>
                  ) : (
                    <>
                      {selectedEmi.daysOverdue > 0 && (
                        <Alert severity="error" sx={{ mt: 2 }}>
                          <Typography variant="body2">
                            {selectedEmi.daysOverdue} days overdue
                          </Typography>
                          <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                            Penalty: {formatCurrency(selectedEmi.penaltyAmount)}
                          </Typography>
                        </Alert>
                      )}
                      <Button
                        fullWidth
                        variant="contained"
                        startIcon={<PaymentIcon />}
                        sx={{ mt: 2 }}
                        onClick={() => {
                          setPaymentAmount(
                            (selectedEmi.emiAmount + selectedEmi.penaltyAmount).toString()
                          );
                          setPaymentDialog(true);
                        }}
                      >
                        Pay This EMI
                      </Button>
                    </>
                  )}
                </CardContent>
              </Card>
            )}
          </Grid>
        </Grid>
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
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPaymentAmount(e.target.value)}
              sx={{ mb: 2 }}
              InputProps={{ startAdornment: '₹' }}
            />
            <TextField
              fullWidth
              label="Payment Reference / Transaction ID"
              value={paymentReference}
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPaymentReference(e.target.value)}
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
    </Container>
  );
};

export default EMICalendar;
