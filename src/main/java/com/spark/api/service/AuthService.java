package com.spark.api.service;

import com.spark.api.config.SparkProperties;
import com.spark.api.dto.CompleteRegisterRequest;
import com.spark.api.dto.ForgotPasswordRequest;
import com.spark.api.dto.LoginRequest;
import com.spark.api.dto.LoginResponse;
import com.spark.api.dto.MessageResponse;
import com.spark.api.dto.OtpSentResponse;
import com.spark.api.dto.ResetPasswordRequest;
import com.spark.api.dto.SendSignupOtpRequest;
import com.spark.api.dto.UserResponse;
import com.spark.api.dto.VerifyOtpRequest;
import com.spark.api.entity.OtpChallenge;
import com.spark.api.entity.OtpPurpose;
import com.spark.api.entity.Role;
import com.spark.api.entity.User;
import com.spark.api.exception.BadRequestException;
import com.spark.api.exception.DuplicateResourceException;
import com.spark.api.exception.InvalidCredentialsException;
import com.spark.api.repository.OtpChallengeRepository;
import com.spark.api.repository.UserRepository;
import com.spark.api.security.JwtService;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final OtpChallengeRepository otpChallengeRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final MailService mailService;
	private final SparkProperties sparkProperties;
	private final SecureRandom secureRandom = new SecureRandom();

	public AuthService(
			UserRepository userRepository,
			OtpChallengeRepository otpChallengeRepository,
			PasswordEncoder passwordEncoder,
			JwtService jwtService,
			MailService mailService,
			SparkProperties sparkProperties) {
		this.userRepository = userRepository;
		this.otpChallengeRepository = otpChallengeRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.mailService = mailService;
		this.sparkProperties = sparkProperties;
	}

	@Transactional
	public OtpSentResponse sendSignupOtp(SendSignupOtpRequest request) {
		String email = normalizeEmail(request.getEmail());
		String name = request.getName().trim();

		if (userRepository.existsByUsername(name)) {
			throw new DuplicateResourceException("Username is already taken");
		}
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("Email is already registered");
		}

		otpChallengeRepository.deleteByEmailIgnoreCaseAndPurpose(email, OtpPurpose.SIGNUP);

		String otp = generateOtp();
		OtpChallenge challenge = new OtpChallenge();
		challenge.setEmail(email);
		challenge.setPurpose(OtpPurpose.SIGNUP);
		challenge.setOtpHash(passwordEncoder.encode(otp));
		challenge.setUsername(name);
		challenge.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		challenge.setVerified(false);
		challenge.setExpiresAt(Instant.now().plus(sparkProperties.getOtp().getTtlMinutes(), ChronoUnit.MINUTES));
		otpChallengeRepository.save(challenge);

		try {
			mailService.deliverOtp(email, otp, "Spark signup verification code");
		} catch (IllegalStateException ex) {
			throw new BadRequestException(ex.getMessage());
		}
		return new OtpSentResponse("If the request is valid, a verification code has been sent.");
	}

	@Transactional
	public OtpSentResponse forgotPassword(ForgotPasswordRequest request) {
		String email = normalizeEmail(request.getEmail());
		String genericMessage = "If an account exists for this email, a verification code has been sent.";

		if (!userRepository.existsByEmail(email)) {
			return new OtpSentResponse(genericMessage);
		}

		otpChallengeRepository.deleteByEmailIgnoreCaseAndPurpose(email, OtpPurpose.PASSWORD_RESET);

		String otp = generateOtp();
		OtpChallenge challenge = new OtpChallenge();
		challenge.setEmail(email);
		challenge.setPurpose(OtpPurpose.PASSWORD_RESET);
		challenge.setOtpHash(passwordEncoder.encode(otp));
		challenge.setVerified(false);
		challenge.setExpiresAt(Instant.now().plus(sparkProperties.getOtp().getTtlMinutes(), ChronoUnit.MINUTES));
		otpChallengeRepository.save(challenge);

		try {
			mailService.deliverOtp(email, otp, "Spark password reset code");
		} catch (IllegalStateException ex) {
			throw new BadRequestException(ex.getMessage());
		}
		return new OtpSentResponse(genericMessage);
	}

	@Transactional
	public MessageResponse verifyOtp(VerifyOtpRequest request) {
		String email = normalizeEmail(request.getEmail());
		OtpChallenge challenge = otpChallengeRepository
				.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, request.getPurpose())
				.orElseThrow(() -> new BadRequestException("No active verification request found"));

		if (challenge.getExpiresAt().isBefore(Instant.now())) {
			otpChallengeRepository.delete(challenge);
			throw new BadRequestException("Verification code has expired");
		}

		if (!passwordEncoder.matches(request.getOtp(), challenge.getOtpHash())) {
			throw new BadRequestException("Invalid verification code");
		}

		challenge.setVerified(true);
		otpChallengeRepository.save(challenge);
		return new MessageResponse("Verification successful");
	}

	@Transactional
	public UserResponse register(CompleteRegisterRequest request) {
		String email = normalizeEmail(request.getEmail());
		OtpChallenge challenge = requireVerifiedChallenge(email, OtpPurpose.SIGNUP);

		if (userRepository.existsByUsername(challenge.getUsername())) {
			throw new DuplicateResourceException("Username is already taken");
		}
		if (userRepository.existsByEmail(email)) {
			throw new DuplicateResourceException("Email is already registered");
		}

		User user = new User();
		user.setUsername(challenge.getUsername());
		user.setEmail(email);
		user.setPassword(challenge.getPasswordHash());
		user.setRole(Role.USER);

		User saved = userRepository.save(user);
		otpChallengeRepository.deleteByEmailIgnoreCaseAndPurpose(email, OtpPurpose.SIGNUP);
		return UserResponse.fromEntity(saved);
	}

	@Transactional
	public MessageResponse resetPassword(ResetPasswordRequest request) {
		String email = normalizeEmail(request.getEmail());
		OtpChallenge challenge = requireVerifiedChallenge(email, OtpPurpose.PASSWORD_RESET);

		User user = userRepository
				.findByUsernameOrEmail(email, email)
				.orElseThrow(() -> new BadRequestException("Account not found"));

		user.setPassword(passwordEncoder.encode(request.getPassword()));
		userRepository.save(user);
		otpChallengeRepository.deleteByEmailIgnoreCaseAndPurpose(email, OtpPurpose.PASSWORD_RESET);
		return new MessageResponse("Password updated successfully");
	}

	public LoginResponse login(LoginRequest request) {
		User user = userRepository
				.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid username/email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid username/email or password");
		}

		String token = jwtService.generateToken(user);
		return new LoginResponse(token, jwtService.getExpirationSeconds(), UserResponse.fromEntity(user));
	}

	private OtpChallenge requireVerifiedChallenge(String email, OtpPurpose purpose) {
		OtpChallenge challenge = otpChallengeRepository
				.findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(email, purpose)
				.orElseThrow(() -> new BadRequestException("No verified challenge found. Complete OTP verification first."));

		if (challenge.getExpiresAt().isBefore(Instant.now())) {
			otpChallengeRepository.delete(challenge);
			throw new BadRequestException("Verification has expired. Request a new code.");
		}
		if (!challenge.isVerified()) {
			throw new BadRequestException("OTP has not been verified yet");
		}
		return challenge;
	}

	private String generateOtp() {
		int length = sparkProperties.getOtp().getLength();
		int bound = (int) Math.pow(10, length);
		int min = bound / 10;
		int value = secureRandom.nextInt(bound - min) + min;
		return String.valueOf(value);
	}

	private static String normalizeEmail(String email) {
		return email.trim().toLowerCase();
	}
}
