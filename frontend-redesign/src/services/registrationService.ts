import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Registration Interfaces
export interface RegistrationRequest {
  // User credentials
  username: string;
  password: string;
  
  // Personal information
  fullName: string;
  email: string;
  phone: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  
  // Address information
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  
  // Identity verification
  panCard: string;
  aadharCard: string;
  
  // Account preferences
  accountType: 'SAVINGS' | 'CURRENT';
  initialDeposit: number;
  nomineName?: string;
  nomineeRelation?: string;
  
  // Terms acceptance
  termsAccepted: boolean;
  
  // Skip account creation (NEW)
  skipAccount?: boolean;
}

export interface RegistrationResponse {
  success: boolean;
  message: string;
  userId: number;
  customerId: number;
  username: string;
  hasAccount: boolean; // NEW: Indicates if account was created
  accountNumber?: string; // Optional - only if account created
  accountId?: number; // Optional - only if account created
}

export interface UserCredentials {
  username: string;
  password: string;
  role?: string;
}

export interface CustomerData {
  name: string;
  email: string;
  phone: string;
}

export interface AccountData {
  customerId: number;
  accountNumber?: string;
  balance: number;
  accountType?: string;
}

// API Functions

/**
 * Step 1: Register user credentials (AppUser)
 */
export const registerUser = async (credentials: UserCredentials): Promise<any> => {
  const response = await axios.post(`${API_BASE_URL}/auth/register`, {
    username: credentials.username,
    password: credentials.password,
    role: credentials.role || 'ROLE_CUSTOMER'
  });
  return response.data;
};

/**
 * Step 2: Create customer profile
 */
export const createCustomer = async (customerData: CustomerData): Promise<any> => {
  const response = await axios.post(`${API_BASE_URL}/registration/customers`, customerData);
  return response.data;
};

/**
 * Step 3: Create initial account
 */
export const createAccount = async (accountData: AccountData): Promise<any> => {
  const response = await axios.post(`${API_BASE_URL}/registration/accounts`, {
    customerId: accountData.customerId,
    accountNumber: accountData.accountNumber || generateAccountNumber(),
    balance: accountData.balance
  });
  return response.data;
};

/**
 * Step 4: Link customer to user
 */
export const linkCustomerToUser = async (userId: number, customerId: number): Promise<any> => {
  const response = await axios.put(`${API_BASE_URL}/registration/customers/${customerId}/link-user`, {
    user: { id: userId }
  });
  return response.data;
};

/**
 * Complete registration - creates user, customer, and optionally account
 * Uses the new /api/registration/complete endpoint that handles everything in one transaction
 */
export const completeRegistration = async (request: RegistrationRequest): Promise<RegistrationResponse> => {
  try {
    // Call the new unified registration endpoint
    const response = await axios.post(`${API_BASE_URL}/registration/complete`, {
      username: request.username,
      password: request.password,
      role: 'ROLE_CUSTOMER',
      fullName: request.fullName,
      email: request.email,
      phone: request.phone,
      accountNumber: generateAccountNumber(),
      initialDeposit: request.skipAccount ? null : request.initialDeposit,
      skipAccount: request.skipAccount
    });
    
    return response.data;
    
  } catch (error: any) {
    console.error('Registration error:', error);
    if (error.response?.data?.error) {
      throw new Error(error.response.data.error);
    }
    throw new Error(error.response?.data?.message || 'Registration failed. Please try again.');
  }
};

/**
 * Check if username is available
 */
export const checkUsernameAvailability = async (username: string): Promise<boolean> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/registration/check-username`, {
      params: { username }
    });
    return response.data.available;
  } catch (error) {
    // If endpoint doesn't exist, assume username checking not available
    return true;
  }
};

/**
 * Check if email is already registered
 */
export const checkEmailAvailability = async (email: string): Promise<boolean> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/registration/check-email`, {
      params: { email }
    });
    return response.data.available;
  } catch (error) {
    // If endpoint doesn't exist, assume email checking not available
    return true;
  }
};

/**
 * Validate PAN card format
 */
export const validatePAN = (pan: string): boolean => {
  const panRegex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}$/;
  return panRegex.test(pan);
};

/**
 * Validate Aadhar card format
 */
export const validateAadhar = (aadhar: string): boolean => {
  const aadharRegex = /^\d{12}$/;
  return aadharRegex.test(aadhar.replace(/\s/g, ''));
};

/**
 * Validate phone number (Indian format)
 */
export const validatePhone = (phone: string): boolean => {
  const phoneRegex = /^[6-9]\d{9}$/;
  return phoneRegex.test(phone.replace(/[\s-]/g, ''));
};

/**
 * Validate email format
 */
export const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Validate password strength
 */
export const validatePassword = (password: string): {
  isValid: boolean;
  errors: string[];
} => {
  const errors: string[] = [];
  
  if (password.length < 8) {
    errors.push('Password must be at least 8 characters long');
  }
  if (!/[A-Z]/.test(password)) {
    errors.push('Password must contain at least one uppercase letter');
  }
  if (!/[a-z]/.test(password)) {
    errors.push('Password must contain at least one lowercase letter');
  }
  if (!/[0-9]/.test(password)) {
    errors.push('Password must contain at least one number');
  }
  if (!/[!@#$%^&*]/.test(password)) {
    errors.push('Password must contain at least one special character (!@#$%^&*)');
  }
  
  return {
    isValid: errors.length === 0,
    errors
  };
};

/**
 * Generate account number
 */
export const generateAccountNumber = (): string => {
  const timestamp = Date.now().toString();
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  return `ACC${timestamp.substring(timestamp.length - 8)}${random}`;
};

/**
 * Calculate age from date of birth
 */
export const calculateAge = (dateOfBirth: string): number => {
  const today = new Date();
  const birthDate = new Date(dateOfBirth);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  
  return age;
};

/**
 * Format currency for display
 */
export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(amount);
};

/**
 * List of Indian states
 */
export const indianStates = [
  'Andhra Pradesh',
  'Arunachal Pradesh',
  'Assam',
  'Bihar',
  'Chhattisgarh',
  'Goa',
  'Gujarat',
  'Haryana',
  'Himachal Pradesh',
  'Jharkhand',
  'Karnataka',
  'Kerala',
  'Madhya Pradesh',
  'Maharashtra',
  'Manipur',
  'Meghalaya',
  'Mizoram',
  'Nagaland',
  'Odisha',
  'Punjab',
  'Rajasthan',
  'Sikkim',
  'Tamil Nadu',
  'Telangana',
  'Tripura',
  'Uttar Pradesh',
  'Uttarakhand',
  'West Bengal',
  'Andaman and Nicobar Islands',
  'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu',
  'Delhi',
  'Jammu and Kashmir',
  'Ladakh',
  'Lakshadweep',
  'Puducherry'
];
