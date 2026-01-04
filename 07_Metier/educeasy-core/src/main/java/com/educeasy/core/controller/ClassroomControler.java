package com.educeasy.core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.ClassroomInfo;
import com.educeasy.core.service.AuthService;
import com.educeasy.core.service.ClassroomService;

@RestController
@RequestMapping("/classrooms")
public class ClassroomController {

	private final ClassroomService classroomService;

	private final AuthService authService;

	@GetMapping("/{id}/list/pupils")
	public ResponseEntity<?> listPupils(@PathVariable Long id, Authentication auth) {

		if (!classroomService.exists(id)) {
			return ResponseEntity.notFound().build();
		}

		if (auth == null || !auth.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		var username = ((UserDetails) auth.getPrincipal()).getUsername();
		boolean isPrincipal = auth.getAuthorities().stream().anyMatch(a -> "ROLE_PRINCIPAL".equals(a.getAuthority()));
		boolean isTeacher = auth.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()));

		if (isPrincipal || (isTeacher && authService.isTeacherOfClassroom(id, username))) {
			return ResponseEntity.ok(classroomService.activePupilsOfClassroom(id));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@GetMapping("/{id}/list")
	public List<ClassroomInfo> getClassroomsForSchool(@PathVariable Long id) {
		return classroomService.getClassroomsforSchool(id);
	}

	public ClassroomController(ClassroomService classroom, AuthService auth) {
		this.classroomService = classroom;
		this.authService = auth;
	}
}
