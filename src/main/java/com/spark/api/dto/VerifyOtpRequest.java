package com.spark.api.dto;

import com.spark.api.entity.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VerifyOtpRequest {

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	private String email;

	@NotBlank(message = "OTP is required")
	@Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit code")
	private String otp;

	@NotNull(message = "Purpose is required")
	private OtpPurpose purpose;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public OtpPurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(OtpPurpose purpose) {
		this.purpose = purpose;
	}
}
