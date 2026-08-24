package com.spark.api.service;

import com.spark.api.dto.MapUserResponse;
import com.spark.api.dto.UpdateLocationRequest;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MapService {

	private final UserRepository userRepository;

	public MapService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public void updateMyLocation(UpdateLocationRequest request) {
		User current = SecurityUtils.requireCurrentUser();
		User user = userRepository.findById(current.getId())
				.orElseThrow(() -> new BadRequestException("User not found"));

		user.setLatitude(request.getLatitude());
		user.setLongitude(request.getLongitude());
		user.setLocationUpdatedAt(Instant.now());
		userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public List<MapUserResponse> getNearbyUsers() {
		User current = SecurityUtils.requireCurrentUser();
		User me = userRepository.findById(current.getId())
				.orElseThrow(() -> new BadRequestException("User not found"));

		return userRepository
				.findByIdNotAndLatitudeIsNotNullAndLongitudeIsNotNull(me.getId())
				.stream()
				.map(other -> MapUserResponse.of(other, ProfileSimilarity.score(me, other)))
				.toList();
	}
}
