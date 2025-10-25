package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "overdue_trackings")
public class OverdueTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "emi_schedule_id")
    private EmiSchedule emiSchedule;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OverdueStatus overdueStatus;
    
    @Column(nullable = false)
    private LocalDate emiDueDate;
    
    @Column(nullable = false)
    private Double emiAmount;
    
    @Column(nullable = false)
    private Integer daysOverdue;
    
    @Column(nullable = false)
    private Double totalOverdueAmount;
    
    @Column(nullable = false)
    private Double penaltyAmount;
    
    private Double latePaymentCharges;
    
    private Boolean notificationSent = false;
    
    private LocalDate lastNotificationDate;
    
    private Integer notificationCount = 0;
    
    @Column(length = 20)
    private String collectionStatus;  // PENDING, IN_PROGRESS, RESOLVED
    
    @Column(length = 1000)
    private String collectionRemarks;
    
    private LocalDate resolutionDate;
    
    private Double amountRecovered;
    
    @Column(nullable = false)
    private Boolean isResolved = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Loan getLoan() {
        return loan;
    }

    public void setLoan(Loan loan) {
        this.loan = loan;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public EmiSchedule getEmiSchedule() {
        return emiSchedule;
    }

    public void setEmiSchedule(EmiSchedule emiSchedule) {
        this.emiSchedule = emiSchedule;
    }

    public OverdueStatus getOverdueStatus() {
        return overdueStatus;
    }

    public void setOverdueStatus(OverdueStatus overdueStatus) {
        this.overdueStatus = overdueStatus;
    }

    public LocalDate getEmiDueDate() {
        return emiDueDate;
    }

    public void setEmiDueDate(LocalDate emiDueDate) {
        this.emiDueDate = emiDueDate;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public Double getTotalOverdueAmount() {
        return totalOverdueAmount;
    }

    public void setTotalOverdueAmount(Double totalOverdueAmount) {
        this.totalOverdueAmount = totalOverdueAmount;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }

    public Double getLatePaymentCharges() {
        return latePaymentCharges;
    }

    public void setLatePaymentCharges(Double latePaymentCharges) {
        this.latePaymentCharges = latePaymentCharges;
    }

    public Boolean getNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public LocalDate getLastNotificationDate() {
        return lastNotificationDate;
    }

    public void setLastNotificationDate(LocalDate lastNotificationDate) {
        this.lastNotificationDate = lastNotificationDate;
    }

    public Integer getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(Integer notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(String collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public String getCollectionRemarks() {
        return collectionRemarks;
    }

    public void setCollectionRemarks(String collectionRemarks) {
        this.collectionRemarks = collectionRemarks;
    }

    public LocalDate getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDate resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public Double getAmountRecovered() {
        return amountRecovered;
    }

    public void setAmountRecovered(Double amountRecovered) {
        this.amountRecovered = amountRecovered;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
