package com.smartqueue.queue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.queue.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}