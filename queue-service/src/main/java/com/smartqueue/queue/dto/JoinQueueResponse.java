package com.smartqueue.queue.dto;

public class JoinQueueResponse {

	private boolean standby;
	private Long tokenId;
	private Integer position;
	private String message;

	public JoinQueueResponse(boolean standby, Long tokenId, Integer position, String message) {
		this.standby = standby;
		this.tokenId = tokenId;
		this.position = position;
		this.message = message;
	}

	public boolean isStandby() {
		return standby;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public Integer getPosition() {
		return position;
	}

	public String getMessage() {
		return message;
	}
}