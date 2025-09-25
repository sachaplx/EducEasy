package com.educeasy.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educeasy.core.entity.Classroom;
import com.educeasy.core.entity.Pupil;
import com.educeasy.core.repository.ClassroomRepository;
import com.educeasy.core.repository.InscriptionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassroomService {

	private final ClassroomRepository classroomRepository;

	private final InscriptionRepository inscriptionRepository;

	@Transactional(readOnly = true)
	public List<Pupil> activePupilsOfClassroom(Long classroomId) {
		return inscriptionRepository.findPupilsActifsByClassroom(classroomId);
	}

	@Transactional(readOnly = true)
	public Optional<Classroom> get(Long id) {
		return classroomRepository.findById(id);
	}

	public ClassroomService(ClassroomRepository classroom, InscriptionRepository inscription) {
		this.classroomRepository = classroom;
		this.inscriptionRepository = inscription;
	}
}
