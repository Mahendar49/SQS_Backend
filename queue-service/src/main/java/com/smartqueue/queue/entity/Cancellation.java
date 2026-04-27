package com.smartqueue.queue.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cancellations")
public class Cancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tokenId;

    // 🔥 NEW: Who cancelled
    private Long userId;

    private String reason;

    // 🔥 NEW: Type of cancellation
    @Enumerated(EnumType.STRING)
    private CancellationType type;

    private LocalDateTime cancelTime;

    @PrePersist
    public void prePersist() {
        this.cancelTime = LocalDateTime.now();

        if (this.type == null) {
            this.type = CancellationType.USER;
        }
    }

    public Cancellation() {}

    public Cancellation(Long id, Long tokenId, Long userId,
                        String reason, CancellationType type,
                        LocalDateTime cancelTime) {
        this.id = id;
        this.tokenId = tokenId;
        this.userId = userId;
        this.reason = reason;
        this.type = type;
        this.cancelTime = cancelTime;
    }

    // ===== GETTERS & SETTERS =====

    public Long getId() {
        return id;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    public CancellationType getType() {
        return type;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setType(CancellationType type) {
        this.type = type;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }
}