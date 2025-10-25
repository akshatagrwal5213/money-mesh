package com.bank.dto;

import java.time.LocalDate;

public class EmiScheduleDto {
    
    private Long id;
    private Integer emiNumber;
    private LocalDate dueDate;
    private Double emiAmount;
    private Double principalComponent;
    private Double interestComponent;
    private Double outstandingPrincipal;
    private Boolean isPaid;
    private LocalDate paidDate;
    private Double paidAmount;
    private Integer daysOverdue;
    private Double penaltyAmount;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getEmiNumber() {
        return emiNumber;
    }

    public void setEmiNumber(Integer emiNumber) {
        this.emiNumber = emiNumber;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Double getEmiAmount() {
        return emiAmount;
    }

    public void setEmiAmount(Double emiAmount) {
        this.emiAmount = emiAmount;
    }

    public Double getPrincipalComponent() {
        return principalComponent;
    }

    public void setPrincipalComponent(Double principalComponent) {
        this.principalComponent = principalComponent;
    }

    public Double getInterestComponent() {
        return interestComponent;
    }

    public void setInterestComponent(Double interestComponent) {
        this.interestComponent = interestComponent;
    }

    public Double getOutstandingPrincipal() {
        return outstandingPrincipal;
    }

    public void setOutstandingPrincipal(Double outstandingPrincipal) {
        this.outstandingPrincipal = outstandingPrincipal;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public Double getPenaltyAmount() {
        return penaltyAmount;
    }

    public void setPenaltyAmount(Double penaltyAmount) {
        this.penaltyAmount = penaltyAmount;
    }
}
