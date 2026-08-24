package com.spark.api.dto;

public class CheckInLocationUpdateResponse {

	private boolean checkedIn;
	private boolean autoCheckedOut;

	public CheckInLocationUpdateResponse(boolean checkedIn, boolean autoCheckedOut) {
		this.checkedIn = checkedIn;
		this.autoCheckedOut = autoCheckedOut;
	}

	public boolean isCheckedIn() {
		return checkedIn;
	}

	public boolean isAutoCheckedOut() {
		return autoCheckedOut;
	}
}
