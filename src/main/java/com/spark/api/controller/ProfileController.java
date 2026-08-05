package com.spark.api.controller;

import com.spark.api.dto.ProfileResponse;
import com.spark.api.dto.UpdateProfileRequest;
import com.spark.api.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

	private final ProfileService profileService;

	public ProfileController(ProfileService profileService) {
		this.profileService = profileService;
	}

	@GetMapping
	public ResponseEntity<ProfileResponse> getMyProfile() {
		return ResponseEntity.ok(profileService.getMyProfile());
	}

	@PutMapping
	public ResponseEntity<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request) {
		return ResponseEntity.ok(profileService.updateMyProfile(request));
	}
}
