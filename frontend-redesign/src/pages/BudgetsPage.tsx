import React, { useState, useEffect } from 'react';
import {
  getAllBudgets,
  createBudget,
  updateBudget,
  deleteBudget,
  Budget,
  BudgetRequest,
  calculateBudgetProgress
} from '../services/module7Service';
import './BudgetsPage.css';

const BudgetsPage: React.FC = () => {
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingBudget, setEditingBudget] = useState<Budget | null>(null);
  const [formData, setFormData] = useState<BudgetRequest>({
    name: '',
    category: '',
    budgetAmount: 0,
    period: 'MONTHLY',
    startDate: new Date().toISOString().split('T')[0],
    endDate: '',
    alertThreshold: 80
  });

  useEffect(() => {
    loadBudgets();
  }, []);

  const loadBudgets = async () => {
    try {
      setLoading(true);
      const data = await getAllBudgets();
      setBudgets(data);
    } catch (error) {
      console.error('Failed to load budgets:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingBudget) {
        await updateBudget(editingBudget.id!, formData);
      } else {
        await createBudget(formData);
      }
      setShowModal(false);
      resetForm();
      loadBudgets();
    } catch (error) {
      console.error('Failed to save budget:', error);
      alert('Failed to save budget');
    }
  };

  const handleEdit = (budget: Budget) => {
    setEditingBudget(budget);
    setFormData({
      name: budget.name,
      category: budget.category,
      budgetAmount: budget.budgetAmount,
      period: budget.period as any,
      startDate: budget.startDate,
      endDate: budget.endDate || '',
      alertThreshold: budget.alertThreshold
    });
    setShowModal(true);
  };

  const handleDelete = async (budgetId: number) => {
    if (!confirm('Are you sure you want to delete this budget?')) return;
    try {
      await deleteBudget(budgetId);
      loadBudgets();
    } catch (error) {
      console.error('Failed to delete budget:', error);
      alert('Failed to delete budget');
    }
  };

  const resetForm = () => {
    setEditingBudget(null);
    setFormData({
      name: '',
      category: '',
      budgetAmount: 0,
      period: 'MONTHLY',
      startDate: new Date().toISOString().split('T')[0],
      endDate: '',
      alertThreshold: 80
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  };

  const getBudgetStatus = (budget: Budget) => {
    const progress = calculateBudgetProgress(budget);
    if (progress >= 100) return 'exceeded';
    if (progress >= budget.alertThreshold) return 'warning';
    return 'good';
  };

  const activeBudgets = budgets.filter(b => b.isActive);
  const inactiveBudgets = budgets.filter(b => !b.isActive);

  if (loading) {
    return (
      <div className="budgets-loading">
        <div className="spinner"></div>
        <p>Loading budgets...</p>
      </div>
    );
  }

  return (
    <div className="budgets-page">
      <div className="budgets-header">
        <h1>💰 Budget Management</h1>
        <button className="btn-primary" onClick={() => setShowModal(true)}>
          ➕ Create Budget
        </button>
      </div>

      {activeBudgets.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">📊</div>
          <h2>No Active Budgets</h2>
          <p>Create your first budget to start tracking your spending</p>
          <button className="btn-primary" onClick={() => setShowModal(true)}>
            Create Budget
          </button>
        </div>
      )}

      {activeBudgets.length > 0 && (
        <div className="budgets-grid">
          {activeBudgets.map(budget => {
            const progress = calculateBudgetProgress(budget);
            const status = getBudgetStatus(budget);
            
            return (
              <div key={budget.id} className={`budget-card ${status}`}>
                <div className="budget-header">
                  <div className="budget-title">
                    <h3>{budget.category}</h3>
                    <span className="budget-period">{budget.period}</span>
                  </div>
                  <div className="budget-actions">
                    <button onClick={() => handleEdit(budget)} title="Edit">
                      ✏️
                    </button>
                    <button onClick={() => handleDelete(budget.id!)} title="Delete">
                      🗑️
                    </button>
                  </div>
                </div>

                <div className="budget-amounts">
                  <div className="spent-amount">
                    <span className="label">Spent</span>
                    <span className="amount">{formatCurrency(budget.spentAmount)}</span>
                  </div>
                  <div className="total-amount">
                    <span className="label">of</span>
                    <span className="amount">{formatCurrency(budget.budgetAmount)}</span>
                  </div>
                </div>

                <div className="budget-progress">
                  <div className="progress-bar">
                    <div 
                      className={`progress-fill ${status}`}
                      style={{ width: `${Math.min(progress, 100)}%` }}
                    ></div>
                  </div>
                  <span className="progress-text">{progress.toFixed(0)}%</span>
                </div>

                <div className="budget-details">
                  <div className="detail-item">
                    <span>Remaining</span>
                    <strong className={(budget.budgetAmount - budget.spentAmount) < 0 ? 'negative' : 'positive'}>
                      {formatCurrency(Math.abs(budget.budgetAmount - budget.spentAmount))}
                      {(budget.budgetAmount - budget.spentAmount) < 0 && ' over'}
                    </strong>
                  </div>
                  <div className="detail-item">
                    <span>Period</span>
                    <strong>{budget.startDate} to {budget.endDate || 'Ongoing'}</strong>
                  </div>
                  {budget.alertSent && (
                    <div className="alert-badge">
                      ⚠️ Alert sent at {budget.alertThreshold}%
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {inactiveBudgets.length > 0 && (
        <div className="inactive-budgets">
          <h2>Inactive Budgets</h2>
          <div className="inactive-list">
            {inactiveBudgets.map(budget => (
              <div key={budget.id} className="inactive-budget-item">
                <span className="category">{budget.category}</span>
                <span className="period">{budget.period}</span>
                <span className="amount">{formatCurrency(budget.budgetAmount)}</span>
                <button 
                  onClick={() => handleEdit(budget)}
                  className="btn-activate"
                >
                  Activate
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Create/Edit Budget Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => { setShowModal(false); resetForm(); }}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingBudget ? 'Edit Budget' : 'Create Budget'}</h2>
              <button className="modal-close" onClick={() => { setShowModal(false); resetForm(); }}>
                ✕
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Budget Name *</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g., Monthly Groceries, Entertainment Budget"
                  required
                />
              </div>

              <div className="form-group">
                <label>Category *</label>
                <select
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  required
                >
                  <option value="">Select Category</option>
                  <option value="Food & Dining">Food & Dining</option>
                  <option value="Transportation">Transportation</option>
                  <option value="Shopping">Shopping</option>
                  <option value="Entertainment">Entertainment</option>
                  <option value="Bills & Utilities">Bills & Utilities</option>
                  <option value="Healthcare">Healthcare</option>
                  <option value="Education">Education</option>
                  <option value="Travel">Travel</option>
                  <option value="Personal Care">Personal Care</option>
                  <option value="Other">Other</option>
                </select>
              </div>

              <div className="form-group">
                <label>Budget Amount *</label>
                <input
                  type="number"
                  value={formData.budgetAmount}
                  onChange={(e) => setFormData({ ...formData, budgetAmount: parseFloat(e.target.value) })}
                  min="0"
                  step="0.01"
                  required
                />
              </div>

              <div className="form-group">
                <label>Period *</label>
                <select
                  value={formData.period}
                  onChange={(e) => setFormData({ ...formData, period: e.target.value as any })}
                  required
                >
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                  <option value="QUARTERLY">Quarterly</option>
                  <option value="YEARLY">Yearly</option>
                  <option value="CUSTOM">Custom</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Start Date *</label>
                  <input
                    type="date"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    required
                  />
                </div>

                {formData.period === 'CUSTOM' && (
                  <div className="form-group">
                    <label>End Date</label>
                    <input
                      type="date"
                      value={formData.endDate}
                      onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    />
                  </div>
                )}
              </div>

              <div className="form-group">
                <label>Alert Threshold (%) *</label>
                <input
                  type="number"
                  value={formData.alertThreshold}
                  onChange={(e) => setFormData({ ...formData, alertThreshold: parseInt(e.target.value) })}
                  min="0"
                  max="100"
                  required
                />
                <small>You'll be notified when spending reaches this percentage</small>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => { setShowModal(false); resetForm(); }}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  {editingBudget ? 'Update Budget' : 'Create Budget'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default BudgetsPage;
