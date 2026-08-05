package com.spark.api.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spark.api.entity.LookingForGender;
import com.spark.api.entity.LookingForIntent;
import org.junit.jupiter.api.Test;

class UpdateProfileRequestJsonTest {

	private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

	@Test
	void deserializesLookingForSelectionsFromFlutterPayload() throws Exception {
		String json = """
				{
				  "name": "roman",
				  "age": 22,
				  "about": "I am a software engineer.",
				  "gender": "men",
				  "intents": ["date", "makeFriends", "networking", "professionalDevelopment"],
				  "dateLookingFor": "men",
				  "friendsLookingFor": "women"
				}
				""";

		UpdateProfileRequest request = mapper.readValue(json, UpdateProfileRequest.class);

		assertEquals(LookingForGender.MEN, request.getDateLookingFor());
		assertEquals(LookingForGender.WOMEN, request.getFriendsLookingFor());
		assertNotNull(request.getIntents());
		assertTrue(request.getIntents().contains(LookingForIntent.DATE));
		assertTrue(request.getIntents().contains(LookingForIntent.MAKE_FRIENDS));
	}
}
