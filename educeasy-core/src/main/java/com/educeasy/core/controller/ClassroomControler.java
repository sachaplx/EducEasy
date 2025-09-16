package com.educeasy.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.entity.Classroom;
import com.educeasy.core.service.AuthService;
import com.educeasy.core.service.ClassroomService;

@RestController
@RequestMapping("/classrooms")
public class ClassroomControler {

	private final ClassroomService classroomService;

	private final AuthService authService;

	@GetMapping("/{id}/list")
	public ResponseEntity<?> listEleves(@PathVariable Long id, Authentication auth) {
		var username = ((UserDetails) auth.getPrincipal()).getUsername();
		boolean isDir = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PRINCIPAL"));
		boolean isInstit = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

		if (isDir || (isInstit && authService.isTeacherOfClassroom(id, username))) {
			return ResponseEntity.ok(classroomService.activePupilsOfClassroom(id));
		}
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Classroom> get(@PathVariable Long id) {
		return classroomService.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	public ClassroomControler(ClassroomService classroom, AuthService auth) {
		this.classroomService = classroom;
		this.authService = auth;
	}
}
