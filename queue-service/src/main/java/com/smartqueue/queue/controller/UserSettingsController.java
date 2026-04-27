package com.smartqueue.queue.controller;

import org.springframework.web.bind.annotation.*;

import com.smartqueue.queue.dto.ApiResponse;
import com.smartqueue.queue.entity.UserSettings;
import com.smartqueue.queue.service.UserSettingsService;

@RestController
@RequestMapping("/api/v1/settings")
public class UserSettingsController {

    private final UserSettingsService service;

    public UserSettingsController(UserSettingsService service) {
        this.service = service;
    }

    // ================= GET =================

    @GetMapping
    public ApiResponse<UserSettings> get(
            @RequestHeader("X-User-Id") Long userId) {

        return new ApiResponse<>(
                true,
                "Settings fetched",
                service.getSettings(userId)
        );
    }

    // ================= UPDATE =================

    @PutMapping
    public ApiResponse<UserSettings> update(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody UserSettings request) {

        request.setUserId(userId); // 🔥 enforce security

        return new ApiResponse<>(
                true,
                "Settings updated",
                service.updateSettings(request)
        );
    }
}