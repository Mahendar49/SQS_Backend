package com.smartqueue.queue.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.smartqueue.queue.entity.Notification;
import com.smartqueue.queue.entity.NotificationChannel;
import com.smartqueue.queue.entity.NotificationType;
import com.smartqueue.queue.entity.UserSettings;
import com.smartqueue.queue.repository.NotificationRepository;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final UserSettingsService userSettingsService; // 🔥 ADDED

    public NotificationService(NotificationRepository repository,
                               SimpMessagingTemplate messagingTemplate,
                               EmailService emailService,
                               UserSettingsService userSettingsService) { // 🔥 UPDATED

        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
        this.userSettingsService = userSettingsService;
    }

    // ================= PUBLIC METHODS =================

    public void notifyUser(Long userId, String message,
                           Integer position, Long counterId) {

        sendNotification(userId, message, position, counterId,
                NotificationType.GENERAL, true);
    }

    public void notifyIfImportant(Long userId, String message,
                                  Integer position, Long counterId,
                                  NotificationType type) {

        // 🔥 Smart filtering
        if (position != null && position > 3 && type != NotificationType.TURN_ALERT) {
            return;
        }

        sendNotification(userId, message, position, counterId, type, false);
    }

    // ================= CORE METHOD =================

    private void sendNotification(Long userId,
                                  String message,
                                  Integer position,
                                  Long counterId,
                                  NotificationType type,
                                  boolean forceEmail) {

        try {

            // 🔥 0. CHECK USER SETTINGS
            UserSettings settings = userSettingsService.getSettings(userId);

            if (settings == null || !Boolean.TRUE.equals(settings.getNotificationsEnabled())) {
                log.info("Notifications disabled for user {}", userId);
                return;
            }

            // 🔥 1. SAVE
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setMessage(message);
            notification.setPosition(position);
            notification.setCounterId(counterId);
            notification.setType(type);
            notification.setChannel(NotificationChannel.IN_APP);

            repository.save(notification);

            // 🔥 2. REAL-TIME (only if push enabled)
            if (Boolean.TRUE.equals(settings.getPushEnabled())) {
                messagingTemplate.convertAndSend(
                        "/topic/user/" + userId,
                        message
                );
            }

            // 🔥 3. EMAIL (controlled)
            if ((forceEmail || type == NotificationType.TURN_ALERT)
                    && Boolean.TRUE.equals(settings.getEmailEnabled())) {

                emailService.sendEmail(
                        buildUserEmail(userId),
                        "SmartQueue Notification",
                        message
                );
            }

            log.info("Notification sent to user {}", userId);

        } catch (Exception e) {
            log.error("Notification failed for user {} : {}", userId, e.getMessage());
        }
    }

    // ================= FETCH =================

    public List<Notification> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderBySentAtDesc(userId);
    }

    // ================= HELPER =================

    private String buildUserEmail(Long userId) {
        // 🔥 Placeholder (replace later with User Service)
        return "user" + userId + "@mail.com";
    }
}