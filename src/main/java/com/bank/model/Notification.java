package com.bank.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private AppUser user;
    
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private NotificationType type;
    
    @Column(nullable=false)
    private String title;
    
    @Column(length=1000)
    private String message;
    
    @Enumerated(value=EnumType.STRING)
    @Column(nullable=false)
    private NotificationPriority priority = NotificationPriority.MEDIUM;
    
    @Column(nullable=false)
    private Boolean isRead = false;
    
    private Double amount;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime readAt;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return this.user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public NotificationType getType() {
        return this.type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationPriority getPriority() {
        return this.priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public Boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Double getAmount() {
        return this.amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return this.readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
