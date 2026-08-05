package com.spark.api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProfileGender {
	MEN("men"),
	WOMEN("women");

	private final String value;

	ProfileGender(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static ProfileGender fromValue(String value) {
		if (value == null) {
			return null;
		}
		for (ProfileGender gender : values()) {
			if (gender.value.equalsIgnoreCase(value) || gender.name().equalsIgnoreCase(value)) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Unknown gender: " + value);
	}
}
