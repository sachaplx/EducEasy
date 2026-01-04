package com.educeasy.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educeasy.core.dto.ClassroomInfo;
import com.educeasy.core.dto.PupilInfo;
import com.educeasy.core.entity.Classroom;
import com.educeasy.core.entity.Professor;
import com.educeasy.core.repository.ClassroomRepository;
import com.educeasy.core.repository.InscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomService {

	private final ClassroomRepository classroomRepository;

	private final InscriptionRepository inscriptionRepository;

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
		var classes = classroomRepository.findBySchoolId(id);
		return classes.stream().map(this::toDTO).toList();
	}

	private ClassroomInfo toDTO(Classroom c) {
		var dto = new ClassroomInfo();
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

	public ClassroomService(ClassroomRepository classroom, InscriptionRepository inscription, PupilService pupil) {
		this.classroomRepository = classroom;
		this.inscriptionRepository = inscription;
		this.pupilService = pupil;
	}
}
