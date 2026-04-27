package com.smartqueue.queue.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "standby_users")
public class StandbyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long queueId;

    // 🔥 NEW: Link to QueueToken
    private Long tokenId;

    private Integer position;

    // 🔥 NEW: Track standby state
    @Enumerated(EnumType.STRING)
    private StandbyStatus status;

    private LocalDateTime joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = StandbyStatus.WAITING;
        }
    }

    public StandbyUser() {}

    public StandbyUser(Long id, Long userId, Long queueId, Long tokenId,
                       Integer position, StandbyStatus status, LocalDateTime joinedAt) {
        this.id = id;
        this.userId = userId;
        this.queueId = queueId;
        this.tokenId = tokenId;
        this.position = position;
        this.status = status;
        this.joinedAt = joinedAt;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQueueId() {
        return queueId;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public Integer getPosition() {
        return position;
    }

    public StandbyStatus getStatus() {
        return status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setStatus(StandbyStatus status) {
        this.status = status;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}