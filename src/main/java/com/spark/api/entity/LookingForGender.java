package com.spark.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LookingForGender {
	MEN("men"),
	WOMEN("women"),
	ANY("any");

	private final String value;

	LookingForGender(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static LookingForGender fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (LookingForGender gender : values()) {
			if (gender.value.equalsIgnoreCase(value) || gender.name().equalsIgnoreCase(value)) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Unknown looking-for gender: " + value);
	}
}
