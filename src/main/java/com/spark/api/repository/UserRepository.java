package com.spark.api.repository;

import com.spark.api.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	Optional<User> findByUsernameOrEmail(String username, String email);

	List<User> findByIdNotAndLatitudeIsNotNullAndLongitudeIsNotNull(UUID id);
}
