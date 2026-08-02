package com.spark.api.dto;

public class OtpSentResponse {

	private final String message;

	public OtpSentResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
