package com.educeasy.core.service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.educeasy.core.dto.ClassroomInfo;
import com.educeasy.core.dto.PupilInfo;
import com.educeasy.core.entity.Classroom;
import com.educeasy.core.entity.Inscription;
import com.educeasy.core.entity.Professor;
import com.educeasy.core.entity.Pupil;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.School;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.ClassroomRepository;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.ProfessorRepository;
import com.educeasy.core.repository.SchoolRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class ClassroomService {

	private final ClassroomRepository classroomRepository;

	private final InscriptionRepository inscriptionRepository;

	private final UserRepository userRepository;

	private final ProfessorRepository professorRepository;

	private final SchoolRepository schoolRepository;

	private final PupilService pupilService;

	@Transactional(readOnly = true)
	public List<PupilInfo> activePupilsOfClassroom(Long classroomId) {
		return pupilService.getListPupilInfo(inscriptionRepository.findPupilsActifsByClassroom(classroomId));
	}

	public boolean exists(Long classroomId) {
		if (classroomRepository.existsById(classroomId)) {
			return true;
		}
		return false;
	}

	@Transactional(readOnly = true)
	public Optional<ClassroomInfo> getInfo(Long id) {
		return classroomRepository.findByIdWithTeacherAndSchool(id).map(this::toDTO);
	}

	public List<ClassroomInfo> getClassroomsforSchool(Long id) {
		List<Classroom> classes = classroomRepository.findBySchoolId(id);
		return classes.stream().map(this::toDTO).toList();
	}

	private ClassroomInfo toDTO(Classroom c) {
		ClassroomInfo dto = new ClassroomInfo();
		dto.setId(c.getId());
		dto.setName(c.getNom());
		dto.setLevel(c.getNiveau());
		if (c.getSchool() != null) {
			dto.setSchoolId(c.getSchool().getId());
			dto.setSchoolName(c.getSchool().getNom());
		}

		Professor p = c.getMaitre();
		if (p != null) {
			dto.setTeacherId(p.getId());
			dto.setTeacherLastName(p.getNom());
			dto.setTeacherFirstName(p.getPrenom());
		}
		return dto;
	}

	@Transactional
	public void setMaitreByEmail(Long id, String email) {
		if (email == null || email.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email manquant.");
		}
		String emailClean = email.trim();
		Classroom classroom = classroomRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classe introuvable."));
		User user = userRepository.findByEmailIgnoreCase(emailClean).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
		Professor professor = professorRepository.findByUserId(user.getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilisateur non professeur."));

		classroom.setMaitre(professor);
		classroomRepository.save(classroom);
	}

	public PupilInfo addPupilToClassroom(Long id, Map<String, String> body) {
		if (body.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les informations de l'élève sont incorrects.");
		}
		Classroom classroom = classroomRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Classe introuvable."));

		Pupil pupil = pupilService.createPupil(body);

		Inscription inscription = new Inscription();
		inscription.setPupil(pupil);
		inscription.setClassroom(classroom);
		inscription.setDateEntree(LocalDate.now());
		inscription.setDateSortie(null);
		inscriptionRepository.save(inscription);

		return pupilService.toInfo(pupil);
	}

	@Transactional
	public ClassroomInfo createForSchool(String username, Long schoolId, Map<String, String> body) throws Exception {
		String nom = body == null ? null : body.get("nom");
		String niveau = body == null ? null : body.get("niveau");
		String annee = body == null ? null : body.get("anneeScolaire");

		if (nom == null || nom.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nom de classe manquant.");
		}
		if (annee == null || annee.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Année scolaire manquante.");
		}

		User me = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User introuvable"));

		if (me.getRole() != Role.ADMIN) {
			if (me.getRole() != Role.PRINCIPAL) {
				throw new AccessDeniedException("Access denied");
			}
			boolean ok = schoolRepository.existsByIdAndPrincipalUserId(schoolId, me.getId());
			if (!ok) {
				throw new AccessDeniedException("Cette école ne vous appartient pas.");
			}
		}

		School school = schoolRepository.findById(schoolId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "École introuvable."));

		Classroom c = new Classroom();
		c.setSchool(school);
		c.setNom(nom);
		c.setNiveau(niveau);
		c.setAnneeScolaire(annee.trim());
		c = classroomRepository.save(c);

		return toDTO(c);
	}

	public ClassroomService(ClassroomRepository classroom, InscriptionRepository inscription, UserRepository user, SchoolRepository school, ProfessorRepository professor, PupilService pupil) {
		this.classroomRepository = classroom;
		this.inscriptionRepository = inscription;
		this.userRepository = user;
		this.schoolRepository = school;
		this.professorRepository = professor;
		this.pupilService = pupil;
	}
}
