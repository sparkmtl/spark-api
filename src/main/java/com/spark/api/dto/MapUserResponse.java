package com.spark.api.dto;

import com.spark.api.entity.CheckIn;
import com.spark.api.entity.User;
import java.util.UUID;

/**
 * Public, map-safe view of another user: just enough to place them on the
 * map and color their marker by how similar their profile is to the viewer.
 */
public class MapUserResponse {

	private UUID id;
	private String name;
	private Integer age;
	private double latitude;
	private double longitude;
	/** 0.0 (least similar) .. 1.0 (most similar) to the requesting user. */
	private double similarity;

	public static MapUserResponse of(User user, double similarity) {
		MapUserResponse response = new MapUserResponse();
		response.id = user.getId();
		String displayName = user.getDisplayName();
		response.name = displayName != null && !displayName.isBlank() ? displayName : user.getUsername();
		response.age = user.getAge();
		response.latitude = user.getLatitude();
		response.longitude = user.getLongitude();
		response.similarity = similarity;
		return response;
	}

	public static MapUserResponse ofCheckIn(User user, CheckIn checkIn, double similarity) {
		MapUserResponse response = new MapUserResponse();
		response.id = user.getId();
		String displayName = user.getDisplayName();
		response.name = displayName != null && !displayName.isBlank() ? displayName : user.getUsername();
		response.age = user.getAge();
		response.latitude = checkIn.getCurrentLatitude();
		response.longitude = checkIn.getCurrentLongitude();
		response.similarity = similarity;
		return response;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getSimilarity() {
		return similarity;
	}
}
