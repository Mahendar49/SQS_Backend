package com.smartqueue.queue.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.queue.entity.UserSettings;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

    Optional<UserSettings> findByUserId(Long userId);
}