package com.spark.api.controller;

import com.spark.api.dto.ChatMessageResponse;
import com.spark.api.dto.ConversationResponse;
import com.spark.api.dto.SendMessageRequest;
import com.spark.api.dto.StartConversationRequest;
import com.spark.api.service.ChatService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/conversations")
	public ResponseEntity<ConversationResponse> startConversation(@Valid @RequestBody StartConversationRequest request) {
		return ResponseEntity.ok(chatService.startConversation(request));
	}

	@GetMapping("/conversations/{conversationId}/messages")
	public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable UUID conversationId) {
		return ResponseEntity.ok(chatService.getMessages(conversationId));
	}

	@PostMapping("/conversations/{conversationId}/messages")
	public ResponseEntity<ChatMessageResponse> sendMessage(
			@PathVariable UUID conversationId,
			@Valid @RequestBody SendMessageRequest request) {
		return ResponseEntity.ok(chatService.sendMessage(conversationId, request));
	}
}
