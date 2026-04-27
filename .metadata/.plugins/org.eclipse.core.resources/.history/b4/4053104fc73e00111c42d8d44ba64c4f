package com.smartqueue.queue.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.smartqueue.queue.entity.QueueAnalytics;
import com.smartqueue.queue.repository.QueueAnalyticsRepository;

@Service
public class AnalyticsService {

    private final QueueAnalyticsRepository repository;

    public AnalyticsService(QueueAnalyticsRepository repository) {
        this.repository = repository;
    }

    // ================= COMMON METHOD =================

    private QueueAnalytics getOrCreate(Long queueId) {

        LocalDate today = LocalDate.now();
        int hour = LocalDateTime.now().getHour();

        return repository
                .findByQueueIdAndDateAndHour(queueId, today, hour)
                .orElseGet(() -> {
                    QueueAnalytics qa = new QueueAnalytics();
                    qa.setQueueId(queueId);
                    qa.setDate(today);
                    qa.setHour(hour);
                    qa.setTotalUsers(0);
                    qa.setServedUsers(0);
                    qa.setCancellations(0);
                    qa.setAvgWaitTime(0L);
                    return qa;
                });
    }

    // ================= TRACK JOIN =================

    public void trackJoin(Long queueId) {

        QueueAnalytics analytics = getOrCreate(queueId);

        analytics.setTotalUsers(analytics.getTotalUsers() + 1);

        repository.save(analytics);
    }

    // ================= TRACK CANCEL =================

    public void trackCancel(Long queueId) {

        QueueAnalytics analytics = getOrCreate(queueId);

        analytics.setCancellations(analytics.getCancellations() + 1);

        repository.save(analytics);
    }

    // ================= TRACK SERVED =================

    public void trackServed(Long queueId) {

        QueueAnalytics analytics = getOrCreate(queueId);

        analytics.setServedUsers(analytics.getServedUsers() + 1);

        repository.save(analytics);
    }

    // ================= TRACK WAIT TIME =================

    public void trackWaitTime(Long queueId, long waitTime) {

        QueueAnalytics analytics = getOrCreate(queueId);

        int served = analytics.getServedUsers();

        if (served == 0) {
            analytics.setAvgWaitTime(waitTime);
        } else {
            long currentAvg = analytics.getAvgWaitTime();
            long newAvg = ((currentAvg * (served - 1)) + waitTime) / served;
            analytics.setAvgWaitTime(newAvg);
        }

        repository.save(analytics);
    }

    // ================= GET ANALYTICS =================

    public QueueAnalytics getAnalytics(Long queueId) {

        LocalDate today = LocalDate.now();
        int hour = LocalDateTime.now().getHour();

        return repository
                .findByQueueIdAndDateAndHour(queueId, today, hour)
                .orElseThrow(() -> new RuntimeException("No analytics found"));
    }
}