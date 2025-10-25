import React from 'react'
import { Link } from 'react-router-dom'
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Button,
  Paper,
} from '@mui/material'
import {
  People as PeopleIcon,
  PersonAdd as PersonAddIcon,
  Approval as ApprovalIcon,
  Dashboard as DashboardIcon,
} from '@mui/icons-material'

const AdminDashboard: React.FC = () => {
  const adminCards = [
    {
      title: 'Manage Customers',
      description: 'View and manage all customer accounts',
      icon: <PeopleIcon sx={{ fontSize: 48, color: '#1976d2' }} />,
      path: '/admin/customers',
      color: '#1976d2',
    },
    {
      title: 'Create Customer',
      description: 'Add new customers and their accounts',
      icon: <PersonAddIcon sx={{ fontSize: 48, color: '#2e7d32' }} />,
      path: '/admin/create-customer',
      color: '#2e7d32',
    },
    {
      title: 'Loan Approvals',
      description: 'Review and approve pending loan applications',
      icon: <ApprovalIcon sx={{ fontSize: 48, color: '#ed6c02' }} />,
      path: '/admin/loan-approvals',
      color: '#ed6c02',
    },
  ]

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" gutterBottom sx={{ fontWeight: 'bold', color: '#1976d2' }}>
          Admin Dashboard
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Welcome to the MoneyMesh Banking System administration panel
        </Typography>
      </Box>

      <Grid container spacing={3}>
        {adminCards.map((card) => (
          <Grid item xs={12} md={4} key={card.path}>
            <Card
              sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                transition: 'transform 0.2s, box-shadow 0.2s',
                '&:hover': {
                  transform: 'translateY(-4px)',
                  boxShadow: 4,
                },
              }}
            >
              <CardContent sx={{ flexGrow: 1, textAlign: 'center', py: 4 }}>
                <Box sx={{ mb: 2 }}>{card.icon}</Box>
                <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
                  {card.title}
                </Typography>
                <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                  {card.description}
                </Typography>
                <Button
                  component={Link}
                  to={card.path}
                  variant="contained"
                  sx={{
                    bgcolor: card.color,
                    '&:hover': {
                      bgcolor: card.color,
                      opacity: 0.9,
                    },
                  }}
                >
                  Open
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Paper sx={{ mt: 4, p: 3, bgcolor: '#f5f5f5' }}>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 'bold' }}>
          Quick Stats
        </Typography>
        <Grid container spacing={2} sx={{ mt: 1 }}>
          <Grid item xs={12} md={4}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'white', borderRadius: 2 }}>
              <Typography variant="h4" color="primary" sx={{ fontWeight: 'bold' }}>
                3
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Total Customers
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'white', borderRadius: 2 }}>
              <Typography variant="h4" color="success.main" sx={{ fontWeight: 'bold' }}>
                3
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Active Accounts
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={12} md={4}>
            <Box sx={{ textAlign: 'center', p: 2, bgcolor: 'white', borderRadius: 2 }}>
              <Typography variant="h4" color="warning.main" sx={{ fontWeight: 'bold' }}>
                0
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Pending Approvals
              </Typography>
            </Box>
          </Grid>
        </Grid>
      </Paper>
    </Box>
  )
}

export default AdminDashboard
