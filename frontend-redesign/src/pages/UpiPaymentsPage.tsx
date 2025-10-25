import React, { useState, useEffect } from 'react'
import { paymentService } from '../services/paymentService'

const UpiPaymentsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'send' | 'history'>('send')
  const [upiId, setUpiId] = useState('')
  const [receiverUpiId, setReceiverUpiId] = useState('')
  const [amount, setAmount] = useState('')
  const [provider, setProvider] = useState<'GOOGLE_PAY' | 'PHONEPE' | 'PAYTM' | 'AMAZON_PAY' | 'BHIM' | 'WHATSAPP_PAY' | 'BANK_UPI' | 'OTHER'>('GOOGLE_PAY')
  const [description, setDescription] = useState('')
  const [history, setHistory] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (activeTab === 'history') {
      fetchHistory()
    }
  }, [activeTab])

  const fetchHistory = async () => {
    try {
      setLoading(true)
      const data = await paymentService.getUpiTransactionHistory()
      setHistory(data.content || data)
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Failed to load history')
    } finally {
      setLoading(false)
    }
  }

  const handleSendPayment = async (e: React.FormEvent) => {
    e.preventDefault()
    setMessage('')
    
    try {
      setLoading(true)
      const response = await paymentService.initiateUpiPayment({
        upiId,
        receiverUpiId,
        amount: parseFloat(amount),
        provider,
        description
      })
      
      setMessage(`Payment successful! Transaction ID: ${response.upiTransactionId}`)
      setReceiverUpiId('')
      setAmount('')
      setDescription('')
      
      // Refresh history
      if (activeTab === 'history') {
        fetchHistory()
      }
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Payment failed')
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'SUCCESS': return 'text-green-600'
      case 'FAILED': return 'text-red-600'
      case 'PENDING': return 'text-yellow-600'
      default: return 'text-gray-600'
    }
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">UPI Payments</h1>

      {/* Tabs */}
      <div className="flex gap-4 mb-6 border-b">
        <button
          onClick={() => setActiveTab('send')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'send'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-600'
          }`}
        >
          Send Payment
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'history'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-600'
          }`}
        >
          Transaction History
        </button>
      </div>

      {message && (
        <div className={`p-4 mb-4 rounded ${message.includes('success') ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
          {message}
        </div>
      )}

      {activeTab === 'send' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Send UPI Payment</h2>
          
          <form onSubmit={handleSendPayment} className="space-y-4">
            <div>
              <label className="block text-sm font-medium mb-1">Your UPI ID</label>
              <input
                type="text"
                value={upiId}
                onChange={(e) => setUpiId(e.target.value)}
                placeholder="yourname@bankingsystem"
                className="w-full border rounded px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Receiver UPI ID</label>
              <input
                type="text"
                value={receiverUpiId}
                onChange={(e) => setReceiverUpiId(e.target.value)}
                placeholder="receiver@paytm"
                className="w-full border rounded px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Amount (₹)</label>
              <input
                type="number"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                placeholder="0.00"
                step="0.01"
                min="1"
                className="w-full border rounded px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">UPI Provider</label>
              <select
                value={provider}
                onChange={(e) => setProvider(e.target.value as any)}
                className="w-full border rounded px-3 py-2"
              >
                <option value="GOOGLE_PAY">Google Pay</option>
                <option value="PHONEPE">PhonePe</option>
                <option value="PAYTM">Paytm</option>
                <option value="AMAZON_PAY">Amazon Pay</option>
                <option value="BHIM">BHIM</option>
                <option value="WHATSAPP_PAY">WhatsApp Pay</option>
                <option value="BANK_UPI">Bank UPI</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium mb-1">Description (Optional)</label>
              <input
                type="text"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Payment for..."
                className="w-full border rounded px-3 py-2"
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
            >
              {loading ? 'Processing...' : 'Send Payment'}
            </button>
          </form>
        </div>
      )}

      {activeTab === 'history' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Transaction History</h2>
          
          {loading ? (
            <p>Loading...</p>
          ) : history.length === 0 ? (
            <p className="text-gray-600">No transactions found</p>
          ) : (
            <div className="space-y-3">
              {history.map((tx) => (
                <div key={tx.upiTransactionId} className="border rounded p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium">₹{tx.amount.toFixed(2)}</p>
                      <p className="text-sm text-gray-600">To: {tx.receiverUpiId}</p>
                      <p className="text-sm text-gray-600">{tx.description}</p>
                      <p className="text-xs text-gray-500 mt-1">
                        {new Date(tx.transactionDate).toLocaleString()}
                      </p>
                    </div>
                    <div className="text-right">
                      <span className={`text-sm font-medium ${getStatusColor(tx.status)}`}>
                        {tx.status}
                      </span>
                      <p className="text-xs text-gray-500 mt-1">
                        {tx.provider}
                      </p>
                      <p className="text-xs text-gray-500">
                        Ref: {tx.referenceNumber}
                      </p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

export default UpiPaymentsPage
