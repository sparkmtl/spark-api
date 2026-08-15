package com.spark.api.dto;

import com.spark.api.entity.Conversation;
import java.util.UUID;

public class ConversationResponse {

	private UUID id;
	private UUID otherUserId;
	private String otherUserName;

	public static ConversationResponse of(Conversation conversation, UUID otherUserId, String otherUserName) {
		ConversationResponse response = new ConversationResponse();
		response.id = conversation.getId();
		response.otherUserId = otherUserId;
		response.otherUserName = otherUserName;
		return response;
	}

	public UUID getId() {
		return id;
	}

	public UUID getOtherUserId() {
		return otherUserId;
	}

	public String getOtherUserName() {
		return otherUserName;
	}
}
