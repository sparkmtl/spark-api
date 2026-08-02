package com.spark.api.controller;

import com.spark.api.dto.CompleteRegisterRequest;
import com.spark.api.dto.ForgotPasswordRequest;
import com.spark.api.dto.LoginRequest;
import com.spark.api.dto.LoginResponse;
import com.spark.api.dto.MessageResponse;
import com.spark.api.dto.OtpSentResponse;
import com.spark.api.dto.ResetPasswordRequest;
import com.spark.api.dto.SendSignupOtpRequest;
import com.spark.api.dto.UserResponse;
import com.spark.api.dto.VerifyOtpRequest;
import com.spark.api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register/send-otp")
	public ResponseEntity<OtpSentResponse> sendSignupOtp(@Valid @RequestBody SendSignupOtpRequest request) {
		return ResponseEntity.ok(authService.sendSignupOtp(request));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<OtpSentResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
		return ResponseEntity.ok(authService.forgotPassword(request));
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<MessageResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
		return ResponseEntity.ok(authService.verifyOtp(request));
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody CompleteRegisterRequest request) {
		UserResponse response = authService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		return ResponseEntity.ok(authService.resetPassword(request));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		LoginResponse response = authService.login(request);
		return ResponseEntity.ok(response);
	}
}
