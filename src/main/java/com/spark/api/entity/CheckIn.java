package com.spark.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "check_ins")
public class CheckIn {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private boolean active;

	@Column(name = "anchor_latitude", nullable = false)
	private double anchorLatitude;

	@Column(name = "anchor_longitude", nullable = false)
	private double anchorLongitude;

	@Column(name = "checked_in_at", nullable = false, updatable = false)
	private Instant checkedInAt;

	@Column(name = "current_latitude", nullable = false)
	private double currentLatitude;

	@Column(name = "current_longitude", nullable = false)
	private double currentLongitude;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	@Column(name = "user_id", nullable = false)
	private UUID userId;

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		if (checkedInAt == null) {
			checkedInAt = now;
		}
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public double getAnchorLatitude() {
		return anchorLatitude;
	}

	public void setAnchorLatitude(double anchorLatitude) {
		this.anchorLatitude = anchorLatitude;
	}

	public double getAnchorLongitude() {
		return anchorLongitude;
	}

	public void setAnchorLongitude(double anchorLongitude) {
		this.anchorLongitude = anchorLongitude;
	}

	public Instant getCheckedInAt() {
		return checkedInAt;
	}

	public void setCheckedInAt(Instant checkedInAt) {
		this.checkedInAt = checkedInAt;
	}

	public double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}
}
