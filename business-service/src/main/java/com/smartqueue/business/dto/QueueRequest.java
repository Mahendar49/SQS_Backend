package com.smartqueue.business.dto;

import com.smartqueue.business.entity.PriorityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QueueRequest {

	@NotBlank(message = "Service name is required")
	private String serviceName;

	@NotNull(message = "Priority type is required")
	private PriorityType priorityType;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public PriorityType getPriorityType() {
		return priorityType;
	}

	public void setPriorityType(PriorityType priorityType) {
		this.priorityType = priorityType;
	}

	// getters/setters

}