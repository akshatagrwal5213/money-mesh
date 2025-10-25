import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { paymentService } from '../services/paymentService'

const BillPaymentsPage: React.FC = () => {
  const [bills, setBills] = useState<any[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [filter, setFilter] = useState<'all' | 'autopay'>('all')

  useEffect(() => {
    fetchBills()
  }, [filter])

  const fetchBills = async () => {
    try {
      setLoading(true)
      const data = filter === 'autopay' 
        ? await paymentService.getAutoPayBills()
        : await paymentService.getBillPaymentHistory()
      setBills(data.content || data)
      setLoading(false)
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to fetch bills.')
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

  if (loading) {
    return <div className="p-6">Loading...</div>
  }

  if (error) {
    return <div className="p-6 text-red-600">{error}</div>
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Bill Payments</h1>
        <Link to="/pay-bill" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
          Pay New Bill
        </Link>
      </div>

      <div className="mb-6 flex gap-4">
        <button
          onClick={() => setFilter('all')}
          className={`px-4 py-2 rounded ${
            filter === 'all'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          }`}
        >
          All Bills
        </button>
        <button
          onClick={() => setFilter('autopay')}
          className={`px-4 py-2 rounded ${
            filter === 'autopay'
              ? 'bg-blue-600 text-white'
              : 'bg-gray-200 text-gray-700'
          }`}
        >
          Auto-Pay Bills
        </button>
      </div>

      <div className="bg-white shadow rounded-lg overflow-hidden">
        {bills.length === 0 ? (
          <div className="p-6 text-center text-gray-600">
            No bills found
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Bill ID</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Biller</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Type</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Amount</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Due Date</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Payment Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {bills.map((bill) => (
                  <tr key={bill.billPaymentId} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm text-gray-900">{bill.billPaymentId}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{bill.billerName}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{bill.billType}</td>
                    <td className="px-6 py-4 text-sm font-medium">₹{bill.totalAmount.toFixed(2)}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {bill.dueDate ? new Date(bill.dueDate).toLocaleDateString() : 'N/A'}
                    </td>
                    <td className="px-6 py-4 text-sm">
                      <span className={`font-medium ${getStatusColor(bill.paymentStatus)}`}>
                        {bill.paymentStatus}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      {new Date(bill.paymentDate).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

export default BillPaymentsPage
