package com.spark.api.service;

import com.spark.api.entity.LookingForIntent;
import com.spark.api.entity.User;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Computes how similar two users' profile settings are, as a score from
 * {@code 0.0} (least similar) to {@code 1.0} (most similar). Used to color
 * user markers on the map from blue (dissimilar) to red (similar).
 *
 * Each comparable profile signal contributes a weight to the final score.
 * Signals that are missing on either profile (e.g. gender not set yet) are
 * skipped entirely and the remaining weights are renormalized, so an
 * incomplete profile doesn't unfairly drag the score toward "dissimilar".
 */
public final class ProfileSimilarity {

	private static final double GENDER_WEIGHT = 0.15;
	private static final double AGE_WEIGHT = 0.20;
	private static final double INTENTS_WEIGHT = 0.35;
	private static final double LOOKING_FOR_WEIGHT = 0.30;

	/** Age difference (in years) beyond which closeness contributes nothing. */
	private static final double AGE_FALLOFF_YEARS = 20.0;

	/** Neutral score returned when two profiles share no comparable data. */
	private static final double NEUTRAL_SCORE = 0.5;

	private ProfileSimilarity() {
	}

	public static double score(User current, User other) {
		double usedWeight = 0;
		double weightedSum = 0;

		if (current.getGender() != null && other.getGender() != null) {
			weightedSum += GENDER_WEIGHT * (current.getGender() == other.getGender() ? 1 : 0);
			usedWeight += GENDER_WEIGHT;
		}

		if (current.getAge() != null && other.getAge() != null) {
			double diff = Math.abs(current.getAge() - other.getAge());
			double closeness = Math.max(0, 1 - diff / AGE_FALLOFF_YEARS);
			weightedSum += AGE_WEIGHT * closeness;
			usedWeight += AGE_WEIGHT;
		}

		Set<LookingForIntent> currentIntents = intentsOf(current);
		Set<LookingForIntent> otherIntents = intentsOf(other);
		if (!currentIntents.isEmpty() && !otherIntents.isEmpty()) {
			weightedSum += INTENTS_WEIGHT * jaccard(currentIntents, otherIntents);
			usedWeight += INTENTS_WEIGHT;
		}

		Double lookingForScore = lookingForAlignment(current, other);
		if (lookingForScore != null) {
			weightedSum += LOOKING_FOR_WEIGHT * lookingForScore;
			usedWeight += LOOKING_FOR_WEIGHT;
		}

		if (usedWeight == 0) {
			return NEUTRAL_SCORE;
		}
		double normalized = weightedSum / usedWeight;
		return Math.max(0.0, Math.min(1.0, normalized));
	}

	private static Double lookingForAlignment(User current, User other) {
		int matches = 0;
		int total = 0;

		if (current.getDateLookingFor() != null && other.getDateLookingFor() != null) {
			total++;
			if (current.getDateLookingFor() == other.getDateLookingFor()) {
				matches++;
			}
		}
		if (current.getFriendsLookingFor() != null && other.getFriendsLookingFor() != null) {
			total++;
			if (current.getFriendsLookingFor() == other.getFriendsLookingFor()) {
				matches++;
			}
		}

		return total == 0 ? null : (double) matches / total;
	}

	private static Set<LookingForIntent> intentsOf(User user) {
		Set<LookingForIntent> intents = new LinkedHashSet<>();
		if (user.getDateLookingFor() != null) {
			intents.add(LookingForIntent.DATE);
		}
		if (user.getFriendsLookingFor() != null) {
			intents.add(LookingForIntent.MAKE_FRIENDS);
		}
		if (user.getNetworking() == 1) {
			intents.add(LookingForIntent.NETWORKING);
		}
		if (user.getProfessionalDevelopment() == 1) {
			intents.add(LookingForIntent.PROFESSIONAL_DEVELOPMENT);
		}
		return intents;
	}

	private static double jaccard(Set<LookingForIntent> a, Set<LookingForIntent> b) {
		Set<LookingForIntent> union = new LinkedHashSet<>(a);
		union.addAll(b);
		if (union.isEmpty()) {
			return 0;
		}
		long intersectionSize = a.stream().filter(b::contains).count();
		return (double) intersectionSize / union.size();
	}
}
