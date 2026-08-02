package com.spark.api.dto;

import com.spark.api.entity.Role;
import com.spark.api.entity.User;
import java.time.Instant;
import java.util.UUID;

public class UserResponse {

	private UUID id;
	private String username;
	private String email;
	private Role role;
	private Instant createdAt;

	public static UserResponse fromEntity(User user) {
		UserResponse response = new UserResponse();
		response.id = user.getId();
		response.username = user.getUsername();
		response.email = user.getEmail();
		response.role = user.getRole();
		response.createdAt = user.getCreatedAt();
		return response;
	}

	public UUID getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Role getRole() {
		return role;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
