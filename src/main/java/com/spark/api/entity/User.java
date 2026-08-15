package com.spark.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role = Role.USER;

	/** Profile display name (About me -> Name). */
	@Column(length = 50)
	private String displayName;

	private Integer age;

	@Column(length = 500)
	private String about;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ProfileGender gender;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private LookingForGender dateLookingFor;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private LookingForGender friendsLookingFor;

	@Column(nullable = false)
	private int networking = 0;

	@Column(name = "professional_development", nullable = false)
	private int professionalDevelopment = 0;

	/** Last known map location, shared by the client while viewing the Map tab. */
	private Double latitude;

	private Double longitude;

	private Instant locationUpdatedAt;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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

	public int getNetworking() {
		return networking;
	}

	public void setNetworking(int networking) {
		this.networking = networking;
	}

	public int getProfessionalDevelopment() {
		return professionalDevelopment;
	}

	public void setProfessionalDevelopment(int professionalDevelopment) {
		this.professionalDevelopment = professionalDevelopment;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Instant getLocationUpdatedAt() {
		return locationUpdatedAt;
	}

	public void setLocationUpdatedAt(Instant locationUpdatedAt) {
		this.locationUpdatedAt = locationUpdatedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
