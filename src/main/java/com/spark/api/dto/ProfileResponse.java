package com.spark.api.dto;

import com.spark.api.entity.LookingForGender;
import com.spark.api.entity.LookingForIntent;
import com.spark.api.entity.ProfileGender;
import com.spark.api.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class ProfileResponse {

	private UUID id;
	private String name;
	private Integer age;
	private String about;
	private ProfileGender gender;
	/** Derived from users columns (API compatibility for Flutter). */
	private Set<LookingForIntent> intents;
	private LookingForGender dateLookingFor;
	private LookingForGender friendsLookingFor;
	/** 1 if selected, 0 if not. */
	private int networking;
	/** 1 if selected, 0 if not. */
	private int professionalDevelopment;
	private boolean complete;

	public static ProfileResponse fromEntity(User user) {
		ProfileResponse response = new ProfileResponse();
		response.id = user.getId();
		String displayName = user.getDisplayName();
		response.name = displayName != null && !displayName.isBlank() ? displayName : user.getUsername();
		response.age = user.getAge();
		response.about = user.getAbout();
		response.gender = user.getGender();
		response.networking = user.getNetworking();
		response.professionalDevelopment = user.getProfessionalDevelopment();
		response.dateLookingFor = user.getDateLookingFor();
		response.friendsLookingFor = user.getFriendsLookingFor();
		response.intents = intentsFromColumns(user);
		response.complete = isComplete(user);
		return response;
	}

	private static Set<LookingForIntent> intentsFromColumns(User user) {
		Set<LookingForIntent> intents = new LinkedHashSet<>();
		if (user.getDateLookingFor() != null) {
			intents.add(LookingForIntent.DATE);
		}
		if (user.getFriendsLookingFor() != null) {
			intents.add(LookingForIntent.MAKE_FRIENDS);
		}
		if (user.getNetworking() == 1) {
			intents.add(LookingForIntent.NETWORKING);
		}
		if (user.getProfessionalDevelopment() == 1) {
			intents.add(LookingForIntent.PROFESSIONAL_DEVELOPMENT);
		}
		return intents;
	}

	private static boolean isComplete(User user) {
		boolean hasIntent = user.getDateLookingFor() != null
				|| user.getFriendsLookingFor() != null
				|| user.getNetworking() == 1
				|| user.getProfessionalDevelopment() == 1;
		return user.getDisplayName() != null
				&& !user.getDisplayName().isBlank()
				&& user.getAge() != null
				&& user.getAbout() != null
				&& !user.getAbout().isBlank()
				&& user.getGender() != null
				&& hasIntent;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getAge() {
		return age;
	}

	public String getAbout() {
		return about;
	}

	public ProfileGender getGender() {
		return gender;
	}

	public Set<LookingForIntent> getIntents() {
		return intents;
	}

	public LookingForGender getDateLookingFor() {
		return dateLookingFor;
	}

	public LookingForGender getFriendsLookingFor() {
		return friendsLookingFor;
	}

	public int getNetworking() {
		return networking;
	}

	public int getProfessionalDevelopment() {
		return professionalDevelopment;
	}

	public boolean isComplete() {
		return complete;
	}
}
