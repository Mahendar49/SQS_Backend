package com.smartqueue.queue.dto;
public class CounterResponse {

    private Long id;
    private Long queueId; 
    private Integer avgServiceTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAvgServiceTime() {
		return avgServiceTime;
	}
	public void setAvgServiceTime(Integer avgServiceTime) {
		this.avgServiceTime = avgServiceTime;
	}
	public Long getQueueId() {
		return queueId;
	}
    // getters/setters
    
    
}