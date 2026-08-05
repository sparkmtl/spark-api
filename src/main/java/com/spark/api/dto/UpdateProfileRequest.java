package com.spark.api.dto;

import com.spark.api.entity.LookingForGender;
import com.spark.api.entity.LookingForIntent;
import com.spark.api.entity.ProfileGender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateProfileRequest {

	@NotBlank(message = "Name is required")
	@Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
	private String name;

	@NotNull(message = "Age is required")
	@Min(value = 18, message = "You must be at least 18")
	@Max(value = 120, message = "Enter a valid age")
	private Integer age;

	@NotBlank(message = "Tell us a bit about yourself")
	@Size(max = 500, message = "Keep it under 500 characters")
	private String about;

	@NotNull(message = "Select your gender")
	private ProfileGender gender;

	@NotEmpty(message = "Select at least one option")
	private Set<LookingForIntent> intents;

	private LookingForGender dateLookingFor;

	private LookingForGender friendsLookingFor;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public ProfileGender getGender() {
		return gender;
	}

	public void setGender(ProfileGender gender) {
		this.gender = gender;
	}

	public Set<LookingForIntent> getIntents() {
		return intents;
	}

	public void setIntents(Set<LookingForIntent> intents) {
		this.intents = intents;
	}

	public LookingForGender getDateLookingFor() {
		return dateLookingFor;
	}

	public void setDateLookingFor(LookingForGender dateLookingFor) {
		this.dateLookingFor = dateLookingFor;
	}

	public LookingForGender getFriendsLookingFor() {
		return friendsLookingFor;
	}

	public void setFriendsLookingFor(LookingForGender friendsLookingFor) {
		this.friendsLookingFor = friendsLookingFor;
	}
}
