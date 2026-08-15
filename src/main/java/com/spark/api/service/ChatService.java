package com.spark.api.service;

import com.spark.api.dto.ChatMessageResponse;
import com.spark.api.dto.ConversationResponse;
import com.spark.api.dto.SendMessageRequest;
import com.spark.api.dto.StartConversationRequest;
import com.spark.api.entity.ChatMessage;
import com.spark.api.entity.Conversation;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.repository.ChatMessageRepository;
import com.spark.api.repository.ConversationRepository;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.SecurityUtils;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {

	private final ConversationRepository conversationRepository;
	private final ChatMessageRepository chatMessageRepository;
	private final UserRepository userRepository;

	public ChatService(
			ConversationRepository conversationRepository,
			ChatMessageRepository chatMessageRepository,
			UserRepository userRepository) {
		this.conversationRepository = conversationRepository;
		this.chatMessageRepository = chatMessageRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public ConversationResponse startConversation(StartConversationRequest request) {
		User current = SecurityUtils.requireCurrentUser();
		UUID otherUserId = request.getOtherUserId();

		if (otherUserId.equals(current.getId())) {
			throw new BadRequestException("Cannot start a conversation with yourself");
		}
		User other = userRepository.findById(otherUserId)
				.orElseThrow(() -> new BadRequestException("User not found"));

		UUID userOneId = current.getId().compareTo(otherUserId) <= 0 ? current.getId() : otherUserId;
		UUID userTwoId = current.getId().compareTo(otherUserId) <= 0 ? otherUserId : current.getId();

		Conversation conversation = conversationRepository
				.findByUserOneIdAndUserTwoId(userOneId, userTwoId)
				.orElseGet(() -> {
					Conversation created = new Conversation();
					created.setUserOneId(userOneId);
					created.setUserTwoId(userTwoId);
					return conversationRepository.save(created);
				});

		return ConversationResponse.of(conversation, other.getId(), displayName(other));
	}

	@Transactional(readOnly = true)
	public List<ChatMessageResponse> getMessages(UUID conversationId) {
		User current = SecurityUtils.requireCurrentUser();
		requireParticipant(conversationId, current.getId());

		return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
				.stream()
				.map(message -> ChatMessageResponse.of(message, current.getId()))
				.toList();
	}

	@Transactional
	public ChatMessageResponse sendMessage(UUID conversationId, SendMessageRequest request) {
		User current = SecurityUtils.requireCurrentUser();
		requireParticipant(conversationId, current.getId());

		ChatMessage message = new ChatMessage();
		message.setConversationId(conversationId);
		message.setSenderId(current.getId());
		message.setContent(request.getContent().trim());

		ChatMessage saved = chatMessageRepository.save(message);
		return ChatMessageResponse.of(saved, current.getId());
	}

	private Conversation requireParticipant(UUID conversationId, UUID userId) {
		Conversation conversation = conversationRepository.findById(conversationId)
				.orElseThrow(() -> new BadRequestException("Conversation not found"));
		if (!conversation.hasParticipant(userId)) {
			throw new BadRequestException("You are not part of this conversation");
		}
		return conversation;
	}

	private String displayName(User user) {
		String displayName = user.getDisplayName();
		return displayName != null && !displayName.isBlank() ? displayName : user.getUsername();
	}
}
