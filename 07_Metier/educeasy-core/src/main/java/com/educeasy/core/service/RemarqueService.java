// com.educeasy.core.service.RemarkService.java
package com.educeasy.core.service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.dto.EntryDTO.CreateRemarkRequest;
import com.educeasy.core.dto.RemarqueInfo;
import com.educeasy.core.entity.RemarkLevel;
import com.educeasy.core.entity.Remarque;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.PrincipalRepository;
import com.educeasy.core.repository.ProfessorRepository;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.RemarqueRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class RemarqueService {
	private final RemarqueRepository remarqueRepository;

	private final PupilRepository pupilRepository;

	private final InscriptionRepository inscriptionRepository;

	private final UserRepository userRepository;

	private final ProfessorRepository professorRepository;

	private final PrincipalRepository principalRepository;

	@Transactional(readOnly = true)
	public List<RemarqueInfo> listForPupil(Long pupilId, RemarkLevel type) {
		var remarks = remarqueRepository.findByPupil(pupilId, type);
		return remarks.stream().map(this::toDTO).toList();
	}

	public RemarqueInfo addRemark(Long pupilId, CreateRemarkRequest req, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow();

		switch (user.getRole()) {
		case TEACHER:
			boolean ok = inscriptionRepository.existsActiveForTeacher(pupilId, user.getId());
			if (!ok) {
				throw new AccessDeniedException("You cannot update this pupil");
			}
		case PRINCIPAL:
			boolean stillOk = inscriptionRepository.existsActiveForPrincipal(pupilId, user.getId());
			if (!stillOk) {
				throw new AccessDeniedException("Pupil is not in any of your school anymore");
			}
			break;
		default:
			throw new AccessDeniedException("Unauthorized role");
		}

		if (req.type() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type est requis.");
		if (req.contenu() == null || req.contenu().isBlank())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le contenu est requis.");

		var pupil = pupilRepository.findById(pupilId).orElseThrow();

		var r = new Remarque();
		r.setPupil(pupil);
		r.setAuteur(user);
		r.setType(RemarkLevel.valueOf(req.type().toUpperCase()));
		r.setContenu(req.contenu().trim());
		r.setCreatedAt(Instant.now());

		var saved = remarqueRepository.save(r);
		return toDTO(saved);
	}

	@Transactional
	public void deleteRemarque(Long pupilId, Long remarqueId, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow(() -> new AccessDeniedException("Utilisateur not found"));

		var remarque = remarqueRepository.findById(remarqueId).orElseThrow(() -> new IllegalArgumentException("Remarqk not found"));

		if (remarque.getPupil() == null || !remarque.getPupil().getId().equals(pupilId)) {
			throw new AccessDeniedException("This remark doesn't match the pupil");
		}

		if (!canManageEntry(user, pupilId)) {
			throw new AccessDeniedException("You cannot manage this remark");
		}

		remarqueRepository.delete(remarque);
	}

	private RemarqueInfo toDTO(Remarque r) {
		var dto = new RemarqueInfo();
		dto.setId(r.getId());
		dto.setPupilId(r.getPupil().getId());
		dto.setType(r.getType() != null ? r.getType().name() : null);
		dto.setContenu(r.getContenu());
		dto.setCreatedAt(r.getCreatedAt());

		var role = r.getAuteur().getRole();
		if (role == Role.TEACHER) {
			professorRepository.findByUserId(r.getAuteur().getId()).ifPresent(p -> {
				dto.setAuteurFirstName(p.getPrenom());
				dto.setAuteurLastName(p.getNom());
			});
		} else if (role == Role.PRINCIPAL) {
			principalRepository.findByUserId(r.getAuteur().getId()).ifPresent(p -> {
				dto.setAuteurFirstName(p.getPrenom());
				dto.setAuteurLastName(p.getNom());
			});
		} else {
			dto.setAuteurFirstName(r.getAuteur().getUsername());
			dto.setAuteurLastName(r.getAuteur().getUsername());
		}
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

	public RemarqueService(RemarqueRepository remarqueRepository, PupilRepository pupilRepository, InscriptionRepository inscriptionRepository, UserRepository userRepository, ProfessorRepository professorRepository, PrincipalRepository principalRepository) {
		this.remarqueRepository = remarqueRepository;
		this.pupilRepository = pupilRepository;
		this.inscriptionRepository = inscriptionRepository;
		this.userRepository = userRepository;
		this.professorRepository = professorRepository;
		this.principalRepository = principalRepository;
	}
}
