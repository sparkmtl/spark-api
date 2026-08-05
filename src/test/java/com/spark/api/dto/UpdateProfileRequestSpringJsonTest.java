package com.spark.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.spark.api.entity.LookingForGender;
import com.spark.api.entity.LookingForIntent;
import com.spark.api.entity.ProfileGender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class UpdateProfileRequestSpringJsonTest {

	@Autowired
	private JacksonTester<UpdateProfileRequest> json;

	@Test
	void bindsFlutterLookingForPayload() throws Exception {
		String payload = """
				{
				  "name": "roman",
				  "age": 22,
				  "about": "I am a software engineer.",
				  "gender": "men",
				  "intents": ["date", "makeFriends"],
				  "dateLookingFor": "men",
				  "friendsLookingFor": "women"
				}
				""";

		UpdateProfileRequest request = json.parseObject(payload);

		assertThat(request.getGender()).isEqualTo(ProfileGender.MEN);
		assertThat(request.getDateLookingFor()).isEqualTo(LookingForGender.MEN);
		assertThat(request.getFriendsLookingFor()).isEqualTo(LookingForGender.WOMEN);
		assertThat(request.getIntents())
				.containsExactlyInAnyOrder(LookingForIntent.DATE, LookingForIntent.MAKE_FRIENDS);
	}
}
