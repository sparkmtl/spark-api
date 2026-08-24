package com.spark.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GeoUtilsTest {

	@Test
	void distanceKm_samePointIsZero() {
		assertEquals(0.0, GeoUtils.distanceKm(43.65, -79.38, 43.65, -79.38), 0.001);
	}

	@Test
	void distanceKm_oneKmApart() {
		// ~1 km north along a meridian at Toronto latitude
		double km = GeoUtils.distanceKm(43.6532, -79.3832, 43.6622, -79.3832);
		assertTrue(km > 0.9 && km < 1.1);
	}

	@Test
	void distanceKm_beyondAutoCheckoutRadius() {
		double km = GeoUtils.distanceKm(43.6532, -79.3832, 43.6632, -79.3832);
		assertTrue(km > GeoUtils.AUTO_CHECKOUT_RADIUS_KM);
	}
}
