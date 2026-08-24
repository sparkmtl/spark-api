package com.spark.api.dto;

import com.spark.api.entity.CheckIn;
import java.time.Instant;

public class CheckInStatusResponse {

	private boolean checkedIn;
	private Double anchorLatitude;
	private Double anchorLongitude;
	private Double latitude;
	private Double longitude;
	private Instant checkedInAt;

	public static CheckInStatusResponse notCheckedIn() {
		CheckInStatusResponse response = new CheckInStatusResponse();
		response.checkedIn = false;
		return response;
	}

	public static CheckInStatusResponse of(CheckIn checkIn) {
		CheckInStatusResponse response = new CheckInStatusResponse();
		response.checkedIn = checkIn.isActive();
		response.anchorLatitude = checkIn.getAnchorLatitude();
		response.anchorLongitude = checkIn.getAnchorLongitude();
		response.latitude = checkIn.getCurrentLatitude();
		response.longitude = checkIn.getCurrentLongitude();
		response.checkedInAt = checkIn.getCheckedInAt();
		return response;
	}

	public boolean isCheckedIn() {
		return checkedIn;
	}

	public Double getAnchorLatitude() {
		return anchorLatitude;
	}

	public Double getAnchorLongitude() {
		return anchorLongitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Instant getCheckedInAt() {
		return checkedInAt;
	}
}
