import React, { useState, useEffect } from 'react';
import { accountService } from '../services/accountService';
import { 
  createSip, 
  getMySips, 
  cancelSip,
  getAllMutualFunds,
  SipInvestment,
  SipRequest,
  SipFrequency,
  MutualFund
} from '../services/investmentService';

const SipPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'view' | 'create'>('view');
  const [sips, setSips] = useState<SipInvestment[]>([]);
  const [mutualFunds, setMutualFunds] = useState<MutualFund[]>([]);
  const [accounts, setAccounts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Create SIP form state
  const [formData, setFormData] = useState<SipRequest>({
    accountNumber: '',
    fundCode: '',
    frequency: SipFrequency.MONTHLY,
    installmentAmount: 1000,
    totalInstallments: 12,
    startDate: new Date().toISOString().split('T')[0],
    autoDebit: true
  });

  useEffect(() => {
    loadSips();
    loadMutualFunds();
    loadAccounts();
  }, []);

  const loadSips = async () => {
    try {
      setLoading(true);
      const data = await getMySips();
      setSips(data);
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to load SIPs' });
    } finally {
      setLoading(false);
    }
  };

  const loadMutualFunds = async () => {
    try {
      const data = await getAllMutualFunds();
      setMutualFunds(data);
      if (data.length > 0) {
        setFormData(prev => ({ ...prev, fundCode: data[0].fundCode }));
      }
    } catch (error) {
      console.error('Failed to load mutual funds', error);
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

  const handleCreateSip = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      await createSip(formData);
      setMessage({ type: 'success', text: 'SIP created successfully!' });
      setFormData({
        accountNumber: accounts[0]?.accountNumber || '',
        fundCode: mutualFunds[0]?.fundCode || '',
        frequency: SipFrequency.MONTHLY,
        installmentAmount: 1000,
        totalInstallments: 12,
        startDate: new Date().toISOString().split('T')[0],
        autoDebit: true
      });
      setActiveTab('view');
      loadSips();
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to create SIP' });
    } finally {
      setLoading(false);
    }
  };

  const handleCancelSip = async (sipNumber: string) => {
    if (!window.confirm('Are you sure you want to cancel this SIP? This action cannot be undone.')) return;

    try {
      setLoading(true);
      await cancelSip(sipNumber);
      setMessage({ type: 'success', text: 'SIP cancelled successfully!' });
      loadSips();
    } catch (error: any) {
      setMessage({ type: 'error', text: error.response?.data?.message || 'Failed to cancel SIP' });
    } finally {
      setLoading(false);
    }
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
      case 'COMPLETED': return 'bg-blue-100 text-blue-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getFrequencyLabel = (frequency: string) => {
    switch (frequency) {
      case 'DAILY': return 'Daily';
      case 'WEEKLY': return 'Weekly';
      case 'MONTHLY': return 'Monthly';
      case 'QUARTERLY': return 'Quarterly';
      default: return frequency;
    }
  };

  const selectedFund = mutualFunds.find(f => f.fundCode === formData.fundCode);
  const estimatedUnits = selectedFund ? (formData.installmentAmount / selectedFund.currentNav) : 0;
  const totalInvestment = formData.installmentAmount * (formData.totalInstallments || 0);

  return (
    <div className="max-w-7xl mx-auto">
      <h1 className="text-3xl font-bold mb-6">Systematic Investment Plan (SIP)</h1>

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
            My SIPs
          </button>
          <button
            onClick={() => setActiveTab('create')}
            className={`pb-2 px-4 font-medium ${
              activeTab === 'create'
                ? 'border-b-2 border-blue-500 text-blue-600'
                : 'text-gray-500 hover:text-gray-700'
            }`}
          >
            Start New SIP
          </button>
        </div>
      </div>

      {/* View SIPs Tab */}
      {activeTab === 'view' && (
        <div>
          {loading ? (
            <div className="text-center py-8">Loading SIPs...</div>
          ) : sips.length === 0 ? (
            <div className="text-center py-12 bg-gray-50 rounded-lg">
              <p className="text-gray-500 mb-4">No SIPs found</p>
              <button
                onClick={() => setActiveTab('create')}
                className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
              >
                Start Your First SIP
              </button>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {sips.map((sip) => (
                <div key={sip.sipNumber} className="bg-white p-6 rounded-lg shadow border">
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <h3 className="text-lg font-semibold">{sip.mutualFund.fundName}</h3>
                      <p className="text-sm text-gray-500">{sip.sipNumber}</p>
                      <span className={`inline-block px-2 py-1 text-xs rounded mt-1 ${getStatusColor(sip.status)}`}>
                        {sip.status}
                      </span>
                    </div>
                    {sip.status === 'ACTIVE' && (
                      <button
                        onClick={() => handleCancelSip(sip.sipNumber)}
                        disabled={loading}
                        className="bg-red-500 text-white px-4 py-2 rounded text-sm hover:bg-red-600 disabled:bg-gray-400"
                      >
                        Cancel SIP
                      </button>
                    )}
                  </div>

                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                      <p className="text-sm text-gray-500">Installment Amount</p>
                      <p className="font-semibold">{formatCurrency(sip.installmentAmount)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Frequency</p>
                      <p className="font-semibold">{getFrequencyLabel(sip.frequency)}</p>
                    </div>
                  </div>

                  {/* Progress Bar */}
                  <div className="mb-4">
                    <div className="flex justify-between text-sm mb-1">
                      <span className="text-gray-500">Installments Executed</span>
                      <span className="font-medium">{sip.installmentsExecuted} {sip.totalInstallments ? `/ ${sip.totalInstallments}` : ''}</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className="bg-green-500 h-2 rounded-full transition-all"
                        style={{ width: `${sip.totalInstallments ? (sip.installmentsExecuted / sip.totalInstallments) * 100 : 0}%` }}
                      ></div>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 mb-4">
                    <div>
                      <p className="text-sm text-gray-500">Total Invested</p>
                      <p className="font-semibold text-blue-600">
                        {formatCurrency(sip.installmentAmount * sip.installmentsExecuted)}
                      </p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-500">Units Acquired</p>
                      <p className="font-semibold text-purple-600">{sip.totalUnits.toFixed(4)}</p>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-4 text-sm border-t pt-4">
                    <div>
                      <p className="text-gray-500">Next Installment</p>
                      <p className="font-medium">{formatDate(sip.nextInstallmentDate)}</p>
                    </div>
                    <div>
                      <p className="text-gray-500">Started On</p>
                      <p className="font-medium">{formatDate(sip.startDate)}</p>
                    </div>
                  </div>

                  {sip.status === 'ACTIVE' && sip.totalInstallments && (
                    <div className="mt-3 p-3 bg-green-50 rounded text-sm">
                      <p className="text-green-800">
                        💡 Remaining: {formatCurrency(sip.installmentAmount * (sip.totalInstallments - sip.installmentsExecuted))}
                      </p>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Create SIP Tab */}
      {activeTab === 'create' && (
        <div className="max-w-2xl">
          <form onSubmit={handleCreateSip} className="bg-white p-6 rounded-lg shadow">
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
              <label className="block text-sm font-medium mb-2">Select Mutual Fund</label>
              <select
                value={formData.fundCode}
                onChange={(e) => setFormData({ ...formData, fundCode: e.target.value })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
                required
              >
                {mutualFunds.map((fund) => (
                  <option key={fund.fundCode} value={fund.fundCode}>
                    {fund.fundName} - NAV: {formatCurrency(fund.currentNav)}
                  </option>
                ))}
              </select>
              {selectedFund && (
                <div className="mt-2 text-sm text-gray-600 p-3 bg-gray-50 rounded">
                  <p><strong>AMC:</strong> {selectedFund.amc}</p>
                  <p><strong>Category:</strong> {selectedFund.category}</p>
                  <p><strong>Min SIP:</strong> {formatCurrency(selectedFund.minSipAmount)}</p>
                </div>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">
                Installment Amount (Min: ₹{selectedFund?.minSipAmount || 500})
              </label>
              <input
                type="number"
                min={selectedFund?.minSipAmount || 500}
                step="100"
                value={formData.installmentAmount}
                onChange={(e) => setFormData({ ...formData, installmentAmount: Number(e.target.value) })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
                required
              />
              {selectedFund && (
                <p className="mt-1 text-sm text-gray-500">
                  Estimated units per installment: {estimatedUnits.toFixed(4)} units
                </p>
              )}
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">Frequency</label>
              <select
                value={formData.frequency}
                onChange={(e) => setFormData({ ...formData, frequency: e.target.value as SipFrequency })}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
              >
                <option value={SipFrequency.MONTHLY}>Monthly</option>
                <option value={SipFrequency.WEEKLY}>Weekly</option>
                <option value={SipFrequency.QUARTERLY}>Quarterly</option>
              </select>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">
                Number of Installments: {formData.totalInstallments}
              </label>
              <input
                type="range"
                min="6"
                max="120"
                step="6"
                value={formData.totalInstallments}
                onChange={(e) => setFormData({ ...formData, totalInstallments: Number(e.target.value) })}
                className="w-full"
              />
              <div className="flex justify-between text-xs text-gray-500 mt-1">
                <span>6 installments</span>
                <span>60 installments</span>
                <span>120 installments</span>
              </div>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium mb-2">Start Date</label>
              <input
                type="date"
                value={formData.startDate}
                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                min={new Date().toISOString().split('T')[0]}
                className="w-full p-3 border rounded focus:ring-2 focus:ring-blue-500"
                required
              />
            </div>

            {/* Summary */}
            <div className="bg-blue-50 p-4 rounded mb-6">
              <h4 className="font-semibold mb-3">SIP Summary</h4>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Fund:</span>
                  <span className="font-medium">{selectedFund?.fundName}</span>
                </div>
                <div className="flex justify-between">
                  <span>Installment Amount:</span>
                  <span className="font-medium">{formatCurrency(formData.installmentAmount)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Frequency:</span>
                  <span className="font-medium">{getFrequencyLabel(formData.frequency)}</span>
                </div>
                <div className="flex justify-between">
                  <span>Number of Installments:</span>
                  <span className="font-medium">{formData.totalInstallments || 'Perpetual'}</span>
                </div>
                <div className="flex justify-between">
                  <span>Estimated Total Units:</span>
                  <span className="font-medium">{(estimatedUnits * (formData.totalInstallments || 0)).toFixed(4)}</span>
                </div>
                <div className="flex justify-between border-t pt-2">
                  <span>Total Investment:</span>
                  <span className="font-semibold">{formatCurrency(totalInvestment)}</span>
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-500 text-white py-3 rounded hover:bg-blue-600 disabled:bg-gray-400"
            >
              {loading ? 'Creating SIP...' : 'Start SIP'}
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default SipPage;
