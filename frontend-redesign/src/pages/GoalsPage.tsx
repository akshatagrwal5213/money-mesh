import React, { useState, useEffect } from 'react';
import {
  getAllGoals,
  createGoal,
  updateGoal,
  deleteGoal,
  contributeToGoal,
  FinancialGoal,
  GoalRequest,
  GoalContributionRequest,
  calculateGoalProgress,
  getRemainingAmount,
  getDaysRemaining
} from '../services/module7Service';
import './GoalsPage.css';

const GoalsPage: React.FC = () => {
  const [goals, setGoals] = useState<FinancialGoal[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [showContributeModal, setShowContributeModal] = useState(false);
  const [selectedGoal, setSelectedGoal] = useState<FinancialGoal | null>(null);
  const [editingGoal, setEditingGoal] = useState<FinancialGoal | null>(null);
  const [formData, setFormData] = useState<GoalRequest>({
    name: '',
    type: 'EMERGENCY_FUND',
    targetAmount: 0,
    targetDate: '',
    description: '',
    monthlyContribution: 0,
    isAutomated: false
  });
  const [contributionData, setContributionData] = useState<GoalContributionRequest>({
    goalId: 0,
    amount: 0,
    accountId: 1
  });

  useEffect(() => {
    loadGoals();
  }, []);

  const loadGoals = async () => {
    try {
      setLoading(true);
      const data = await getAllGoals();
      setGoals(data);
    } catch (error) {
      console.error('Failed to load goals:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingGoal) {
        await updateGoal(editingGoal.id!, formData);
      } else {
        await createGoal(formData);
      }
      setShowModal(false);
      resetForm();
      loadGoals();
    } catch (error) {
      console.error('Failed to save goal:', error);
      alert('Failed to save goal');
    }
  };

  const handleContribute = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await contributeToGoal(contributionData);
      setShowContributeModal(false);
      setContributionData({ goalId: 0, amount: 0, accountId: 1 });
      loadGoals();
    } catch (error) {
      console.error('Failed to contribute to goal:', error);
      alert('Failed to contribute to goal');
    }
  };

  const handleEdit = (goal: FinancialGoal) => {
    setEditingGoal(goal);
    setFormData({
      name: goal.name,
      type: goal.type as any,
      targetAmount: goal.targetAmount,
      targetDate: goal.targetDate,
      description: goal.description || '',
      monthlyContribution: goal.monthlyContribution || 0,
      isAutomated: goal.isAutomated
    });
    setShowModal(true);
  };

  const handleDelete = async (goalId: number) => {
    if (!confirm('Are you sure you want to delete this goal?')) return;
    try {
      await deleteGoal(goalId);
      loadGoals();
    } catch (error) {
      console.error('Failed to delete goal:', error);
      alert('Failed to delete goal');
    }
  };

  const openContributeModal = (goal: FinancialGoal) => {
    setSelectedGoal(goal);
    setContributionData({ goalId: goal.id!, amount: 0, accountId: 1 });
    setShowContributeModal(true);
  };

  const resetForm = () => {
    setEditingGoal(null);
    setFormData({
      name: '',
      type: 'EMERGENCY_FUND',
      targetAmount: 0,
      targetDate: '',
      description: '',
      monthlyContribution: 0,
      isAutomated: false
    });
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED': return '✅';
      case 'ON_TRACK': return '🎯';
      case 'BEHIND': return '⚠️';
      case 'PAUSED': return '⏸️';
      case 'CANCELLED': return '❌';
      default: return '📊';
    }
  };

  const getStatusClass = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'completed';
      case 'ON_TRACK': return 'on-track';
      case 'BEHIND': return 'behind';
      case 'PAUSED': return 'paused';
      case 'CANCELLED': return 'cancelled';
      default: return 'in-progress';
    }
  };

  const activeGoals = goals.filter(g => g.status === 'IN_PROGRESS' || g.status === 'ON_TRACK' || g.status === 'BEHIND');
  const completedGoals = goals.filter(g => g.status === 'COMPLETED');
  const otherGoals = goals.filter(g => g.status === 'PAUSED' || g.status === 'CANCELLED');

  if (loading) {
    return (
      <div className="goals-loading">
        <div className="spinner"></div>
        <p>Loading goals...</p>
      </div>
    );
  }

  return (
    <div className="goals-page">
      <div className="goals-header">
        <h1>🎯 Financial Goals</h1>
        <button className="btn-primary" onClick={() => setShowModal(true)}>
          ➕ Create Goal
        </button>
      </div>

      {goals.length === 0 && (
        <div className="empty-state">
          <div className="empty-icon">🎯</div>
          <h2>No Goals Set</h2>
          <p>Create your first financial goal and start saving</p>
          <button className="btn-primary" onClick={() => setShowModal(true)}>
            Create Goal
          </button>
        </div>
      )}

      {activeGoals.length > 0 && (
        <div className="goals-section">
          <h2>Active Goals</h2>
          <div className="goals-grid">
            {activeGoals.map(goal => {
              const progress = calculateGoalProgress(goal);
              const remaining = getRemainingAmount(goal);
              const daysLeft = getDaysRemaining(goal.targetDate);
              
              return (
                <div key={goal.id} className={`goal-card ${getStatusClass(goal.status)}`}>
                  <div className="goal-header">
                    <div className="goal-title">
                      <span className="goal-icon">{getStatusIcon(goal.status)}</span>
                      <div>
                        <h3>{goal.name}</h3>
                        <span className="goal-type">{goal.type.replace('_', ' ')}</span>
                      </div>
                    </div>
                    <div className="goal-actions">
                      <button onClick={() => openContributeModal(goal)} title="Contribute">
                        💰
                      </button>
                      <button onClick={() => handleEdit(goal)} title="Edit">
                        ✏️
                      </button>
                      <button onClick={() => handleDelete(goal.id!)} title="Delete">
                        🗑️
                      </button>
                    </div>
                  </div>

                  {goal.description && (
                    <p className="goal-description">{goal.description}</p>
                  )}

                  <div className="goal-amounts">
                    <div className="current-amount">
                      <span className="label">Current</span>
                      <span className="amount">{formatCurrency(goal.currentAmount)}</span>
                    </div>
                    <div className="target-amount">
                      <span className="label">Target</span>
                      <span className="amount">{formatCurrency(goal.targetAmount)}</span>
                    </div>
                  </div>

                  <div className="goal-progress">
                    <div className="progress-bar">
                      <div 
                        className={`progress-fill ${getStatusClass(goal.status)}`}
                        style={{ width: `${Math.min(progress, 100)}%` }}
                      ></div>
                    </div>
                    <span className="progress-text">{progress.toFixed(1)}%</span>
                  </div>

                  <div className="goal-details">
                    <div className="detail-item">
                      <span>Remaining</span>
                      <strong>{formatCurrency(remaining)}</strong>
                    </div>
                    <div className="detail-item">
                      <span>Target Date</span>
                      <strong>{new Date(goal.targetDate).toLocaleDateString()}</strong>
                    </div>
                    <div className="detail-item">
                      <span>Days Left</span>
                      <strong className={daysLeft < 30 ? 'urgent' : ''}>
                        {daysLeft > 0 ? `${daysLeft} days` : 'Overdue'}
                      </strong>
                    </div>
                  </div>

                  {goal.isAutomated && (
                    <div className="automation-badge">
                      🤖 Auto: {formatCurrency(goal.monthlyContribution || 0)} / month
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </div>
      )}

      {completedGoals.length > 0 && (
        <div className="goals-section">
          <h2>Completed Goals</h2>
          <div className="completed-goals-list">
            {completedGoals.map(goal => (
              <div key={goal.id} className="completed-goal-item">
                <span className="icon">✅</span>
                <div className="goal-info">
                  <strong>{goal.name}</strong>
                  <span>{goal.type.replace('_', ' ')}</span>
                </div>
                <div className="goal-amount">
                  {formatCurrency(goal.targetAmount)}
                </div>
                <button onClick={() => handleEdit(goal)} className="btn-view">
                  View
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {otherGoals.length > 0 && (
        <div className="goals-section">
          <h2>Paused & Cancelled</h2>
          <div className="other-goals-list">
            {otherGoals.map(goal => (
              <div key={goal.id} className="other-goal-item">
                <span className="icon">{getStatusIcon(goal.status)}</span>
                <div className="goal-info">
                  <strong>{goal.name}</strong>
                  <span className="status">{goal.status}</span>
                </div>
                <div className="goal-amount">
                  {formatCurrency(goal.currentAmount)} / {formatCurrency(goal.targetAmount)}
                </div>
                <button onClick={() => handleEdit(goal)} className="btn-view">
                  Edit
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Create/Edit Goal Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={() => { setShowModal(false); resetForm(); }}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingGoal ? 'Edit Goal' : 'Create Goal'}</h2>
              <button className="modal-close" onClick={() => { setShowModal(false); resetForm(); }}>
                ✕
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Goal Name *</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="e.g., Emergency Fund, New Car, Vacation"
                  required
                />
              </div>

              <div className="form-group">
                <label>Goal Type *</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value as any })}
                  required
                >
                  <option value="SAVINGS">Savings</option>
                  <option value="EMERGENCY_FUND">Emergency Fund</option>
                  <option value="RETIREMENT">Retirement</option>
                  <option value="EDUCATION">Education</option>
                  <option value="HOME_PURCHASE">Home Purchase</option>
                  <option value="VEHICLE_PURCHASE">Vehicle Purchase</option>
                  <option value="VACATION">Vacation</option>
                  <option value="DEBT_PAYOFF">Debt Payoff</option>
                  <option value="INVESTMENT">Investment</option>
                  <option value="OTHER">Other</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label>Target Amount *</label>
                  <input
                    type="number"
                    value={formData.targetAmount}
                    onChange={(e) => setFormData({ ...formData, targetAmount: parseFloat(e.target.value) })}
                    min="0"
                    step="0.01"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>Target Date *</label>
                <input
                  type="date"
                  value={formData.targetDate}
                  onChange={(e) => setFormData({ ...formData, targetDate: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label>Description</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="Add notes about this goal..."
                  rows={3}
                ></textarea>
              </div>

              <div className="form-group checkbox">
                <label>
                  <input
                    type="checkbox"
                    checked={formData.isAutomated}
                    onChange={(e) => setFormData({ ...formData, isAutomated: e.target.checked })}
                  />
                  Enable automated contributions
                </label>
              </div>

              {formData.isAutomated && (
                <div className="automation-fields">
                  <div className="form-group">
                    <label>Monthly Contribution</label>
                    <input
                      type="number"
                      value={formData.monthlyContribution}
                      onChange={(e) => setFormData({ ...formData, monthlyContribution: parseFloat(e.target.value) })}
                      min="0"
                      step="0.01"
                    />
                  </div>
                </div>
              )}

              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => { setShowModal(false); resetForm(); }}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  {editingGoal ? 'Update Goal' : 'Create Goal'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Contribute Modal */}
      {showContributeModal && selectedGoal && (
        <div className="modal-overlay" onClick={() => setShowContributeModal(false)}>
          <div className="modal-content small" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Contribute to {selectedGoal.name}</h2>
              <button className="modal-close" onClick={() => setShowContributeModal(false)}>
                ✕
              </button>
            </div>

            <div className="contribute-info">
              <div className="info-row">
                <span>Current Amount:</span>
                <strong>{formatCurrency(selectedGoal.currentAmount)}</strong>
              </div>
              <div className="info-row">
                <span>Remaining:</span>
                <strong>{formatCurrency(getRemainingAmount(selectedGoal))}</strong>
              </div>
            </div>

            <form onSubmit={handleContribute}>
              <div className="form-group">
                <label>Contribution Amount *</label>
                <input
                  type="number"
                  value={contributionData.amount}
                  onChange={(e) => setContributionData({ ...contributionData, amount: parseFloat(e.target.value) })}
                  min="0"
                  step="0.01"
                  required
                  autoFocus
                />
              </div>

              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={() => setShowContributeModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Contribute {contributionData.amount > 0 && formatCurrency(contributionData.amount)}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default GoalsPage;
