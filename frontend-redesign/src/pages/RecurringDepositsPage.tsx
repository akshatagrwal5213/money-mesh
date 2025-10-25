import React, { useState, useEffect } from 'react';
import { accountService } from '../services/accountService';
import { 
  createRecurringDeposit, 
  getMyRecurringDeposits, 
  payRDInstallment,
  RecurringDeposit,
  RecurringDepositRequest,
  MaturityAction
} from '../services/investmentService';

const RecurringDepositsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'view' | 'create'>('view');
  const [recurringDeposits, setRecurringDeposits] = useState<RecurringDeposit[]>([]);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Create RD form state
  const [formData, setFormData] = useState<RecurringDepositRequest>({
    accountNumber: '',
    monthlyInstallment: 1000,
    tenureMonths: 12,
    maturityAction: MaturityAction.CREDIT_TO_ACCOUNT,
    autoDebit: true
  });

  useEffect(() => {
    loadRecurringDeposits();
    loadAccounts();
  }, []);

  const loadRecurringDeposits = async () => {
    try {
      setLoading(true);
      const data = await getMyRecurringDeposits();
      setRecurringDeposits(data);
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to load recurring deposits' });
    } finally {
      setLoading(false);
    }
  };

  const loadAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data);
      if (data.length > 0) {
        setFormData(prev => ({ ...prev, accountNumber: data[0].accountNumber }));
      }
    } catch (error) {
      console.error('Failed to load accounts', error);
    }
  };

  const handleCreateRD = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      await createRecurringDeposit(formData);
      setMessage({ type: 'success', text: 'Recurring Deposit created successfully!' });
      setFormData({
        accountNumber: accounts[0]?.accountNumber || '',
        monthlyInstallment: 1000,
        tenureMonths: 12,
        maturityAction: MaturityAction.CREDIT_TO_ACCOUNT,
        autoDebit: true
      });
      setActiveTab('view');
      loadRecurringDeposits();
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to create RD' });
    } finally {
      setLoading(false);
    }
  };

  const handlePayInstallment = async (rdNumber: string) => {
    if (!window.confirm('Are you sure you want to pay this installment?')) return;

    try {
      setLoading(true);
      await payRDInstallment(rdNumber);
      setMessage({ type: 'success', text: 'Installment paid successfully!' });
      loadRecurringDeposits();
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to pay installment' });
    } finally {
      setLoading(false);
    }
  };

  const calculateInterestRate = (tenureMonths: number): number => {
    if (tenureMonths <= 6) return 5.5;
    if (tenureMonths <= 12) return 6.0;
    if (tenureMonths <= 24) return 6.5;
    if (tenureMonths <= 36) return 7.0;
    return 7.5;
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 2
    }).format(amount);
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'bg-green-100 text-green-800';
      case 'MATURED': return 'bg-blue-100 text-blue-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const currentInterestRate = calculateInterestRate(formData.tenureMonths);

  return (
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Recurring Deposits</h1>

      {message && (
        <div className={`mb-4 p-4 rounded ${message.type === 'success' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
          {message.text}
          <button onClick={() => setMessage(null)} className="float-right font-bold">×</button>
        </div>
      )}

      {/* Tabs */}
      <div className="mb-6 border-b">
        <div className="flex space-x-4">
          <button
            onClick={() => setActiveTab('view')}
            className={`pb-2 px-4 font-medium ${
              activeTab === 'view'
                ? 'border-b-2 border-blue-500 text-blue-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            My Recurring Deposits
          </button>
          <button
            onClick={() => setActiveTab('create')}
            className={`pb-2 px-4 font-medium ${
              activeTab === 'create'
                ? 'border-b-2 border-blue-500 text-blue-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Create Recurring Deposit
          </button>
        </div>
      </div>

      {/* View RDs Tab */}
      {activeTab === 'view' && (
        <div>
          {loading ? (
            <div className="text-center py-8">Loading recurring deposits...</div>
          ) : recurringDeposits.length === 0 ? (
            <div className="text-center py-12 bg-gray-50 rounded-lg">
              <p className="text-gray-500 mb-4">No recurring deposits found</p>
              <button
                onClick={() => setActiveTab('create')}
                className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
              >
                Create Your First RD
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {recurringDeposits.map((rd) => (
                <div key={rd.rdNumber} className="bg-white p-6 rounded-lg shadow border">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-lg font-semibold">{rd.rdNumber}</h3>
                      <span className={`inline-block px-2 py-1 text-xs rounded mt-1 ${getStatusColor(rd.status)}`}>
                        {rd.status}
                      </span>
                    </div>
                    {rd.status === 'ACTIVE' && (
                      <button
                        onClick={() => handlePayInstallment(rd.rdNumber)}
                        disabled={loading}
                        className="bg-green-500 text-white px-4 py-2 rounded text-sm hover:bg-green-600 disabled:bg-gray-400"
                      >
                        Pay Installment
                      </button>
                    )}
                  </div>

                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                      <p className="text-sm text-gray-500">Monthly Installment</p>
                      <p className="font-semibold">{formatCurrency(rd.monthlyInstallment)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Interest Rate</p>
                      <p className="font-semibold">{rd.interestRate.toFixed(2)}%</p>
                    </div>
                  </div>

                  {/* Progress Bar */}
                  <div className="mb-4">
                    <div className="flex justify-between text-sm mb-1">
                      <span className="text-gray-500">Installments Paid</span>
                      <span className="font-medium">{rd.installmentsPaid} / {rd.tenureMonths}</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className="bg-blue-500 h-2 rounded-full transition-all"
                        style={{ width: `${(rd.installmentsPaid / rd.tenureMonths) * 100}%` }}
                      ></div>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                      <p className="text-sm text-gray-500">Total Deposited</p>
                      <p className="font-semibold text-blue-600">
                        {formatCurrency(rd.monthlyInstallment * rd.installmentsPaid)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Maturity Amount</p>
                      <p className="font-semibold text-green-600">{formatCurrency(rd.maturityAmount)}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 text-sm border-t pt-4">
                    <div>
                      <p className="text-gray-500">Next Installment</p>
                      <p className="font-medium">{formatDate(rd.nextInstallmentDate)}</p>
                    </div>
                    <div>
                      <p className="text-gray-500">Maturity Date</p>
                      <p className="font-medium">{formatDate(rd.maturityDate)}</p>
                    </div>
                  </div>

                  {rd.autoDebit && (
                    <div className="mt-3 text-xs text-green-600 flex items-center">
                      <span className="mr-1">✓</span> Auto-debit enabled
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Create RD Tab */}
      {activeTab === 'create' && (
        <div className="max-w-2xl">
          <form onSubmit={handleCreateRD} className="bg-white p-6 rounded-lg shadow">
            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">Select Account</label>
              <select
                value={formData.accountNumber}
                onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
                required
              >
                {accounts.map((acc) => (
                  <option key={acc.accountNumber} value={acc.accountNumber}>
                    {acc.accountNumber} - {acc.accountType} (Balance: {formatCurrency(acc.balance)})
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">
                Monthly Installment Amount (Min: ₹500)
              </label>
              <input
                type="number"
                min="500"
                step="100"
                value={formData.monthlyInstallment}
                onChange={(e) => setFormData({ ...formData, monthlyInstallment: Number(e.target.value) })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">
                Tenure: {formData.tenureMonths} months - Interest Rate: {currentInterestRate}% p.a.
              </label>
              <input
                type="range"
                min="12"
                max="120"
                step="6"
                value={formData.tenureMonths}
                onChange={(e) => setFormData({ ...formData, tenureMonths: Number(e.target.value) })}
                className="w-full"
              />
              <div className="flex justify-between text-xs text-gray-500 mt-1">
                <span>12 months</span>
                <span>60 months</span>
                <span>120 months</span>
              </div>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">Maturity Action</label>
              <select
                value={formData.maturityAction}
                onChange={(e) => setFormData({ ...formData, maturityAction: e.target.value as MaturityAction })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
              >
                <option value={MaturityAction.CREDIT_TO_ACCOUNT}>Credit to Account</option>
                <option value={MaturityAction.RENEW_PRINCIPAL}>Renew Principal Only</option>
                <option value={MaturityAction.RENEW_PRINCIPAL_AND_INTEREST}>Renew Principal + Interest</option>
              </select>
            </div>

            <div className="mb-6">
              <label className="flex items-center">
                <input
                  type="checkbox"
                  checked={formData.autoDebit}
                  onChange={(e) => setFormData({ ...formData, autoDebit: e.target.checked })}
                  className="mr-2"
                />
                <span className="text-sm">Enable auto-debit for monthly installments</span>
              </label>
            </div>

            {/* Summary */}
            <div className="bg-blue-50 p-4 rounded mb-6">
              <h4 className="font-semibold mb-3">Investment Summary</h4>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Monthly Installment:</span>
                  <span className="font-medium">{formatCurrency(formData.monthlyInstallment)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Tenure:</span>
                  <span className="font-medium">{formData.tenureMonths} months</span>
                </div>
                <div className="flex justify-between">
                  <span>Interest Rate:</span>
                  <span className="font-medium">{currentInterestRate}% p.a.</span>
                </div>
                <div className="flex justify-between">
                  <span>Total Installments:</span>
                  <span className="font-medium">{formData.tenureMonths}</span>
                </div>
                <div className="flex justify-between border-t pt-2">
                  <span>Total Investment:</span>
                  <span className="font-semibold">{formatCurrency(formData.monthlyInstallment * formData.tenureMonths)}</span>
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-500 text-white py-3 rounded hover:bg-blue-600 disabled:bg-gray-400"
            >
              {loading ? 'Creating...' : 'Create Recurring Deposit'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default RecurringDepositsPage;
