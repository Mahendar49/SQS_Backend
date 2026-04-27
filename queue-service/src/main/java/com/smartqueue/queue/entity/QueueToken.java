package com.smartqueue.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
	    name = "queue_tokens",
	    uniqueConstraints = @UniqueConstraint(
	        columnNames = {"userId", "queueId", "status"}
	    )
	)
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long queueId;
    private Long counterId;

    private Integer position;

    // 🔥 FIXED: Use ENUM instead of String
    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    // 🔥 FIXED: Use ENUM instead of String
    @Enumerated(EnumType.STRING)
    private PriorityType priorityType;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();

        // 🔥 Default values
        if (this.status == null) {
            this.status = TokenStatus.WAITING;
        }
        if (this.priorityType == null) {
            this.priorityType = PriorityType.NORMAL;
        }
    }

    public QueueToken() {}

    public QueueToken(Long id, Long userId, Long queueId, Long counterId,
                      Integer position, TokenStatus status,
                      PriorityType priorityType, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.queueId = queueId;
        this.counterId = counterId;
        this.position = position;
        this.status = status;
        this.priorityType = priorityType;
        this.createdAt = createdAt;
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

    public Long getCounterId() {
        return counterId;
    }

    public Integer getPosition() {
        return position;
    }

    public TokenStatus getStatus() {
        return status;
    }

    public PriorityType getPriorityType() {
        return priorityType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setStatus(TokenStatus status) {
        this.status = status;
    }

    public void setPriorityType(PriorityType priorityType) {
        this.priorityType = priorityType;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}