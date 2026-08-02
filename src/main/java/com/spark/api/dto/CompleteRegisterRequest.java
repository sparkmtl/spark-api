package com.spark.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Finalizes signup after OTP verification. Username/password come from the signup challenge. */
public class CompleteRegisterRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
