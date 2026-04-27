package com.smartqueue.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String message;

    private Integer position;

    private Long counterId;

    // 🔥 NEW: Notification type
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // 🔥 NEW: Delivery channel
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;

    // 🔥 NEW: Read/Unread tracking
    private Boolean isRead = false;

    private LocalDateTime sentAt;

    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();

        if (this.isRead == null) {
            this.isRead = false;
        }

        if (this.channel == null) {
            this.channel = NotificationChannel.IN_APP;
        }
    }

    public Notification() {}

    public Notification(Long id, Long userId, String message,
                        Integer position, Long counterId,
                        NotificationType type,
                        NotificationChannel channel,
                        Boolean isRead,
                        LocalDateTime sentAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.position = position;
        this.counterId = counterId;
        this.type = type;
        this.channel = channel;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Integer getPosition() {
        return position;
    }

    public Long getCounterId() {
        return counterId;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}