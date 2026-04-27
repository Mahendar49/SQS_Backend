package com.smartqueue.business.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CounterRequest {
	@NotBlank(message = "Counter name is required")
	private String name;

	@NotNull(message = "Service time required")
	@Min(value = 1, message = "Service time must be greater than 0")
	private Integer avgServiceTime;

	public CounterRequest() {
		// TODO Auto-generated constructor stub
	}

	public CounterRequest(String name, Integer avgServiceTime) {
		super();
		this.name = name;
		this.avgServiceTime = avgServiceTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAvgServiceTime() {
		return avgServiceTime;
	}

	public void setAvgServiceTime(Integer avgServiceTime) {
		this.avgServiceTime = avgServiceTime;
	}

}