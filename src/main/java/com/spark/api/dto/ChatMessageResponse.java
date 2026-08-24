package com.spark.api.dto;

import com.spark.api.entity.ChatMessage;
import java.time.Instant;
import java.util.UUID;

public class ChatMessageResponse {

	private UUID id;
	private UUID conversationId;
	private String content;
	private Instant createdAt;
	/** Whether the requesting user sent this message (drives bubble alignment). */
	private boolean mine;

	public static ChatMessageResponse of(ChatMessage message, UUID viewerId) {
		ChatMessageResponse response = new ChatMessageResponse();
		response.id = message.getId();
		response.conversationId = message.getConversationId();
		response.content = message.getContent();
		response.createdAt = message.getCreatedAt();
		response.mine = message.getSenderId().equals(viewerId);
		return response;
	}

	public UUID getId() {
		return id;
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public String getContent() {
		return content;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public boolean isMine() {
		return mine;
	}
}
