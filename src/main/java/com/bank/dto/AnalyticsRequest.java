package com.bank.dto;

import java.time.LocalDate;

public class AnalyticsRequest {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private String groupBy; // DAY, WEEK, MONTH, YEAR
    
    // Constructors
    public AnalyticsRequest() {}
    
    public AnalyticsRequest(LocalDate startDate, LocalDate endDate, String groupBy) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupBy = groupBy;
    }
    
    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getGroupBy() {
        return groupBy;
    }
    
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
