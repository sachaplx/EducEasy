package com.educeasy.core.service;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.dto.EntryDTO.CreateNoteRequest;
import com.educeasy.core.dto.NoteInfo;
import com.educeasy.core.entity.Note;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.NoteRepository;
import com.educeasy.core.repository.ProfessorRepository;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class NoteService {

	private final NoteRepository noteRepository;
	private final PupilRepository pupilRepository;
	private final InscriptionRepository inscriptionRepository;
	private final UserRepository userRepository;
	private final ProfessorRepository professorRepository;

	@Transactional
	public NoteInfo addNote(Long pupilId, CreateNoteRequest req, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow();

		switch (user.getRole()) {
		case TEACHER:
			boolean ok = inscriptionRepository.existsActiveForTeacher(pupilId, user.getId());
			if (!ok) {
				throw new AccessDeniedException("You don't have permission to update this pupil.");
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
		if (req.note() == null || req.note().compareTo(BigDecimal.ZERO) < 0 || req.note().compareTo(BigDecimal.TEN.add(BigDecimal.TEN)) > 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note must be between 0 and 20");
		}
		if (req.matiere() == null || req.matiere().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course is required");
		}

		var pupil = pupilRepository.findById(pupilId).orElseThrow();

		var n = new Note();
		n.setPupil(pupil);
		n.setMatiere(req.matiere().trim());
		n.setNote(req.note());
		n.setDateNote(req.dateNote() != null ? req.dateNote() : LocalDate.now());
		n.setCommentaire(req.commentaire());

		var saved = noteRepository.save(n);
		return toDTO(saved);
	}

	@Transactional(readOnly = true)
	public List<NoteInfo> listForPupil(Long pupilId, String matiere, LocalDate from, LocalDate to) {
		var notes = noteRepository.findByPupil(pupilId, matiere, from, to);
		return notes.stream().map(this::toDTO).toList();
	}

	@Transactional
	public void deleteNote(Long pupilId, Long noteId, Authentication auth) throws Exception {
		var user = userRepository.findByUsernameIgnoreCase(auth.getName()).orElseThrow(() -> new AccessDeniedException("User not found"));

		var note = noteRepository.findById(noteId).orElseThrow(() -> new IllegalArgumentException("Note not found"));

		if (note.getPupil() == null || !note.getPupil().getId().equals(pupilId)) {
			throw new AccessDeniedException("This note doesn't match the pupil");
		}

		if (!canManageEntry(user, pupilId)) {
			throw new AccessDeniedException("You cannot manage this note");
		}

		noteRepository.delete(note);
	}

	private NoteInfo toDTO(Note n) {
		var dto = new NoteInfo();
		dto.setId(n.getId());
		dto.setPupilId(n.getPupil().getId());
		dto.setMatiere(n.getMatiere());
		dto.setNote(n.getNote());
		dto.setDate(n.getDateNote());
		dto.setCommentaire(n.getCommentaire());
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

	public NoteService(NoteRepository noteRepository, PupilRepository pupilRepository, InscriptionRepository inscriptionRepository, UserRepository userRepository, ProfessorRepository professorRepository) {
		this.noteRepository = noteRepository;
		this.pupilRepository = pupilRepository;
		this.inscriptionRepository = inscriptionRepository;
		this.userRepository = userRepository;
		this.professorRepository = professorRepository;
	}

}
