package com.spark.api.repository;

import com.spark.api.entity.CheckIn;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

	List<CheckIn> findByUserIdAndActiveTrue(UUID userId);

	List<CheckIn> findByUserIdNotAndActiveTrue(UUID userId);
}
