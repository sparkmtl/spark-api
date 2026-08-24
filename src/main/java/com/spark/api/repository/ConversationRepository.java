package com.spark.api.repository;

import com.spark.api.entity.Conversation;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

	Optional<Conversation> findByUserOneIdAndUserTwoId(UUID userOneId, UUID userTwoId);
}
