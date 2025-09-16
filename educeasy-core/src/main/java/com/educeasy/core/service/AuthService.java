package com.educeasy.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educeasy.core.repository.ClassroomRepository;

@Service
public class AuthService {

	private final ClassroomRepository classroomRepository;

	@Transactional(readOnly = true)
	public boolean isTeacherOfClassroom(Long classroomId, String username) {
		return classroomRepository.findById(classroomId).map(c -> c.getMaitre() != null && username.equals(c.getMaitre().getUser().getUsername())).orElse(false);
	}

	public AuthService(ClassroomRepository classroom) {
		this.classroomRepository = classroom;
	}
}