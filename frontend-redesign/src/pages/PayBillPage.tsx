import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { paymentService } from '../services/paymentService'

const PayBillPage: React.FC = () => {
  const [billType, setBillType] = useState<'ELECTRICITY' | 'WATER' | 'GAS' | 'MOBILE' | 'BROADBAND' | 'DTH' | 'INSURANCE' | 'CREDIT_CARD' | 'LOAN_EMI' | 'MUNICIPAL_TAX' | 'EDUCATION' | 'SUBSCRIPTION' | 'OTHER'>('ELECTRICITY')
  const [billerName, setBillerName] = useState('')
  const [billerCode, setBillerCode] = useState('')
  const [consumerNumber, setConsumerNumber] = useState('')
  const [billAmount, setBillAmount] = useState('')
  const [convenienceFee, setConvenienceFee] = useState('0')
  const [paymentMethod, setPaymentMethod] = useState<'UPI' | 'DEBIT_CARD' | 'CREDIT_CARD' | 'NET_BANKING'>('UPI')
  const [dueDate, setDueDate] = useState('')
  const [enableAutoPay, setEnableAutoPay] = useState(false)
  const [remarks, setRemarks] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')
    setSuccess('')

    try {
      setLoading(true)
      const response = await paymentService.payBill({
        billType,
        billerName,
        billerCode,
        consumerNumber,
        billAmount: parseFloat(billAmount),
        convenienceFee: parseFloat(convenienceFee),
        paymentMethod,
        dueDate: dueDate || undefined,
        remarks: remarks || undefined,
        enableAutoPay
      })
      
      setSuccess(`Bill paid successfully! Receipt: ${response.receiptNumber}`)
      setTimeout(() => navigate('/bill-payments'), 2000)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to pay bill.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="max-w-2xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">Pay a Bill</h1>
      
      {error && (
        <div className="bg-red-100 text-red-800 p-4 rounded mb-4">{error}</div>
      )}
      {success && (
        <div className="bg-green-100 text-green-800 p-4 rounded mb-4">{success}</div>
      )}

      <div className="bg-white shadow rounded-lg p-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1">Bill Type *</label>
            <select
              value={billType}
              onChange={(e) => setBillType(e.target.value as any)}
              className="w-full border rounded px-3 py-2"
              required
            >
              <option value="ELECTRICITY">Electricity</option>
              <option value="WATER">Water</option>
              <option value="GAS">Gas</option>
              <option value="MOBILE">Mobile</option>
              <option value="BROADBAND">Broadband</option>
              <option value="DTH">DTH</option>
              <option value="INSURANCE">Insurance</option>
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="LOAN_EMI">Loan EMI</option>
              <option value="MUNICIPAL_TAX">Municipal Tax</option>
              <option value="EDUCATION">Education</option>
              <option value="SUBSCRIPTION">Subscription</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Biller Name *</label>
            <input
              type="text"
              value={billerName}
              onChange={(e) => setBillerName(e.target.value)}
              placeholder="e.g., Tata Power, Airtel"
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Biller Code *</label>
            <input
              type="text"
              value={billerCode}
              onChange={(e) => setBillerCode(e.target.value)}
              placeholder="Unique biller identifier"
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Consumer Number *</label>
            <input
              type="text"
              value={consumerNumber}
              onChange={(e) => setConsumerNumber(e.target.value)}
              placeholder="Your account/consumer number"
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Bill Amount (₹) *</label>
            <input
              type="number"
              step="0.01"
              value={billAmount}
              onChange={(e) => setBillAmount(e.target.value)}
              placeholder="0.00"
              min="1"
              className="w-full border rounded px-3 py-2"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Convenience Fee (₹)</label>
            <input
              type="number"
              step="0.01"
              value={convenienceFee}
              onChange={(e) => setConvenienceFee(e.target.value)}
              placeholder="0.00"
              min="0"
              className="w-full border rounded px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Payment Method *</label>
            <select
              value={paymentMethod}
              onChange={(e) => setPaymentMethod(e.target.value as any)}
              className="w-full border rounded px-3 py-2"
              required
            >
              <option value="UPI">UPI</option>
              <option value="DEBIT_CARD">Debit Card</option>
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="NET_BANKING">Net Banking</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Due Date</label>
            <input
              type="date"
              value={dueDate}
              onChange={(e) => setDueDate(e.target.value)}
              className="w-full border rounded px-3 py-2"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Remarks</label>
            <textarea
              value={remarks}
              onChange={(e) => setRemarks(e.target.value)}
              placeholder="Optional notes"
              className="w-full border rounded px-3 py-2"
              rows={3}
            />
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              id="autopay"
              checked={enableAutoPay}
              onChange={(e) => setEnableAutoPay(e.target.checked)}
              className="mr-2"
            />
            <label htmlFor="autopay" className="text-sm font-medium">
              Enable Auto-Pay for this bill
            </label>
          </div>

          <div className="bg-blue-50 p-4 rounded">
            <p className="text-sm font-medium">Total Amount</p>
            <p className="text-2xl font-bold text-blue-600">
              ₹{(parseFloat(billAmount || '0') + parseFloat(convenienceFee || '0')).toFixed(2)}
            </p>
          </div>

          <div className="flex gap-3">
            <button
              type="button"
              onClick={() => navigate('/bill-payments')}
              className="flex-1 bg-gray-200 text-gray-700 py-2 rounded hover:bg-gray-300"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
            >
              {loading ? 'Processing...' : 'Pay Bill'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default PayBillPage
