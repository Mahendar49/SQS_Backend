package com.smartqueue.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "queue_analytics",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"queueId", "date", "hour"})
    }
)
public class QueueAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long queueId;

    private LocalDate date;

    private Integer hour;

    // 🔥 Total users joined
    private Integer totalUsers;

    // 🔥 Users successfully served
    private Integer servedUsers;

    // 🔥 Avg wait time (in seconds)
    private Long avgWaitTime;

    // 🔥 Total cancellations
    private Integer cancellations;

    @PrePersist
    public void prePersist() {
        if (totalUsers == null) totalUsers = 0;
        if (servedUsers == null) servedUsers = 0;
        if (avgWaitTime == null) avgWaitTime = 0L;
        if (cancellations == null) cancellations = 0;
    }

    public QueueAnalytics() {}

    public QueueAnalytics(Long id, Long queueId, LocalDate date, Integer hour,
                          Integer totalUsers, Integer servedUsers,
                          Long avgWaitTime, Integer cancellations) {
        this.id = id;
        this.queueId = queueId;
        this.date = date;
        this.hour = hour;
        this.totalUsers = totalUsers;
        this.servedUsers = servedUsers;
        this.avgWaitTime = avgWaitTime;
        this.cancellations = cancellations;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Long getQueueId() {
        return queueId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getHour() {
        return hour;
    }

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public Integer getServedUsers() {
        return servedUsers;
    }

    public Long getAvgWaitTime() {
        return avgWaitTime;
    }

    public Integer getCancellations() {
        return cancellations;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

    public void setServedUsers(Integer servedUsers) {
        this.servedUsers = servedUsers;
    }

    public void setAvgWaitTime(Long avgWaitTime) {
        this.avgWaitTime = avgWaitTime;
    }

    public void setCancellations(Integer cancellations) {
        this.cancellations = cancellations;
    }
}