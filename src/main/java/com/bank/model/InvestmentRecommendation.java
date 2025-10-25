package com.bank.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "investment_recommendations")
public class InvestmentRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType recommendationType;
    
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private AssetClass suggestedAssetClass;
    
    private Double suggestedAmount;
    private String productName; // Specific fund/product name
    
    private Integer priority; // 1 (highest) to 5 (lowest)
    private Double potentialImpact; // Impact on financial health score (0-100)
    
    private LocalDate generatedDate;
    private LocalDate expiryDate;
    private Boolean isActive;
    private Boolean isImplemented;
    
    @Column(length = 1000)
    private String reasoning; // Why this recommendation
    
    @Column(length = 500)
    private String actionItems; // What user should do

    // Constructors
    public InvestmentRecommendation() {
        this.generatedDate = LocalDate.now();
        this.isActive = true;
        this.isImplemented = false;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public RecommendationType getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(RecommendationType recommendationType) {
        this.recommendationType = recommendationType;
    }

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

    public AssetClass getSuggestedAssetClass() {
        return suggestedAssetClass;
    }

    public void setSuggestedAssetClass(AssetClass suggestedAssetClass) {
        this.suggestedAssetClass = suggestedAssetClass;
    }

    public Double getSuggestedAmount() {
        return suggestedAmount;
    }

    public void setSuggestedAmount(Double suggestedAmount) {
        this.suggestedAmount = suggestedAmount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Double getPotentialImpact() {
        return potentialImpact;
    }

    public void setPotentialImpact(Double potentialImpact) {
        this.potentialImpact = potentialImpact;
    }

    public LocalDate getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsImplemented() {
        return isImplemented;
    }

    public void setIsImplemented(Boolean isImplemented) {
        this.isImplemented = isImplemented;
    }

    public String getReasoning() {
        return reasoning;
    }

    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }

    public String getActionItems() {
        return actionItems;
    }

    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }
}
