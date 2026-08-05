package com.spark.api.security;

import com.spark.api.entity.User;
import com.spark.api.exception.InvalidCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static User requireCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
			throw new InvalidCredentialsException("Authentication required");
		}
		return user;
	}
}
