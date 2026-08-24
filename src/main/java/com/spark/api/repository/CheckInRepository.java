package com.spark.api.repository;

import com.spark.api.entity.CheckIn;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

	Optional<CheckIn> findByUserIdAndActiveTrue(UUID userId);

	List<CheckIn> findByUserIdNotAndActiveTrue(UUID userId);
}
