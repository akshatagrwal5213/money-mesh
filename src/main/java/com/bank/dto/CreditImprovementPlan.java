package com.bank.dto;

import com.bank.model.RecommendationPriority;
import java.util.List;

public class CreditImprovementPlan {
    
    private Integer currentScore;
    private Integer targetScore;
    private Integer potentialIncrease;
    private String estimatedTimeframe;  // e.g., "3-6 months"
    
    private List<Recommendation> recommendations;
    
    public static class Recommendation {
        private String title;
        private String description;
        private RecommendationPriority priority;
        private Integer estimatedImpact;  // Points
        private String actionSteps;
        private String timeframe;
        
        public Recommendation() {}
        
        public Recommendation(String title, String description, RecommendationPriority priority, Integer estimatedImpact) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.estimatedImpact = estimatedImpact;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public RecommendationPriority getPriority() {
            return priority;
        }

        public void setPriority(RecommendationPriority priority) {
            this.priority = priority;
        }

        public Integer getEstimatedImpact() {
            return estimatedImpact;
        }

        public void setEstimatedImpact(Integer estimatedImpact) {
            this.estimatedImpact = estimatedImpact;
        }

        public String getActionSteps() {
            return actionSteps;
        }

        public void setActionSteps(String actionSteps) {
            this.actionSteps = actionSteps;
        }

        public String getTimeframe() {
            return timeframe;
        }

        public void setTimeframe(String timeframe) {
            this.timeframe = timeframe;
        }
    }

    // Constructors
    public CreditImprovementPlan() {}

    public CreditImprovementPlan(Integer currentScore, Integer targetScore) {
        this.currentScore = currentScore;
        this.targetScore = targetScore;
        this.potentialIncrease = targetScore - currentScore;
    }

    // Getters and Setters
    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getTargetScore() {
        return targetScore;
    }

    public void setTargetScore(Integer targetScore) {
        this.targetScore = targetScore;
    }

    public Integer getPotentialIncrease() {
        return potentialIncrease;
    }

    public void setPotentialIncrease(Integer potentialIncrease) {
        this.potentialIncrease = potentialIncrease;
    }

    public String getEstimatedTimeframe() {
        return estimatedTimeframe;
    }

    public void setEstimatedTimeframe(String estimatedTimeframe) {
        this.estimatedTimeframe = estimatedTimeframe;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
