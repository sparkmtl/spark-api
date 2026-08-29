package com.spark.api.service;

import com.spark.api.dto.CheckInLocationUpdateResponse;
import com.spark.api.dto.CheckInStatusResponse;
import com.spark.api.dto.MapUserResponse;
import com.spark.api.dto.UpdateLocationRequest;
import com.spark.api.entity.CheckIn;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.exception.DuplicateResourceException;
import com.spark.api.repository.CheckInRepository;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.SecurityUtils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInService {

	private final UserRepository userRepository;
	private final CheckInRepository checkInRepository;

	public CheckInService(UserRepository userRepository, CheckInRepository checkInRepository) {
		this.userRepository = userRepository;
		this.checkInRepository = checkInRepository;
	}

	@Transactional
	public CheckInStatusResponse checkIn(UpdateLocationRequest request) {
		User user = loadCurrentUser();
		if (findActiveCheckIn(user.getId()).isPresent()) {
			throw new DuplicateResourceException("Already checked in");
		}

		Instant now = Instant.now();
		CheckIn checkIn = new CheckIn();
		checkIn.setUserId(user.getId());
		checkIn.activate();
		checkIn.setAnchorLatitude(request.getLatitude());
		checkIn.setAnchorLongitude(request.getLongitude());
		checkIn.setCurrentLatitude(request.getLatitude());
		checkIn.setCurrentLongitude(request.getLongitude());
		checkIn.setCheckedInAt(now);
		checkIn.setUpdatedAt(now);

		try {
			checkInRepository.saveAndFlush(checkIn);
		} catch (DataIntegrityViolationException ex) {
			throw new DuplicateResourceException("Already checked in");
		}

		syncUserCheckInFlag(user, checkIn);
		userRepository.save(user);
		return CheckInStatusResponse.of(checkIn);
	}

	@Transactional
	public CheckInStatusResponse getStatus() {
		User user = loadCurrentUser();
		Optional<CheckIn> active = findActiveCheckIn(user.getId());
		if (active.isPresent()) {
			return CheckInStatusResponse.of(active.get());
		}
		if (user.isCheckedIn() && user.getCheckInLatitude() != null && user.getCheckInLongitude() != null) {
			return migrateLegacyUserCheckIn(user);
		}
		return CheckInStatusResponse.notCheckedIn();
	}

	@Transactional
	protected CheckInStatusResponse migrateLegacyUserCheckIn(User user) {
		CheckIn checkIn = new CheckIn();
		checkIn.setUserId(user.getId());
		checkIn.activate();
		checkIn.setAnchorLatitude(user.getCheckInAnchorLatitude() != null
				? user.getCheckInAnchorLatitude()
				: user.getCheckInLatitude());
		checkIn.setAnchorLongitude(user.getCheckInAnchorLongitude() != null
				? user.getCheckInAnchorLongitude()
				: user.getCheckInLongitude());
		checkIn.setCurrentLatitude(user.getCheckInLatitude());
		checkIn.setCurrentLongitude(user.getCheckInLongitude());
		checkIn.setCheckedInAt(user.getCheckInAt() != null ? user.getCheckInAt() : Instant.now());
		checkIn.setUpdatedAt(user.getCheckInUpdatedAt() != null ? user.getCheckInUpdatedAt() : Instant.now());

		try {
			checkInRepository.saveAndFlush(checkIn);
		} catch (DataIntegrityViolationException ex) {
			return findActiveCheckIn(user.getId())
					.map(CheckInStatusResponse::of)
					.orElseThrow(() -> new DuplicateResourceException("Already checked in"));
		}
		return CheckInStatusResponse.of(checkIn);
	}

	@Transactional
	public CheckInLocationUpdateResponse updateLocation(UpdateLocationRequest request) {
		User user = loadCurrentUser();
		CheckIn checkIn = findActiveCheckIn(user.getId())
				.orElseThrow(() -> new BadRequestException("Not checked in"));

		double distanceKm = GeoUtils.distanceKm(
				checkIn.getAnchorLatitude(),
				checkIn.getAnchorLongitude(),
				request.getLatitude(),
				request.getLongitude());

		if (distanceKm >= GeoUtils.AUTO_CHECKOUT_RADIUS_KM) {
			deactivateAllActiveForUser(user);
			return new CheckInLocationUpdateResponse(false, true);
		}

		checkIn.setCurrentLatitude(request.getLatitude());
		checkIn.setCurrentLongitude(request.getLongitude());
		checkIn.setUpdatedAt(Instant.now());
		checkInRepository.save(checkIn);

		syncUserCheckInFlag(user, checkIn);
		userRepository.save(user);
		return new CheckInLocationUpdateResponse(true, false);
	}

	@Transactional
	public CheckInStatusResponse checkOut() {
		User user = loadCurrentUser();
		deactivateAllActiveForUser(user);
		return CheckInStatusResponse.notCheckedIn();
	}

	@Transactional
	public List<MapUserResponse> getCheckedInUsers() {
		User me = loadCurrentUser();
		if (findActiveCheckIn(me.getId()).isEmpty()) {
			return List.of();
		}

		return checkInRepository.findByUserIdNotAndActiveTrue(me.getId())
				.stream()
				.map(checkIn -> {
					User other = userRepository.findById(checkIn.getUserId())
							.orElseThrow(() -> new BadRequestException("User not found"));
					return MapUserResponse.ofCheckIn(other, checkIn, ProfileSimilarity.score(me, other));
				})
				.toList();
	}

	/**
	 * Returns the single active check-in for the user, healing legacy duplicates
	 * by keeping the newest and deactivating the rest.
	 */
	private Optional<CheckIn> findActiveCheckIn(UUID userId) {
		List<CheckIn> actives = checkInRepository.findByUserIdAndActiveTrue(userId);
		if (actives.isEmpty()) {
			return Optional.empty();
		}
		if (actives.size() == 1) {
			CheckIn only = actives.get(0);
			ensureActiveUserId(only);
			return Optional.of(only);
		}
		return Optional.of(healDuplicateActives(actives));
	}

	private CheckIn healDuplicateActives(List<CheckIn> actives) {
		List<CheckIn> sorted = new ArrayList<>(actives);
		sorted.sort(Comparator
				.comparing(CheckIn::getCheckedInAt, Comparator.nullsFirst(Comparator.naturalOrder()))
				.reversed());
		CheckIn keep = sorted.get(0);
		ensureActiveUserId(keep);
		for (int i = 1; i < sorted.size(); i++) {
			CheckIn duplicate = sorted.get(i);
			duplicate.deactivate();
			duplicate.setUpdatedAt(Instant.now());
			checkInRepository.save(duplicate);
		}
		checkInRepository.save(keep);
		return keep;
	}

	private void ensureActiveUserId(CheckIn checkIn) {
		if (checkIn.isActive() && checkIn.getActiveUserId() == null) {
			checkIn.activate();
			checkInRepository.save(checkIn);
		}
	}

	private void deactivateAllActiveForUser(User user) {
		List<CheckIn> actives = checkInRepository.findByUserIdAndActiveTrue(user.getId());
		Instant now = Instant.now();
		for (CheckIn checkIn : actives) {
			checkIn.deactivate();
			checkIn.setUpdatedAt(now);
			checkInRepository.save(checkIn);
		}
		clearUserCheckInFields(user);
		userRepository.save(user);
	}

	private User loadCurrentUser() {
		User current = SecurityUtils.requireCurrentUser();
		return userRepository.findById(current.getId())
				.orElseThrow(() -> new BadRequestException("User not found"));
	}

	private static void syncUserCheckInFlag(User user, CheckIn checkIn) {
		user.setCheckedIn(1);
		user.setCheckInAnchorLatitude(checkIn.getAnchorLatitude());
		user.setCheckInAnchorLongitude(checkIn.getAnchorLongitude());
		user.setCheckInLatitude(checkIn.getCurrentLatitude());
		user.setCheckInLongitude(checkIn.getCurrentLongitude());
		user.setCheckInAt(checkIn.getCheckedInAt());
		user.setCheckInUpdatedAt(checkIn.getUpdatedAt());
	}

	private static void clearUserCheckInFields(User user) {
		user.setCheckedIn(0);
		user.setCheckInAnchorLatitude(null);
		user.setCheckInAnchorLongitude(null);
		user.setCheckInLatitude(null);
		user.setCheckInLongitude(null);
		user.setCheckInAt(null);
		user.setCheckInUpdatedAt(null);
	}
}
