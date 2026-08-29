package com.spark.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spark.api.dto.CheckInLocationUpdateResponse;
import com.spark.api.dto.CheckInStatusResponse;
import com.spark.api.dto.MapUserResponse;
import com.spark.api.dto.UpdateLocationRequest;
import com.spark.api.entity.CheckIn;
import com.spark.api.entity.Role;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.exception.DuplicateResourceException;
import com.spark.api.repository.CheckInRepository;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckInServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CheckInRepository checkInRepository;

	@InjectMocks
	private CheckInService checkInService;

	private MockedStatic<SecurityUtils> securityUtils;

	private User currentUser;
	private UUID currentUserId;

	@BeforeEach
	void setUp() {
		currentUserId = UUID.randomUUID();
		currentUser = new User();
		currentUser.setId(currentUserId);
		currentUser.setUsername("alice");
		currentUser.setEmail("alice@example.com");
		currentUser.setPassword("hash");
		currentUser.setRole(Role.USER);

		securityUtils = mockStatic(SecurityUtils.class);
		securityUtils.when(SecurityUtils::requireCurrentUser).thenReturn(currentUser);
		when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
	}

	@AfterEach
	void tearDown() {
		securityUtils.close();
	}

	@Test
	void checkIn_createsCheckInRowAndSyncsUser() {
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.empty());
		when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> {
			CheckIn saved = invocation.getArgument(0);
			saved.setId(UUID.randomUUID());
			return saved;
		});

		UpdateLocationRequest request = new UpdateLocationRequest();
		request.setLatitude(43.65);
		request.setLongitude(-79.38);

		CheckInStatusResponse status = checkInService.checkIn(request);

		assertTrue(status.isCheckedIn());
		assertEquals(43.65, status.getLatitude());
		assertEquals(-79.38, status.getLongitude());
		assertEquals(43.65, status.getAnchorLatitude());

		ArgumentCaptor<CheckIn> captor = ArgumentCaptor.forClass(CheckIn.class);
		verify(checkInRepository).save(captor.capture());
		assertTrue(captor.getValue().isActive());
		assertEquals(currentUserId, captor.getValue().getUserId());
		assertEquals(1, currentUser.getCheckedIn());
		verify(userRepository).save(currentUser);
	}

	@Test
	void checkIn_whenAlreadyCheckedIn_throwsConflict() {
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId))
				.thenReturn(Optional.of(activeCheckIn(currentUserId)));

		UpdateLocationRequest request = new UpdateLocationRequest();
		request.setLatitude(43.65);
		request.setLongitude(-79.38);

		assertThrows(DuplicateResourceException.class, () -> checkInService.checkIn(request));
	}

	@Test
	void updateLocation_withinRadius_keepsCheckedIn() {
		CheckIn checkIn = activeCheckIn(currentUserId);
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.of(checkIn));
		when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UpdateLocationRequest request = new UpdateLocationRequest();
		request.setLatitude(43.651);
		request.setLongitude(-79.381);

		CheckInLocationUpdateResponse response = checkInService.updateLocation(request);

		assertTrue(response.isCheckedIn());
		assertFalse(response.isAutoCheckedOut());
		verify(checkInRepository).save(checkIn);
	}

	@Test
	void updateLocation_beyondOneKm_autoChecksOut() {
		CheckIn checkIn = activeCheckIn(currentUserId);
		checkIn.setAnchorLatitude(43.6532);
		checkIn.setAnchorLongitude(-79.3832);
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.of(checkIn));
		when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UpdateLocationRequest request = new UpdateLocationRequest();
		request.setLatitude(43.6632);
		request.setLongitude(-79.3832);

		CheckInLocationUpdateResponse response = checkInService.updateLocation(request);

		assertFalse(response.isCheckedIn());
		assertTrue(response.isAutoCheckedOut());
		assertFalse(checkIn.isActive());
		assertEquals(0, currentUser.getCheckedIn());
	}

	@Test
	void checkOut_deactivatesCheckInRow() {
		CheckIn checkIn = activeCheckIn(currentUserId);
		currentUser.setCheckedIn(1);
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.of(checkIn));
		when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

		CheckInStatusResponse status = checkInService.checkOut();

		assertFalse(status.isCheckedIn());
		assertFalse(checkIn.isActive());
		assertEquals(0, currentUser.getCheckedIn());
	}

	@Test
	void checkOut_whenNotCheckedIn_throwsBadRequest() {
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.empty());
		assertThrows(BadRequestException.class, () -> checkInService.checkOut());
	}

	@Test
	void getCheckedInUsers_whenNotCheckedIn_returnsEmpty() {
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId)).thenReturn(Optional.empty());
		List<MapUserResponse> users = checkInService.getCheckedInUsers();
		assertTrue(users.isEmpty());
	}

	@Test
	void getCheckedInUsers_whenCheckedIn_returnsOthers() {
		when(checkInRepository.findByUserIdAndActiveTrue(currentUserId))
				.thenReturn(Optional.of(activeCheckIn(currentUserId)));

		UUID otherUserId = UUID.randomUUID();
		CheckIn otherCheckIn = activeCheckIn(otherUserId);
		otherCheckIn.setCurrentLatitude(43.66);
		otherCheckIn.setCurrentLongitude(-79.39);

		User other = new User();
		other.setId(otherUserId);
		other.setUsername("bob");
		other.setEmail("bob@example.com");
		other.setPassword("hash");
		other.setRole(Role.USER);

		when(checkInRepository.findByUserIdNotAndActiveTrue(currentUserId)).thenReturn(List.of(otherCheckIn));
		when(userRepository.findById(otherUserId)).thenReturn(Optional.of(other));

		List<MapUserResponse> users = checkInService.getCheckedInUsers();

		assertEquals(1, users.size());
		assertEquals(otherUserId, users.get(0).getId());
		assertEquals(43.66, users.get(0).getLatitude());
	}

	private static CheckIn activeCheckIn(UUID userId) {
		CheckIn checkIn = new CheckIn();
		checkIn.setId(UUID.randomUUID());
		checkIn.setUserId(userId);
		checkIn.setActive(true);
		checkIn.setAnchorLatitude(43.65);
		checkIn.setAnchorLongitude(-79.38);
		checkIn.setCurrentLatitude(43.65);
		checkIn.setCurrentLongitude(-79.38);
		checkIn.setCheckedInAt(Instant.now());
		checkIn.setUpdatedAt(Instant.now());
		return checkIn;
	}
}
