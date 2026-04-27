package com.smartqueue.business.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateBusinessRequest {
	@NotBlank(message = "Business name required")
	private String name;

	@NotBlank(message = "Location required")
	private String location;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public CreateBusinessRequest(String name, String location) {
		super();
		this.name = name;
		this.location = location;
	}

	public CreateBusinessRequest() {
	}
}