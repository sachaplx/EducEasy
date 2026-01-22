// com.educeasy.core.service.AbsenceService.java
package com.educeasy.core.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.dto.AbsenceInfo;
import com.educeasy.core.dto.EntryDTO.CreateAbsenceRequest;
import com.educeasy.core.entity.Absence;
import com.educeasy.core.entity.HalfDay;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.AbsenceRepository;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class AbsenceService {

	private final AbsenceRepository absenceRepository;

	private final PupilRepository pupilRepository;

	private final InscriptionRepository inscriptionRepository;

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<AbsenceInfo> listForPupil(Long pupilId, LocalDate from, LocalDate to, Integer halfDayCode, Boolean justifie) {
		HalfDay halfDay = null;
		if (halfDayCode != null) {
			halfDay = HalfDay.fromCode(halfDayCode);
		}
		var list = absenceRepository.findByPupil(pupilId, from, to, halfDay, justifie);
		return list.stream().map(this::toDTO).toList();
	}

	public AbsenceInfo addAbsence(Long pupilId, CreateAbsenceRequest req, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow();

		switch (user.getRole()) {
		case TEACHER:
			boolean ok = inscriptionRepository.existsActiveForTeacher(pupilId, user.getId());
			if (!ok) {
				throw new AccessDeniedException("You cannot update this pupil");
			}
			break;
		case PRINCIPAL:
			boolean stillOk = inscriptionRepository.existsActiveForPrincipal(pupilId, user.getId());
			if (!stillOk) {
				throw new AccessDeniedException("Pupil is not in any of your school anymore");
			}
			break;

		default:
			throw new AccessDeniedException("Unauthorized role");

		}

		if (req.date() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date est requise.");
		if (req.halfDay() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La demi-journée est requise");

		var pupil = pupilRepository.findById(pupilId).orElseThrow();
		var a = new Absence();
		a.setPupil(pupil);
		a.setDate(req.date());
		a.setJustifie(Boolean.TRUE.equals(req.justifie()));
		a.setMotif(req.motif());

		a.setHalfDay(HalfDay.valueOf(req.halfDay().toUpperCase()));

		var saved = absenceRepository.save(a);
		return toDTO(saved);
	}

	@Transactional
	public void deleteAbsence(Long pupilId, Long absenceId, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow(() -> new AccessDeniedException("Utilisateur note found"));

		var absence = absenceRepository.findById(absenceId).orElseThrow(() -> new IllegalArgumentException("Absence not found"));

		if (absence.getPupil() == null || !absence.getPupil().getId().equals(pupilId)) {
			throw new AccessDeniedException("This absence doesn't match the pupil");
		}

		if (!canManageEntry(user, pupilId)) {
			throw new AccessDeniedException("You cannot manage this absence");
		}

		absenceRepository.delete(absence);
	}

	private AbsenceInfo toDTO(Absence a) {
		var dto = new AbsenceInfo();
		dto.setId(a.getId());
		dto.setPupilId(a.getPupil().getId());
		dto.setDate(a.getDate());
		if (a.getHalfDay() != null) {
			dto.setHalfDayCode(a.getHalfDay().code());
			dto.setHalfDayLabel(a.getHalfDay().label());
		}
		dto.setJustifie(a.isJustifie());
		dto.setMotif(a.getMotif());
		return dto;
	}

	private boolean canManageEntry(User user, Long pupilId) {
		if (Role.PRINCIPAL.equals(user.getRole())) {
			return true;
		}

		if (Role.TEACHER.equals(user.getRole())) {
			return inscriptionRepository.existsActiveForTeacher(pupilId, user.getId());
		}

		return false;
	}

	public AbsenceService(AbsenceRepository absenceRepository, PupilRepository pupilRepository, InscriptionRepository inscriptionRepository, UserRepository userRepository) {
		this.absenceRepository = absenceRepository;
		this.pupilRepository = pupilRepository;
		this.inscriptionRepository = inscriptionRepository;
		this.userRepository = userRepository;
	}
}
