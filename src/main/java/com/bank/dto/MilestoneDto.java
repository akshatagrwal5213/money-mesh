package com.bank.dto;

import com.bank.model.MilestoneType;
import java.time.LocalDate;

public class MilestoneDto {
    
    private Long id;
    private MilestoneType milestoneType;
    private LocalDate achievedDate;
    private Integer bonusPoints;
    private Double milestoneValue;
    private Boolean isCredited;
    private LocalDate creditedDate;
    private String description;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MilestoneType getMilestoneType() {
        return milestoneType;
    }

    public void setMilestoneType(MilestoneType milestoneType) {
        this.milestoneType = milestoneType;
    }

    public LocalDate getAchievedDate() {
        return achievedDate;
    }

    public void setAchievedDate(LocalDate achievedDate) {
        this.achievedDate = achievedDate;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public Double getMilestoneValue() {
        return milestoneValue;
    }

    public void setMilestoneValue(Double milestoneValue) {
        this.milestoneValue = milestoneValue;
    }

    public Boolean getIsCredited() {
        return isCredited;
    }

    public void setIsCredited(Boolean isCredited) {
        this.isCredited = isCredited;
    }

    public LocalDate getCreditedDate() {
        return creditedDate;
    }

    public void setCreditedDate(LocalDate creditedDate) {
        this.creditedDate = creditedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
