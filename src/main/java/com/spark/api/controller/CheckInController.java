package com.spark.api.controller;

import com.spark.api.dto.CheckInLocationUpdateResponse;
import com.spark.api.dto.CheckInStatusResponse;
import com.spark.api.dto.MapUserResponse;
import com.spark.api.dto.UpdateLocationRequest;
import com.spark.api.service.CheckInService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/check-in")
public class CheckInController {

	private final CheckInService checkInService;

	public CheckInController(CheckInService checkInService) {
		this.checkInService = checkInService;
	}

	@PostMapping
	public ResponseEntity<CheckInStatusResponse> checkIn(@Valid @RequestBody UpdateLocationRequest request) {
		return ResponseEntity.ok(checkInService.checkIn(request));
	}

	@GetMapping("/status")
	public ResponseEntity<CheckInStatusResponse> getStatus() {
		return ResponseEntity.ok(checkInService.getStatus());
	}

	@PutMapping("/location")
	public ResponseEntity<CheckInLocationUpdateResponse> updateLocation(
			@Valid @RequestBody UpdateLocationRequest request) {
		return ResponseEntity.ok(checkInService.updateLocation(request));
	}

	@DeleteMapping
	public ResponseEntity<CheckInStatusResponse> checkOut() {
		return ResponseEntity.ok(checkInService.checkOut());
	}

	@GetMapping("/users")
	public ResponseEntity<List<MapUserResponse>> getCheckedInUsers() {
		return ResponseEntity.ok(checkInService.getCheckedInUsers());
	}
}
