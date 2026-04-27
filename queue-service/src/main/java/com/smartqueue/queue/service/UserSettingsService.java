package com.smartqueue.queue.service;

import org.springframework.stereotype.Service;

import com.smartqueue.queue.entity.UserSettings;
import com.smartqueue.queue.repository.UserSettingsRepository;

@Service
public class UserSettingsService {

    private final UserSettingsRepository repository;

    public UserSettingsService(UserSettingsRepository repository) {
        this.repository = repository;
    }

    // ================= GET SETTINGS =================

    public UserSettings getSettings(Long userId) {

        return repository.findByUserId(userId)
                .orElseGet(() -> {
                    UserSettings settings = new UserSettings();
                    settings.setUserId(userId);

                    // 🔥 DEFAULT VALUES
                    settings.setNotificationsEnabled(true);
                    settings.setEmailEnabled(true);
                    settings.setPushEnabled(true);

                    return repository.save(settings);
                });
    }

    // ================= UPDATE SETTINGS =================

    public UserSettings updateSettings(UserSettings request) {

        UserSettings existing = repository.findByUserId(request.getUserId())
                .orElseGet(() -> {
                    UserSettings s = new UserSettings();
                    s.setUserId(request.getUserId());
                    return s;
                });

        // 🔥 SAFE UPDATE (only update if not null)

        if (request.getNotificationsEnabled() != null) {
            existing.setNotificationsEnabled(request.getNotificationsEnabled());
        }

        if (request.getEmailEnabled() != null) {
            existing.setEmailEnabled(request.getEmailEnabled());
        }

        if (request.getPushEnabled() != null) {
            existing.setPushEnabled(request.getPushEnabled());
        }

        if (request.getPreferredLocation() != null) {
            existing.setPreferredLocation(request.getPreferredLocation());
        }

        return repository.save(existing);
    }
}