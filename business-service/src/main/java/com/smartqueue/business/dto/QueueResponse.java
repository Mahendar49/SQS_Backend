package com.smartqueue.business.dto;
public class QueueResponse {

    private Long id;
    private String serviceName;
    private String priorityType;

    public QueueResponse(Long id, String serviceName, String priorityType) {
        this.id = id;
        this.serviceName = serviceName;
        this.priorityType = priorityType;
    }

	public Long getId() {
		return id;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getPriorityType() {
		return priorityType;
	}

    // getters
}