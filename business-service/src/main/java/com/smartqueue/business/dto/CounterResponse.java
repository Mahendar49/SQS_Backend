package com.smartqueue.business.dto;

public class CounterResponse {

	private Long id;
	private String name;
	private Integer avgServiceTime;

	public CounterResponse(Long id,String name, Integer avgServiceTime) {
		this.id = id;
		this.name = name;
		this.avgServiceTime = avgServiceTime;
	}

	public Long getId() {
		return id;
	}
	

	public String getName() {
		return name;
	}

	public Integer getAvgServiceTime() {
		return avgServiceTime;
	}
}