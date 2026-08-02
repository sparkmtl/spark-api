package com.spark.api.repository;

import com.spark.api.entity.OtpChallenge;
import com.spark.api.entity.OtpPurpose;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {

	Optional<OtpChallenge> findTopByEmailIgnoreCaseAndPurposeOrderByCreatedAtDesc(String email, OtpPurpose purpose);

	void deleteByEmailIgnoreCaseAndPurpose(String email, OtpPurpose purpose);
}
