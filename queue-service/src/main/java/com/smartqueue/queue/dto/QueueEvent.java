package com.smartqueue.queue.dto;

public class QueueEvent {

    private String message;
    private Integer position;
    private Long counterId;

    public QueueEvent() {}

    public QueueEvent(String message, Integer position, Long counterId) {
        this.message = message;
        this.position = position;
        this.counterId = counterId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getCounterId() {
        return counterId;
    }

    public void setCounterId(Long counterId) {
        this.counterId = counterId;
    }
}