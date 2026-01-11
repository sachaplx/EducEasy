package com.educeasy.core.service;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.entity.Classroom;
import com.educeasy.core.entity.MaitreInvitation;
import com.educeasy.core.entity.School;
import com.educeasy.core.repository.ClassroomRepository;
import com.educeasy.core.repository.MaitreInvitationRepository;

@Service
public class MaitreInvitationService {

	private final MaitreInvitationRepository maitreInvitationRepository;
	private final ClassroomRepository classroomRepository;
	private final EmailService emailService;

	private String newToken() {
		byte[] bytes = new byte[32];
		new SecureRandom().nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	@Transactional
	public MaitreInvitation createOrReuse(Long classroomId, String email) {
		String emailClean = email == null ? null : email.trim();
		if (emailClean == null || emailClean.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email manquant.");
		}

		Classroom classroom = classroomRepository.findById(classroomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classe introuvable."));
		var existing = maitreInvitationRepository.findActiveForEmailAndClassroom(emailClean, classroomId);
		if (existing.isPresent()) {
			return existing.get();
		}

		MaitreInvitation inv = new MaitreInvitation();
		inv.setEmail(emailClean);
		inv.setToken(newToken());
		inv.setClassroom(classroom);
		inv.setExpiresAt(emailService.defaultExpiry());
		inv.setUsedAt(null);

		return maitreInvitationRepository.save(inv);
	}

	public void send(MaitreInvitation inv) {
		Classroom c = inv.getClassroom();
		School s = c.getSchool();
		String link = emailService.buildInviteLink(inv.getToken());
		emailService.sendInviteMail(inv.getEmail(), c.getNom(), s != null ? s.getNom() : "(école)", link);
	}

	public MaitreInvitationService(MaitreInvitationRepository maitreInvitationRepository, ClassroomRepository classroomRepository, EmailService emailService) {
		this.maitreInvitationRepository = maitreInvitationRepository;
		this.classroomRepository = classroomRepository;
		this.emailService = emailService;
	}
}
