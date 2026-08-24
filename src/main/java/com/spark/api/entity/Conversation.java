package com.spark.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

/**
 * A 1:1 conversation between two users. The pair is stored with the lower
 * UUID first so that (a, b) and (b, a) always resolve to the same row.
 */
@Entity
@Table(
		name = "conversations",
		uniqueConstraints = @UniqueConstraint(columnNames = {"user_one_id", "user_two_id"}))
public class Conversation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_one_id", nullable = false)
	private UUID userOneId;

	@Column(name = "user_two_id", nullable = false)
	private UUID userTwoId;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getUserOneId() {
		return userOneId;
	}

	public void setUserOneId(UUID userOneId) {
		this.userOneId = userOneId;
	}

	public UUID getUserTwoId() {
		return userTwoId;
	}

	public void setUserTwoId(UUID userTwoId) {
		this.userTwoId = userTwoId;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public boolean hasParticipant(UUID userId) {
		return userOneId.equals(userId) || userTwoId.equals(userId);
	}

	public UUID otherParticipant(UUID userId) {
		return userOneId.equals(userId) ? userTwoId : userOneId;
	}
}
