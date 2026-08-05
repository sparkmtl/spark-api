package com.spark.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LookingForIntent {
	DATE("date"),
	MAKE_FRIENDS("makeFriends"),
	NETWORKING("networking"),
	PROFESSIONAL_DEVELOPMENT("professionalDevelopment");

	private final String value;

	LookingForIntent(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static LookingForIntent fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (LookingForIntent intent : values()) {
			if (intent.value.equalsIgnoreCase(value) || intent.name().equalsIgnoreCase(value)) {
				return intent;
			}
		}
		throw new IllegalArgumentException("Unknown looking-for intent: " + value);
	}
}
