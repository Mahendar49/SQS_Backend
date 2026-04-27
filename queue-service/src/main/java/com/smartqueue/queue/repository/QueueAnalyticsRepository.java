package com.smartqueue.queue.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartqueue.queue.entity.QueueAnalytics;

public interface QueueAnalyticsRepository extends JpaRepository<QueueAnalytics, Long> {

    Optional<QueueAnalytics> findByQueueIdAndDateAndHour(
            Long queueId, LocalDate date, Integer hour);
}