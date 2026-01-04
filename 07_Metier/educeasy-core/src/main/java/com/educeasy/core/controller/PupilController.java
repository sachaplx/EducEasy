package com.educeasy.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.service.AuthService;
import com.educeasy.core.service.ClassroomService;

@RestController
@RequestMapping("/pupils")
public class PupilController {

	private final ClassroomService classroomService;

	private final AuthService authService;

	public PupilController(ClassroomService classroom, AuthService auth) {
		this.classroomService = classroom;
		this.authService = auth;
	}
}
