package com.smartqueue.queue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CancelRequest {

	@NotNull
	private Long tokenId;

	@NotBlank
	private String reason;

	public CancelRequest() {
	} // 🔥 REQUIRED

	public Long getTokenId() {
		return tokenId;
	}

	public void setTokenId(Long tokenId) {
		this.tokenId = tokenId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
}