import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Card,
  CardContent,
  Grid,
  Button,
  Chip,
  Avatar,
  LinearProgress,
  Divider,
  IconButton,
  Tooltip,
  Paper,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Alert,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Badge,
  CardActionArea,
} from '@mui/material';
import {
  EmojiEvents as TrophyIcon,
  Stars as PointsIcon,
  CardGiftcard as GiftIcon,
  Share as ShareIcon,
  LocalOffer as OfferIcon,
  TrendingUp as TrendingIcon,
  AccountBalance as BankIcon,
  ShoppingCart as ShopIcon,
  Flight as TravelIcon,
  Favorite as DonationIcon,
  CreditCard as CardIcon,
  Receipt as BillIcon,
  ContentCopy as CopyIcon,
  CheckCircle as CheckIcon,
  EmojiEvents as MilestoneIcon,
  PersonAdd as ReferralIcon,
  MonetizationOn as CashbackIcon,
  Loyalty as LoyaltyIcon,
} from '@mui/icons-material';
import {
  getRewardPoints,
  getTierInfo,
  generateReferralCode,
  getReferralHistory,
  getMilestoneProgress,
  getActiveOffers,
  getCashbackHistory,
  formatCurrency,
  pointsToCash,
  getTierGradient,
  getTierColor,
  formatDate,
  TierInfoDto,
  LoyaltyOfferDto,
  CashbackDto,
} from '../services/module12Service';

const RewardsDashboard: React.FC = () => {
  const [pointsData, setPointsData] = useState<any>(null);
  const [tierInfo, setTierInfo] = useState<TierInfoDto | null>(null);
  const [referralData, setReferralData] = useState<any>(null);
  const [milestones, setMilestones] = useState<any>(null);
  const [offers, setOffers] = useState<LoyaltyOfferDto[]>([]);
  const [cashbackHistory, setCashbackHistory] = useState<CashbackDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [copiedCode, setCopiedCode] = useState(false);

  const customerId = 1; // Replace with actual logged-in customer ID

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    try {
      setLoading(true);
      setError(null);

      const [points, tier, referrals, milestonesData, offersData, cashback] = await Promise.all([
        getRewardPoints(customerId),
        getTierInfo(customerId),
        getReferralHistory(customerId),
        getMilestoneProgress(customerId),
        getActiveOffers(customerId),
        getCashbackHistory(customerId),
      ]);

      setPointsData(points);
      setTierInfo(tier);
      setReferralData(referrals);
      setMilestones(milestonesData);
      setOffers(offersData);
      setCashbackHistory(cashback);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateReferral = async () => {
    try {
      setLoading(true);
      const referral = await generateReferralCode(customerId);
      setSuccess(`Referral code generated: ${referral.referralCode}`);
      await fetchAllData();
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCopyReferralCode = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopiedCode(true);
    setTimeout(() => setCopiedCode(false), 2000);
    setSuccess('Referral code copied to clipboard!');
  };

  const getTierIcon = (tier: string) => {
    switch (tier) {
      case 'SILVER':
        return '🥈';
      case 'GOLD':
        return '🥇';
      case 'PLATINUM':
        return '💎';
      case 'DIAMOND':
        return '💠';
      default:
        return '⭐';
    }
  };

  const getProgressToNextTier = () => {
    if (!tierInfo) return 0;
    if (tierInfo.currentTier === 'DIAMOND') return 100;
    
    const currentPoints = tierInfo.activePoints;
    const nextThreshold = tierInfo.nextTierThreshold;
    const previousThreshold = {
      'GOLD': 0,
      'PLATINUM': 10000,
      'DIAMOND': 50000,
    }[tierInfo.currentTier === 'SILVER' ? 'GOLD' : tierInfo.currentTier === 'GOLD' ? 'PLATINUM' : 'DIAMOND'] || 0;

    return ((currentPoints - previousThreshold) / (nextThreshold - previousThreshold)) * 100;
  };

  const getNextTierName = () => {
    if (!tierInfo) return '';
    const tierMap: Record<string, string> = {
      'SILVER': 'GOLD',
      'GOLD': 'PLATINUM',
      'PLATINUM': 'DIAMOND',
      'DIAMOND': 'MAX',
    };
    return tierMap[tierInfo.currentTier] || '';
  };

  if (loading && !pointsData) {
    return (
      <Container maxWidth="xl" sx={{ mt: 4, display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' }}>
        <CircularProgress size={60} />
      </Container>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          <TrophyIcon sx={{ mr: 2, fontSize: 40, verticalAlign: 'middle' }} />
          Rewards & Loyalty Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Track your points, tier benefits, cashback, and exclusive offers
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

      {/* Top Section: Points & Tier */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {/* Points Card */}
        <Grid item xs={12} md={4}>
          <Card
            sx={{
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              color: 'white',
              height: '100%',
            }}
          >
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Avatar sx={{ bgcolor: 'rgba(255,255,255,0.3)', mr: 2 }}>
                  <PointsIcon />
                </Avatar>
                <Typography variant="h6">Reward Points</Typography>
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 1 }}>
                {pointsData?.totalPoints?.toLocaleString() || 0}
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.9, mb: 2 }}>
                Active: {pointsData?.activePoints?.toLocaleString() || 0} points
              </Typography>
              <Divider sx={{ bgcolor: 'rgba(255,255,255,0.3)', mb: 2 }} />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Box>
                  <Typography variant="caption" sx={{ opacity: 0.8 }}>
                    Cash Value
                  </Typography>
                  <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                    {formatCurrency(pointsData?.pointValue || 0)}
                  </Typography>
                </Box>
                <Button
                  variant="contained"
                  sx={{ bgcolor: 'white', color: '#667eea', '&:hover': { bgcolor: '#f0f0f0' } }}
                  size="small"
                  href="#redeem"
                >
                  Redeem
                </Button>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Tier Card */}
        <Grid item xs={12} md={8}>
          <Card sx={{ height: '100%' }}>
            <CardContent>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 3 }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                  <Avatar
                    sx={{
                      width: 80,
                      height: 80,
                      background: getTierGradient(tierInfo?.currentTier || 'SILVER'),
                      fontSize: 40,
                      mr: 3,
                      boxShadow: 3,
                    }}
                  >
                    {getTierIcon(tierInfo?.currentTier || 'SILVER')}
                  </Avatar>
                  <Box>
                    <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 0.5 }}>
                      {tierInfo?.currentTier || 'SILVER'} TIER
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Member since: {tierInfo ? formatDate(tierInfo.tierSince) : 'N/A'}
                    </Typography>
                  </Box>
                </Box>
                <Chip
                  label={`${tierInfo?.cashbackMultiplier || 1.0}x Cashback`}
                  color="primary"
                  sx={{ fontWeight: 'bold' }}
                />
              </Box>

              {/* Progress to Next Tier */}
              {tierInfo?.currentTier !== 'DIAMOND' && (
                <Box sx={{ mb: 3 }}>
                  <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                    <Typography variant="body2" color="text.secondary">
                      Progress to {getNextTierName()} Tier
                    </Typography>
                    <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                      {tierInfo?.pointsToNextTier?.toLocaleString() || 0} points to go
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={getProgressToNextTier()}
                    sx={{
                      height: 10,
                      borderRadius: 5,
                      bgcolor: '#e0e0e0',
                      '& .MuiLinearProgress-bar': {
                        background: getTierGradient(getNextTierName()),
                      },
                    }}
                  />
                </Box>
              )}

              {/* Tier Benefits */}
              <Box>
                <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 'bold' }}>
                  Your Benefits:
                </Typography>
                <Grid container spacing={1}>
                  {tierInfo?.benefits?.map((benefit, index) => (
                    <Grid item xs={12} sm={6} key={index}>
                      <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <CheckIcon sx={{ fontSize: 16, mr: 1, color: 'success.main' }} />
                        <Typography variant="body2">{benefit}</Typography>
                      </Box>
                    </Grid>
                  ))}
                </Grid>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Middle Section: Quick Stats */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <CashbackIcon sx={{ color: '#4caf50', mr: 1 }} />
                <Typography variant="subtitle2" color="text.secondary">
                  Total Cashback
                </Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                {formatCurrency(
                  cashbackHistory.reduce((sum, cb) => sum + cb.finalCashback, 0)
                )}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {cashbackHistory.length} transactions
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <ReferralIcon sx={{ color: '#ff9800', mr: 1 }} />
                <Typography variant="subtitle2" color="text.secondary">
                  Referrals
                </Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                {referralData?.totalReferrals || 0}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                {referralData?.successfulReferrals || 0} successful
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <MilestoneIcon sx={{ color: '#9c27b0', mr: 1 }} />
                <Typography variant="subtitle2" color="text.secondary">
                  Milestones
                </Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                {milestones?.totalAchieved || 0}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                +{milestones?.totalBonus?.toLocaleString() || 0} bonus points
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <OfferIcon sx={{ color: '#f44336', mr: 1 }} />
                <Typography variant="subtitle2" color="text.secondary">
                  Active Offers
                </Typography>
              </Box>
              <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                {offers.length}
              </Typography>
              <Typography variant="caption" color="text.secondary">
                Exclusive deals
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Referral Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <ShareIcon sx={{ mr: 1, color: '#1976d2' }} />
              <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                Refer & Earn
              </Typography>
            </Box>
            <Button
              variant="contained"
              startIcon={<ReferralIcon />}
              onClick={handleGenerateReferral}
              disabled={loading}
            >
              Generate New Code
            </Button>
          </Box>

          <Alert severity="info" sx={{ mb: 2 }}>
            <Typography variant="body2">
              🎁 Invite friends and earn <strong>500 points</strong> when they make their first transaction!
            </Typography>
          </Alert>

          {referralData?.referrals && referralData.referrals.length > 0 ? (
            <Grid container spacing={2}>
              {referralData.referrals.slice(0, 3).map((referral: any, index: number) => (
                <Grid item xs={12} sm={6} md={4} key={index}>
                  <Paper sx={{ p: 2, bgcolor: '#f5f5f5' }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                      <Typography variant="subtitle2" sx={{ fontWeight: 'bold' }}>
                        {referral.referralCode}
                      </Typography>
                      <IconButton
                        size="small"
                        onClick={() => handleCopyReferralCode(referral.referralCode)}
                      >
                        {copiedCode ? <CheckIcon color="success" /> : <CopyIcon />}
                      </IconButton>
                    </Box>
                    <Chip
                      label={referral.referralStatus}
                      size="small"
                      color={
                        referral.referralStatus === 'REWARDED'
                          ? 'success'
                          : referral.referralStatus === 'QUALIFIED'
                          ? 'primary'
                          : 'default'
                      }
                    />
                    <Typography variant="caption" display="block" sx={{ mt: 1 }}>
                      Generated: {formatDate(referral.generatedDate)}
                    </Typography>
                  </Paper>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
              No referrals yet. Generate a code and start earning!
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* Milestones Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <MilestoneIcon sx={{ mr: 1, color: '#ff9800' }} />
            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
              Achievement Milestones
            </Typography>
          </Box>

          {milestones?.milestones && milestones.milestones.length > 0 ? (
            <Grid container spacing={2}>
              {milestones.milestones.map((milestone: any, index: number) => (
                <Grid item xs={12} sm={6} md={4} key={index}>
                  <Card variant="outlined" sx={{ bgcolor: '#fff8e1' }}>
                    <CardContent>
                      <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                        <TrophyIcon sx={{ color: '#ffa726', mr: 1 }} />
                        <Typography variant="subtitle2" sx={{ fontWeight: 'bold' }}>
                          {milestone.milestoneType.replace(/_/g, ' ')}
                        </Typography>
                      </Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        {milestone.description}
                      </Typography>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 1 }}>
                        <Chip
                          label={`+${milestone.bonusPoints} points`}
                          size="small"
                          color="warning"
                        />
                        <Typography variant="caption" color="text.secondary">
                          {formatDate(milestone.achievedDate)}
                        </Typography>
                      </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
              Keep transacting to unlock milestones and earn bonus points!
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* Active Offers Section */}
      <Card sx={{ mb: 3 }}>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <OfferIcon sx={{ mr: 1, color: '#e91e63' }} />
            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
              Exclusive Offers
            </Typography>
          </Box>

          {offers.length > 0 ? (
            <Grid container spacing={2}>
              {offers.map((offer, index) => (
                <Grid item xs={12} md={6} key={index}>
                  <Card
                    variant="outlined"
                    sx={{
                      border: '2px solid #e91e63',
                      transition: 'all 0.3s',
                      '&:hover': { boxShadow: 4 },
                    }}
                  >
                    <CardActionArea>
                      <CardContent>
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                          <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#e91e63' }}>
                            {offer.offerTitle}
                          </Typography>
                          {offer.tierLevel && (
                            <Chip
                              label={offer.tierLevel}
                              size="small"
                              sx={{
                                bgcolor: getTierColor(offer.tierLevel),
                                color: 'white',
                                fontWeight: 'bold',
                              }}
                            />
                          )}
                        </Box>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                          {offer.description}
                        </Typography>
                        <Divider sx={{ my: 1 }} />
                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <Chip label={offer.offerType} size="small" color="primary" variant="outlined" />
                          <Typography variant="caption" color="text.secondary">
                            Valid until: {formatDate(offer.validUntil)}
                          </Typography>
                        </Box>
                      </CardContent>
                    </CardActionArea>
                  </Card>
                </Grid>
              ))}
            </Grid>
          ) : (
            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
              No active offers at the moment. Check back soon!
            </Typography>
          )}
        </CardContent>
      </Card>

      {/* Cashback History */}
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
            <CashbackIcon sx={{ mr: 1, color: '#4caf50' }} />
            <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
              Recent Cashback
            </Typography>
          </Box>

          {cashbackHistory.length > 0 ? (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow>
                    <TableCell>Date</TableCell>
                    <TableCell align="right">Transaction Amount</TableCell>
                    <TableCell align="center">Base Rate</TableCell>
                    <TableCell align="center">Tier Multiplier</TableCell>
                    <TableCell align="right">Cashback Earned</TableCell>
                    <TableCell>Status</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {cashbackHistory.slice(0, 5).map((cashback, index) => (
                    <TableRow key={index}>
                      <TableCell>{formatDate(cashback.transactionDate)}</TableCell>
                      <TableCell align="right">
                        {formatCurrency(cashback.transactionAmount)}
                      </TableCell>
                      <TableCell align="center">{cashback.cashbackPercentage}%</TableCell>
                      <TableCell align="center">{cashback.tierMultiplier}x</TableCell>
                      <TableCell align="right" sx={{ fontWeight: 'bold', color: '#4caf50' }}>
                        {formatCurrency(cashback.finalCashback)}
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={cashback.status}
                          size="small"
                          color={cashback.status === 'CREDITED' ? 'success' : 'warning'}
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Typography variant="body2" color="text.secondary" align="center" sx={{ py: 2 }}>
              No cashback history yet. Start transacting to earn cashback!
            </Typography>
          )}
        </CardContent>
      </Card>
    </Container>
  );
};

export default RewardsDashboard;
