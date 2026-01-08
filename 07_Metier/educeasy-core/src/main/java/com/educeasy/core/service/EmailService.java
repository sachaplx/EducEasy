package com.educeasy.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;

	@Value("${app.url}")
	private String appUrl;

	@Value("${app.mail.from:aide.educeasy@gmail.com}")
	private String mailFrom;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendEmailConfirmation(String toEmail, String token) {
		String subject = "Confirmation de votre compte Educ'Easy";
		String confirmationUrl = appUrl + "/confirm?token=" + token;
		String text = "Bonjour,\n\n Merci de votre inscription. Cliquez sur le lien suivant pour confirmer votre adresse e-mail :\n" + confirmationUrl + "\n\n Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";

		sendSimple(mailFrom, toEmail, subject, text);
	}

	public void sendPasswordResetEmail(String toEmail, String token) {
		String subject = "Réinitialisation de votre mot de passe";
		String resetUrl = appUrl + "/reset-password?token=" + token;
		String text = "Bonjour,\n\n" + "Vous avez demandé à réinitialiser votre mot de passe.\n" + "Cliquez sur le lien suivant pour définir un nouveau mot de passe :\n" + resetUrl + "\n\n" + "Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer cet email.";

		sendSimple(mailFrom, toEmail, subject, text);
	}

	public void sendEmailChangeNotifications(String oldEmail, String newEmail, String username) {
		String subject = "Modification de votre adresse e-mail";

		String textOld = """
				Bonjour %s,

				L'adresse e-mail associée à votre compte a été modifiée.
				Nouvelle adresse : %s

				Si vous n'êtes pas à l'origine de cette modification, contactez immédiatement le support.
				""".formatted(username, newEmail);

		String textNew = """
				Bonjour %s,

				Cette adresse e-mail est désormais associée à votre compte EducEasy.
				Si vous n'êtes pas à l'origine de cette modification, contactez immédiatement le support.
				""".formatted(username);

		sendSimple(mailFrom, oldEmail, subject, textOld);
		sendSimple(mailFrom, newEmail, subject, textNew);
	}

	private void sendSimple(String from, String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);

		try {
			mailSender.send(message);
		} catch (MailException e) {
			log.error("Email send failed", e);
		}
	}
}
