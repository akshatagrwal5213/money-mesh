import React, { useState, useEffect } from 'react'
import { paymentService } from '../services/paymentService'

const QrCodePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'generate' | 'active' | 'history'>('generate')
  const [type, setType] = useState<'STATIC' | 'DYNAMIC' | 'MERCHANT' | 'PERSONAL'>('STATIC')
  const [amount, setAmount] = useState('')
  const [merchantName, setMerchantName] = useState('')
  const [validityMinutes, setValidityMinutes] = useState('60')
  const [maxUsageLimit, setMaxUsageLimit] = useState('1')
  const [activeQrCodes, setActiveQrCodes] = useState<any[]>([])
  const [history, setHistory] = useState<any[]>([])
  const [loading, setLoading] = useState(false)
  const [message, setMessage] = useState('')
  const [generatedQr, setGeneratedQr] = useState<any>(null)

  useEffect(() => {
    if (activeTab === 'active') {
      fetchActiveQrCodes()
    } else if (activeTab === 'history') {
      fetchHistory()
    }
  }, [activeTab])

  const fetchActiveQrCodes = async () => {
    try {
      setLoading(true)
      const data = await paymentService.getActiveQrCodes()
      setActiveQrCodes(data)
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Failed to load active QR codes')
    } finally {
      setLoading(false)
    }
  }

  const fetchHistory = async () => {
    try {
      setLoading(true)
      const data = await paymentService.getQrCodeHistory()
      setHistory(data.content || data)
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Failed to load history')
    } finally {
      setLoading(false)
    }
  }

  const handleGenerateQr = async (e: React.FormEvent) => {
    e.preventDefault()
    setMessage('')
    setGeneratedQr(null)
    
    try {
      setLoading(true)
      const response = await paymentService.generateQrCode({
        type,
        amount: amount ? parseFloat(amount) : undefined,
        merchantName: merchantName || undefined,
        validityMinutes: type === 'DYNAMIC' ? parseInt(validityMinutes) : undefined,
        maxUsageLimit: type === 'DYNAMIC' ? parseInt(maxUsageLimit) : undefined
      })
      
      setGeneratedQr(response)
      setMessage('QR Code generated successfully!')
      setAmount('')
      setMerchantName('')
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Failed to generate QR code')
    } finally {
      setLoading(false)
    }
  }

  const handleDeactivate = async (qrCodeId: string) => {
    if (!confirm('Are you sure you want to deactivate this QR code?')) return
    
    try {
      await paymentService.deactivateQrCode(qrCodeId)
      setMessage('QR code deactivated successfully')
      fetchActiveQrCodes()
    } catch (error: any) {
      setMessage(error.response?.data?.message || 'Failed to deactivate QR code')
    }
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">QR Code Payments</h1>

      {/* Tabs */}
      <div className="flex gap-4 mb-6 border-b">
        <button
          onClick={() => setActiveTab('generate')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'generate'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-600'
          }`}
        >
          Generate QR
        </button>
        <button
          onClick={() => setActiveTab('active')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'active'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-600'
          }`}
        >
          Active QR Codes
        </button>
        <button
          onClick={() => setActiveTab('history')}
          className={`px-4 py-2 font-medium ${
            activeTab === 'history'
              ? 'border-b-2 border-blue-600 text-blue-600'
              : 'text-gray-600'
          }`}
        >
          History
        </button>
      </div>

      {message && (
        <div className={`p-4 mb-4 rounded ${message.includes('success') ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
          {message}
        </div>
      )}

      {activeTab === 'generate' && (
        <div className="grid md:grid-cols-2 gap-6">
          <div className="bg-white shadow rounded-lg p-6">
            <h2 className="text-xl font-semibold mb-4">Generate QR Code</h2>
            
            <form onSubmit={handleGenerateQr} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-1">QR Type</label>
                <select
                  value={type}
                  onChange={(e) => setType(e.target.value as any)}
                  className="w-full border rounded px-3 py-2"
                >
                  <option value="STATIC">Static (Fixed Amount)</option>
                  <option value="DYNAMIC">Dynamic (Variable Amount)</option>
                  <option value="MERCHANT">Merchant</option>
                  <option value="PERSONAL">Personal</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">
                  Amount (₹) {type === 'STATIC' ? '*' : '(Optional)'}
                </label>
                <input
                  type="number"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  placeholder="0.00"
                  step="0.01"
                  min="1"
                  className="w-full border rounded px-3 py-2"
                  required={type === 'STATIC'}
                />
              </div>

              <div>
                <label className="block text-sm font-medium mb-1">Merchant Name (Optional)</label>
                <input
                  type="text"
                  value={merchantName}
                  onChange={(e) => setMerchantName(e.target.value)}
                  placeholder="Your business name"
                  className="w-full border rounded px-3 py-2"
                />
              </div>

              {type === 'DYNAMIC' && (
                <>
                  <div>
                    <label className="block text-sm font-medium mb-1">Validity (Minutes)</label>
                    <input
                      type="number"
                      value={validityMinutes}
                      onChange={(e) => setValidityMinutes(e.target.value)}
                      min="1"
                      className="w-full border rounded px-3 py-2"
                    />
                  </div>

                  <div>
                    <label className="block text-sm font-medium mb-1">Max Usage Limit</label>
                    <input
                      type="number"
                      value={maxUsageLimit}
                      onChange={(e) => setMaxUsageLimit(e.target.value)}
                      min="1"
                      className="w-full border rounded px-3 py-2"
                    />
                  </div>
                </>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-gray-400"
              >
                {loading ? 'Generating...' : 'Generate QR Code'}
              </button>
            </form>
          </div>

          {generatedQr && (
            <div className="bg-white shadow rounded-lg p-6">
              <h2 className="text-xl font-semibold mb-4">Generated QR Code</h2>
              
              <div className="space-y-3">
                <div className="bg-gray-100 p-4 rounded text-center">
                  <div className="text-sm text-gray-600 mb-2">QR Code Data:</div>
                  <div className="bg-white p-4 rounded border-2 border-dashed border-gray-300">
                    <code className="text-xs break-all">{generatedQr.qrData}</code>
                  </div>
                  <p className="text-xs text-gray-500 mt-2">
                    Use a QR code library to display this as an image
                  </p>
                </div>

                <div className="border-t pt-3">
                  <p className="text-sm"><strong>QR ID:</strong> {generatedQr.qrCodeId}</p>
                  <p className="text-sm"><strong>Type:</strong> {generatedQr.type}</p>
                  {generatedQr.amount && (
                    <p className="text-sm"><strong>Amount:</strong> ₹{generatedQr.amount}</p>
                  )}
                  <p className="text-sm"><strong>Merchant:</strong> {generatedQr.merchantName}</p>
                  <p className="text-sm"><strong>Generated:</strong> {new Date(generatedQr.generatedDate).toLocaleString()}</p>
                  {generatedQr.expiryDate && (
                    <p className="text-sm"><strong>Expires:</strong> {new Date(generatedQr.expiryDate).toLocaleString()}</p>
                  )}
                  <p className="text-sm"><strong>Usage:</strong> {generatedQr.usageCount} times</p>
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {activeTab === 'active' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">Active QR Codes</h2>
          
          {loading ? (
            <p>Loading...</p>
          ) : activeQrCodes.length === 0 ? (
            <p className="text-gray-600">No active QR codes found</p>
          ) : (
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-4">
              {activeQrCodes.map((qr) => (
                <div key={qr.qrCodeId} className="border rounded p-4">
                  <div className="mb-2">
                    <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded">
                      {qr.type}
                    </span>
                  </div>
                  <p className="font-medium">{qr.merchantName}</p>
                  {qr.amount && <p className="text-lg">₹{qr.amount}</p>}
                  <p className="text-sm text-gray-600 mt-2">
                    Used: {qr.usageCount} times
                  </p>
                  <p className="text-xs text-gray-500">
                    ID: {qr.qrCodeId}
                  </p>
                  {qr.expiryDate && (
                    <p className="text-xs text-gray-500">
                      Expires: {new Date(qr.expiryDate).toLocaleString()}
                    </p>
                  )}
                  <button
                    onClick={() => handleDeactivate(qr.qrCodeId)}
                    className="mt-3 w-full bg-red-600 text-white text-sm py-1 rounded hover:bg-red-700"
                  >
                    Deactivate
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {activeTab === 'history' && (
        <div className="bg-white shadow rounded-lg p-6">
          <h2 className="text-xl font-semibold mb-4">QR Code History</h2>
          
          {loading ? (
            <p>Loading...</p>
          ) : history.length === 0 ? (
            <p className="text-gray-600">No QR codes found</p>
          ) : (
            <div className="space-y-3">
              {history.map((qr) => (
                <div key={qr.qrCodeId} className="border rounded p-4">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-medium">{qr.merchantName}</p>
                      <p className="text-sm text-gray-600">Type: {qr.type}</p>
                      {qr.amount && <p className="text-sm">Amount: ₹{qr.amount}</p>}
                      <p className="text-xs text-gray-500 mt-1">
                        Generated: {new Date(qr.generatedDate).toLocaleString()}
                      </p>
                    </div>
                    <div className="text-right">
                      <span className={`text-sm font-medium ${qr.isActive ? 'text-green-600' : 'text-gray-600'}`}>
                        {qr.isActive ? 'Active' : 'Inactive'}
                      </span>
                      <p className="text-xs text-gray-500 mt-1">
                        Used: {qr.usageCount} times
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

export default QrCodePage
