package com.spark.api.service;

import com.spark.api.dto.ProfileResponse;
import com.spark.api.dto.UpdateProfileRequest;
import com.spark.api.entity.LookingForGender;
import com.spark.api.entity.LookingForIntent;
import com.spark.api.entity.ProfileGender;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.SecurityUtils;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

	private final UserRepository userRepository;

	public ProfileService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public ProfileResponse getMyProfile() {
		User current = SecurityUtils.requireCurrentUser();
		User user = userRepository.findById(current.getId())
				.orElseThrow(() -> new BadRequestException("User not found"));
		return ProfileResponse.fromEntity(user);
	}

	@Transactional
	public ProfileResponse updateMyProfile(UpdateProfileRequest request) {
		User current = SecurityUtils.requireCurrentUser();
		User user = userRepository.findById(current.getId())
				.orElseThrow(() -> new BadRequestException("User not found"));

		Set<LookingForIntent> intents = normalizeIntents(request.getIntents());
		boolean lookingForDate = intents.contains(LookingForIntent.DATE);
		boolean lookingForFriends = intents.contains(LookingForIntent.MAKE_FRIENDS);
		boolean networking = intents.contains(LookingForIntent.NETWORKING);
		boolean professionalDevelopment = intents.contains(LookingForIntent.PROFESSIONAL_DEVELOPMENT);

		LookingForGender dateLookingFor = resolveDateLookingFor(request, lookingForDate, request.getGender());
		LookingForGender friendsLookingFor = resolveFriendsLookingFor(request, lookingForFriends);

		user.setDisplayName(request.getName().trim());
		user.setAge(request.getAge());
		user.setAbout(request.getAbout().trim());
		user.setGender(request.getGender());
		user.setNetworking(networking ? 1 : 0);
		user.setProfessionalDevelopment(professionalDevelopment ? 1 : 0);
		user.setDateLookingFor(dateLookingFor);
		user.setFriendsLookingFor(friendsLookingFor);

		User saved = userRepository.save(user);
		return ProfileResponse.fromEntity(saved);
	}

	private Set<LookingForIntent> normalizeIntents(Set<LookingForIntent> intents) {
		if (intents == null || intents.isEmpty()) {
			throw new BadRequestException("Select at least one option");
		}
		return new HashSet<>(intents);
	}

	private LookingForGender resolveDateLookingFor(
			UpdateProfileRequest request,
			boolean lookingForDate,
			ProfileGender gender) {
		if (!lookingForDate) {
			return null;
		}
		if (request.getDateLookingFor() != null) {
			return request.getDateLookingFor();
		}
		return oppositeGender(gender);
	}

	private LookingForGender resolveFriendsLookingFor(
			UpdateProfileRequest request,
			boolean lookingForFriends) {
		if (!lookingForFriends) {
			return null;
		}
		if (request.getFriendsLookingFor() != null) {
			return request.getFriendsLookingFor();
		}
		return LookingForGender.ANY;
	}

	private LookingForGender oppositeGender(ProfileGender gender) {
		if (gender == ProfileGender.MEN) {
			return LookingForGender.WOMEN;
		}
		if (gender == ProfileGender.WOMEN) {
			return LookingForGender.MEN;
		}
		return LookingForGender.ANY;
	}
}
