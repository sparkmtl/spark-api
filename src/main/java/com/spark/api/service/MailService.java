package com.spark.api.service;

import com.spark.api.config.SparkProperties;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Sends transactional email (OTP codes) through Mailtrap's live sending SMTP relay.
 *
 * <p>SMTP connection settings come from {@code spring.mail.*} (see application.yml),
 * which resolves to the {@code MAILTRAP_SMTP_HOST}/{@code MAILTRAP_SMTP_PORT}/
 * {@code MAILTRAP_SMTP_USERNAME}/{@code MAILTRAP_TOKEN} environment variables. Spring Boot
 * auto-configures the {@link JavaMailSender} bean from those properties.
 */
@Service
public class MailService {

	private static final Logger log = LoggerFactory.getLogger(MailService.class);

	private final SparkProperties sparkProperties;
	private final JavaMailSender mailSender;

	public MailService(SparkProperties sparkProperties, JavaMailSender mailSender) {
		this.sparkProperties = sparkProperties;
		this.mailSender = mailSender;
	}

	/**
	 * Delivers the OTP to the recipient's real mailbox via Mailtrap live sending.
	 *
	 * @throws IllegalStateException when mail is disabled or the SMTP send fails
	 */
	public void deliverOtp(String toEmail, String otp, String subject) {
		SparkProperties.Mail mail = sparkProperties.getMail();
		if (!mail.isEnabled()) {
			throw new IllegalStateException("Email delivery is disabled (MAIL_ENABLED=false)");
		}

		String text = "Your Spark verification code is: " + otp
				+ "\n\nThis code expires soon. Do not share it.";
		String html = "<p>Your Spark verification code is: <strong>" + escapeHtml(otp)
				+ "</strong></p><p>This code expires soon. Do not share it.</p>";

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			helper.setFrom(mail.getFrom(), mail.getFromName());
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(text, html);
			mailSender.send(message);
			log.info("OTP email delivered via Mailtrap SMTP to {}", toEmail);
		} catch (MailException ex) {
			throw new IllegalStateException(describeFailure(ex), ex);
		} catch (Exception ex) {
			throw new IllegalStateException("Mailtrap SMTP send failed: " + ex.getMessage(), ex);
		}
	}

	private static String describeFailure(MailException ex) {
		Throwable cause = ex.getMostSpecificCause();
		String message = cause != null ? cause.getMessage() : ex.getMessage();
		if (message != null && message.toLowerCase().contains("demo domain")) {
			return "Demo sending domain can only email the Mailtrap account owner. "
					+ "Verify your own domain in Mailtrap -> Sending Domains to email any recipient. Cause: "
					+ message;
		}
		return "Mailtrap SMTP send failed: " + message;
	}

	private static String escapeHtml(String value) {
		if (value == null) {
			return "";
		}
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}
}
