package com.bank.service;

import com.bank.model.AppUser;
import com.bank.model.Notification;
import com.bank.model.NotificationPriority;
import com.bank.model.NotificationType;
import com.bank.repository.AppUserRepository;
import com.bank.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Transactional
    public Notification createNotification(Long userId, String message, NotificationType type, NotificationPriority priority) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(type.name());
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        notification.setType(type);
        notification.setPriority(priority);
        return notificationRepository.save(notification);
    }

    public List<Notification> getUnacknowledgedNotifications(Long userId) {
        return notificationRepository.findByUser_IdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public boolean acknowledgeNotification(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .map(notification -> {
                    notification.setIsRead(true);
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    return true;
                })
                .orElse(false);
    }
    
    // Helper method to send budget alert
    public void sendBudgetAlert(String username, String budgetName, double percentageSpent) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("Budget Alert");
        notification.setMessage(String.format("You've spent %.0f%% of your %s budget", percentageSpent, budgetName));
        notification.setType(NotificationType.BUDGET_ALERT);
        notification.setPriority(NotificationPriority.HIGH);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }
}
