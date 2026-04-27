package com.smartqueue.queue.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    // 🔥 Enable/Disable all notifications
    private Boolean notificationsEnabled = true;

    // 🔥 Email notifications
    private Boolean emailEnabled = true;

    // 🔥 In-app / popup notifications
    private Boolean pushEnabled = true;

    // 🔥 Future use (location-based queues etc.)
    private String preferredLocation;

    public UserSettings() {}

    public UserSettings(Long id, Long userId,
                        Boolean notificationsEnabled,
                        Boolean emailEnabled,
                        Boolean pushEnabled,
                        String preferredLocation) {
        this.id = id;
        this.userId = userId;
        this.notificationsEnabled = notificationsEnabled;
        this.emailEnabled = emailEnabled;
        this.pushEnabled = pushEnabled;
        this.preferredLocation = preferredLocation;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }
}