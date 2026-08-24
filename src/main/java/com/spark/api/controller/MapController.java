package com.spark.api.controller;

import com.spark.api.dto.MapUserResponse;
import com.spark.api.dto.MessageResponse;
import com.spark.api.dto.UpdateLocationRequest;
import com.spark.api.service.MapService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/map")
public class MapController {

	private final MapService mapService;

	public MapController(MapService mapService) {
		this.mapService = mapService;
	}

	@PutMapping("/location")
	public ResponseEntity<MessageResponse> updateLocation(@Valid @RequestBody UpdateLocationRequest request) {
		mapService.updateMyLocation(request);
		return ResponseEntity.ok(new MessageResponse("Location updated"));
	}

	@GetMapping("/users")
	public ResponseEntity<List<MapUserResponse>> getNearbyUsers() {
		return ResponseEntity.ok(mapService.getNearbyUsers());
	}
}
