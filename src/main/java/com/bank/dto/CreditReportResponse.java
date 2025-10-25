package com.bank.dto;

import java.time.LocalDate;

import com.bank.model.CreditScoreCategory;

public class CreditReportResponse {
    
    private Long reportId;
    private Integer scoreAtTime;
    private CreditScoreCategory categoryAtTime;
    private LocalDate generatedDate;
    private String filePath;
    private String downloadUrl;
    private Long fileSize;  // in bytes
    
    // Summary data
    private String reportSummary;
    private Integer totalAccounts;
    private Double totalCreditLimit;
    private Double totalOutstanding;

    // Constructors
    public CreditReportResponse() {}

    public CreditReportResponse(Long reportId, Integer scoreAtTime, CreditScoreCategory categoryAtTime, LocalDate generatedDate) {
        this.reportId = reportId;
        this.scoreAtTime = scoreAtTime;
        this.categoryAtTime = categoryAtTime;
        this.generatedDate = generatedDate;
    }

    // Getters and Setters
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Integer getScoreAtTime() {
        return scoreAtTime;
    }

    public void setScoreAtTime(Integer scoreAtTime) {
        this.scoreAtTime = scoreAtTime;
    }

    public CreditScoreCategory getCategoryAtTime() {
        return categoryAtTime;
    }

    public void setCategoryAtTime(CreditScoreCategory categoryAtTime) {
        this.categoryAtTime = categoryAtTime;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getReportSummary() {
        return reportSummary;
    }

    public void setReportSummary(String reportSummary) {
        this.reportSummary = reportSummary;
    }

    public Integer getTotalAccounts() {
        return totalAccounts;
    }

    public void setTotalAccounts(Integer totalAccounts) {
        this.totalAccounts = totalAccounts;
    }

    public Double getTotalCreditLimit() {
        return totalCreditLimit;
    }

    public void setTotalCreditLimit(Double totalCreditLimit) {
        this.totalCreditLimit = totalCreditLimit;
    }

    public Double getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(Double totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }
}
