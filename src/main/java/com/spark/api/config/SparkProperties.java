package com.spark.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spark")
public class SparkProperties {

	private final Mail mail = new Mail();
	private final Otp otp = new Otp();

	public Mail getMail() {
		return mail;
	}

	public Otp getOtp() {
		return otp;
	}

	public static class Mail {
		/** When true, OTPs are emailed via the configured SMTP relay (see spring.mail.*). */
		private boolean enabled = false;
		private String from = "hello@demomailtrap.co";
		private String fromName = "Spark";

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getFromName() {
			return fromName;
		}

		public void setFromName(String fromName) {
			this.fromName = fromName;
		}
	}

	public static class Otp {
		private int ttlMinutes = 10;
		private int length = 6;

		public int getTtlMinutes() {
			return ttlMinutes;
		}

		public void setTtlMinutes(int ttlMinutes) {
			this.ttlMinutes = ttlMinutes;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}
	}
}
