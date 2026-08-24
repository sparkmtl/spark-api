package com.spark.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class StartConversationRequest {

	@NotNull(message = "otherUserId is required")
	private UUID otherUserId;

	public UUID getOtherUserId() {
		return otherUserId;
	}

	public void setOtherUserId(UUID otherUserId) {
		this.otherUserId = otherUserId;
	}
}
