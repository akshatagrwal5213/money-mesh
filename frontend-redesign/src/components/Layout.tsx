import React, { useState, useEffect } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { useTheme as useCustomTheme } from '../contexts/ThemeContext'
import { useAutoLogout } from '../hooks/useAutoLogout'
import { getPreferences } from '../services/module7Service'
import AutoLogoutCountdown from './AutoLogoutCountdown'
import {
  AppBar,
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography,
  Avatar,
  Divider,
  Collapse,
  Badge,
  Menu,
  MenuItem,
  Tooltip,
} from '@mui/material'
import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  AccountBalance as AccountIcon,
  SwapHoriz as TransferIcon,
  CreditCard as CardIcon,
  TrendingUp as InvestmentIcon,
  Payment as PaymentIcon,
  Assessment as AnalyticsIcon,
  Shield as InsuranceIcon,
  AccountBalance as TaxIcon,
  Diamond as WealthIcon,
  Stars as RewardsIcon,
  Score as CreditIcon,
  Settings as SettingsIcon,
  Notifications as NotificationsIcon,
  Logout as LogoutIcon,
  ExpandLess,
  ExpandMore,
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Approval as ApprovalIcon,
  Receipt as TransactionIcon,
  ShowChart as ChartIcon,
  Description as ReceiptIcon,
  LightMode as LightModeIcon,
  DarkMode as DarkModeIcon,
} from '@mui/icons-material'

const DRAWER_WIDTH = 280

interface NavItem {
  title: string
  path: string
  icon: React.ReactNode
  children?: NavItem[]
}

const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, logout, isAdmin, isAuthenticated, hasAccounts } = useAuth()
  const { theme, toggleTheme } = useCustomTheme()
  const location = useLocation()
  const navigate = useNavigate()
  const [mobileOpen, setMobileOpen] = useState(false)
  const [autoLogoutMinutes, setAutoLogoutMinutes] = useState<number>(30)
  const [expandedSections, setExpandedSections] = useState<{ [key: string]: boolean }>({
    banking: true,
    investments: false,
    payments: false,
    analytics: false,
    insurance: false,
    wealth: false,
    loans: false,
  })
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

  // Load user preferences for auto-logout
  useEffect(() => {
    const loadPreferences = async () => {
      console.log('[Layout] Loading preferences - isAuthenticated:', isAuthenticated, 'isAdmin:', isAdmin);
      if (isAuthenticated && !isAdmin) {
        try {
          const prefs = await getPreferences()
          console.log('[Layout] Preferences loaded, autoLogoutMinutes:', prefs.autoLogoutMinutes);
          setAutoLogoutMinutes(prefs.autoLogoutMinutes || 30)
        } catch (error) {
          console.error('Failed to load preferences:', error)
          setAutoLogoutMinutes(30) // Default to 30 minutes
        }
      }
    }
    loadPreferences()

    // Listen for preference updates (custom event)
    const handlePreferencesUpdated = () => {
      console.log('[Layout] Preferences updated event received, reloading...');
      loadPreferences();
    };

    window.addEventListener('preferencesUpdated', handlePreferencesUpdated);

    return () => {
      window.removeEventListener('preferencesUpdated', handlePreferencesUpdated);
    };
  }, [isAuthenticated, isAdmin])

  // Enable auto-logout for authenticated users (only customers, not admins)
  console.log('[Layout] useAutoLogout called with:', autoLogoutMinutes, 'enabled:', isAuthenticated && !isAdmin);
  const { getRemainingTime } = useAutoLogout(autoLogoutMinutes, isAuthenticated && !isAdmin)

  // Hide navigation for login and register pages
  const isPublicPage = location.pathname === '/login' || location.pathname === '/register'
  
  // Render simple layout for public pages
  if (isPublicPage || !isAuthenticated) {
    return (
      <Box 
        sx={{ 
          minHeight: '100vh', 
          backgroundColor: theme === 'DARK' ? '#0d1117' : '#f8f9fa' 
        }}
      >
        {children}
      </Box>
    )
  }

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen)
  }

  const handleSectionToggle = (section: string) => {
    setExpandedSections((prev) => ({
      ...prev,
      [section]: !prev[section],
    }))
  }

  const handleProfileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleProfileMenuClose = () => {
    setAnchorEl(null)
  }

  const handleLogout = () => {
    handleProfileMenuClose()
    logout()
  }

  // Define which sections require accounts
  const sectionsRequiringAccounts = ['investments', 'payments', 'analytics', 'wealth', 'loans', 'rewards']

  const customerNavSections = [
    {
      title: 'Banking',
      key: 'banking',
      requiresAccount: false, // Basic banking should always be visible
      items: [
        { title: 'Dashboard', path: '/dashboard', icon: <DashboardIcon />, requiresAccount: false },
        { title: 'Accounts', path: '/accounts', icon: <AccountIcon />, requiresAccount: false },
        { title: 'Transactions', path: '/transactions', icon: <TransactionIcon />, requiresAccount: true },
        { title: 'Transfers', path: '/transfers', icon: <TransferIcon />, requiresAccount: true },
        { title: 'Cards', path: '/cards', icon: <CardIcon />, requiresAccount: true },
      ],
    },
    {
      title: 'Investments',
      key: 'investments',
      requiresAccount: true,
      items: [
        { title: 'Portfolio', path: '/portfolio', icon: <InvestmentIcon /> },
        { title: 'Fixed Deposits', path: '/fixed-deposits', icon: <AccountIcon /> },
        { title: 'Recurring Deposits', path: '/recurring-deposits', icon: <AccountIcon /> },
        { title: 'Mutual Funds', path: '/mutual-funds', icon: <ChartIcon /> },
        { title: 'SIP', path: '/sip', icon: <ChartIcon /> },
      ],
    },
    {
      title: 'Payments',
      key: 'payments',
      requiresAccount: true,
      items: [
        { title: 'UPI Payments', path: '/upi-payments', icon: <PaymentIcon /> },
        { title: 'QR Codes', path: '/qr-codes', icon: <PaymentIcon /> },
        { title: 'Bill Payments', path: '/bill-payments', icon: <ReceiptIcon /> },
      ],
    },
    {
      title: 'Analytics & Planning',
      key: 'analytics',
      requiresAccount: true,
      items: [
        { title: 'Analytics', path: '/analytics', icon: <AnalyticsIcon /> },
        { title: 'Budgets', path: '/budgets', icon: <AnalyticsIcon /> },
        { title: 'Goals', path: '/goals', icon: <AnalyticsIcon /> },
      ],
    },
    {
      title: 'Insurance',
      key: 'insurance',
      requiresAccount: true, // Insurance requires account
      items: [
        { title: 'Insurance', path: '/insurance', icon: <InsuranceIcon /> },
        { title: 'Claims', path: '/claims', icon: <ReceiptIcon /> },
      ],
    },
    {
      title: 'Tax & Wealth',
      key: 'wealth',
      requiresAccount: true,
      items: [
        { title: 'Tax Dashboard', path: '/tax-dashboard', icon: <TaxIcon /> },
        { title: 'Tax Calculator', path: '/tax-calculator', icon: <AnalyticsIcon /> },
        { title: 'Wealth Dashboard', path: '/wealth-dashboard', icon: <WealthIcon /> },
        { title: 'Retirement Planner', path: '/retirement-planner', icon: <WealthIcon /> },
      ],
    },
    {
      title: 'Loans & Credit',
      key: 'loans',
      requiresAccount: true,
      items: [
        { title: 'My Loans', path: '/loan-dashboard', icon: <AccountIcon /> },
        { title: 'Loan Eligibility', path: '/loan-eligibility', icon: <AccountIcon /> },
        { title: 'Credit Score', path: '/credit-dashboard', icon: <CreditIcon /> },
        { title: 'Apply for Loan', path: '/loans', icon: <AccountIcon /> },
      ],
    },
    {
      title: 'Rewards',
      key: 'rewards',
      requiresAccount: true,
      items: [
        { title: 'Rewards Dashboard', path: '/rewards-dashboard', icon: <RewardsIcon /> },
        { title: 'Redemption Store', path: '/redemption-store', icon: <RewardsIcon /> },
      ],
    },
  ]

  // Filter sections and items based on whether user has accounts
  const visibleNavSections = customerNavSections
    .filter(section => !section.requiresAccount || hasAccounts)
    .map(section => ({
      ...section,
      items: section.items.filter((item: any) => 
        !item.requiresAccount || hasAccounts
      )
    }))
    .filter(section => section.items.length > 0) // Remove sections with no visible items

  const adminNavItems = [
    { title: 'Manage Customers', path: '/admin/customers', icon: <PeopleIcon /> },
    { title: 'Create Customer & Account', path: '/admin/create-customer', icon: <PersonAddIcon /> },
    { title: 'Loan Approvals', path: '/admin/loan-approvals', icon: <ApprovalIcon /> },
  ]

  const isActive = (path: string) => location.pathname === path

  const drawer = (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ p: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
        <AccountIcon sx={{ fontSize: 32, color: '#1976d2' }} />
        <Typography variant="h6" sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          MoneyMesh
        </Typography>
      </Box>
      <Divider />
      
      <Box sx={{ flexGrow: 1, overflowY: 'auto', overflowX: 'hidden' }}>
        <List>
          {isAdmin ? (
            // Admin Navigation
            adminNavItems.map((item) => (
              <ListItem key={item.path} disablePadding>
                <ListItemButton
                  component={Link}
                  to={item.path}
                  selected={isActive(item.path)}
                  sx={{
                    '&.Mui-selected': {
                      backgroundColor: '#e3f2fd',
                      borderRight: '4px solid #1976d2',
                      '& .MuiListItemIcon-root': { color: '#1976d2' },
                      '& .MuiListItemText-primary': { fontWeight: 'bold', color: '#1976d2' },
                    },
                  }}
                >
                  <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
                  <ListItemText primary={item.title} />
                </ListItemButton>
              </ListItem>
            ))
          ) : (
            // Customer Navigation with Sections
            visibleNavSections.map((section) => (
              <Box key={section.key}>
                <ListItemButton onClick={() => handleSectionToggle(section.key)}>
                  <ListItemText
                    primary={section.title}
                    primaryTypographyProps={{
                      fontSize: '0.875rem',
                      fontWeight: 'bold',
                      color: '#666',
                    }}
                  />
                  {expandedSections[section.key] ? <ExpandLess /> : <ExpandMore />}
                </ListItemButton>
                <Collapse in={expandedSections[section.key]} timeout="auto" unmountOnExit>
                  <List component="div" disablePadding>
                    {section.items.map((item) => (
                      <ListItem key={item.path} disablePadding>
                        <ListItemButton
                          component={Link}
                          to={item.path}
                          selected={isActive(item.path)}
                          sx={{
                            pl: 4,
                            '&.Mui-selected': {
                              backgroundColor: '#e3f2fd',
                              borderRight: '4px solid #1976d2',
                              '& .MuiListItemIcon-root': { color: '#1976d2' },
                              '& .MuiListItemText-primary': { fontWeight: 'bold', color: '#1976d2' },
                            },
                            '&:hover': {
                              backgroundColor: '#f5f5f5',
                            },
                          }}
                        >
                          <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
                          <ListItemText 
                            primary={item.title}
                            primaryTypographyProps={{ fontSize: '0.875rem' }}
                          />
                        </ListItemButton>
                      </ListItem>
                    ))}
                  </List>
                </Collapse>
              </Box>
            ))
          )}
        </List>
      </Box>

      <Divider />
      <List>
        <ListItem disablePadding>
          <ListItemButton component={Link} to="/settings" selected={isActive('/settings')}>
            <ListItemIcon sx={{ minWidth: 40 }}><SettingsIcon /></ListItemIcon>
            <ListItemText primary="Settings" primaryTypographyProps={{ fontSize: '0.875rem' }} />
          </ListItemButton>
        </ListItem>
        <ListItem disablePadding>
          <ListItemButton component={Link} to="/notifications" selected={isActive('/notifications')}>
            <ListItemIcon sx={{ minWidth: 40 }}>
              <Badge badgeContent={3} color="error">
                <NotificationsIcon />
              </Badge>
            </ListItemIcon>
            <ListItemText primary="Notifications" primaryTypographyProps={{ fontSize: '0.875rem' }} />
          </ListItemButton>
        </ListItem>
      </List>
    </Box>
  )

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar
        position="fixed"
        sx={{
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          ml: { sm: `${DRAWER_WIDTH}px` },
          backgroundColor: '#1976d2',
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            edge="start"
            onClick={handleDrawerToggle}
            sx={{ mr: 2, display: { sm: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1 }}>
            {location.pathname === '/dashboard' ? 'Dashboard' :
             location.pathname.split('/').pop()?.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase())}
          </Typography>
          
          {/* Auto-logout countdown - only for customers */}
          {!isAdmin && (
            <Box sx={{ mr: 2 }}>
              <AutoLogoutCountdown 
                getRemainingTime={getRemainingTime} 
                enabled={isAuthenticated && !isAdmin}
              />
            </Box>
          )}
          
          <Tooltip title={theme === 'DARK' ? 'Switch to Light Mode' : 'Switch to Dark Mode'}>
            <IconButton color="inherit" onClick={toggleTheme} sx={{ mr: 1 }}>
              {theme === 'DARK' ? <LightModeIcon /> : <DarkModeIcon />}
            </IconButton>
          </Tooltip>
          <IconButton color="inherit" onClick={handleProfileMenuOpen}>
            <Avatar sx={{ width: 32, height: 32, bgcolor: '#fff', color: '#1976d2' }}>
              {user?.username?.charAt(0).toUpperCase() || 'U'}
            </Avatar>
          </IconButton>
          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleProfileMenuClose}
            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          >
            <MenuItem disabled>
              <Typography variant="body2">{user?.username}</Typography>
            </MenuItem>
            <Divider />
            <MenuItem onClick={() => { handleProfileMenuClose(); navigate('/settings'); }}>
              <ListItemIcon><SettingsIcon fontSize="small" /></ListItemIcon>
              Settings
            </MenuItem>
            <MenuItem onClick={handleLogout}>
              <ListItemIcon><LogoutIcon fontSize="small" /></ListItemIcon>
              Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>
      
      <Box
        component="nav"
        sx={{ width: { sm: DRAWER_WIDTH }, flexShrink: { sm: 0 } }}
      >
        <Drawer
          variant="temporary"
          open={mobileOpen}
          onClose={handleDrawerToggle}
          ModalProps={{ keepMounted: true }}
          sx={{
            display: { xs: 'block', sm: 'none' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
        >
          {drawer}
        </Drawer>
        <Drawer
          variant="permanent"
          sx={{
            display: { xs: 'none', sm: 'block' },
            '& .MuiDrawer-paper': { boxSizing: 'border-box', width: DRAWER_WIDTH },
          }}
          open
        >
          {drawer}
        </Drawer>
      </Box>
      
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          width: { sm: `calc(100% - ${DRAWER_WIDTH}px)` },
          mt: 8,
          backgroundColor: theme === 'DARK' ? '#0d1117' : '#f8f9fa',
          minHeight: '100vh',
        }}
      >
        {children}
      </Box>
    </Box>
  )
}

export default Layout
