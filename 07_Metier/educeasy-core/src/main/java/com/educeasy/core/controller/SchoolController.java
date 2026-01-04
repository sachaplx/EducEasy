package com.educeasy.core.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.SchoolInfo;
import com.educeasy.core.service.SchoolService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {

	private final SchoolService schoolService;

	@GetMapping("/mine")
	@PreAuthorize("hasRole('PRINCIPAL')")
	public ResponseEntity<List<SchoolInfo>> mySchools(Authentication auth) {
		return ResponseEntity.ok(schoolService.listMine(auth.getName()));
	}

	@PostMapping
	@PreAuthorize("hasRole('PRINCIPAL')")
	public ResponseEntity<SchoolInfo> create(@Valid	@RequestBody SchoolInfo info, Authentication auth) throws Exception {
		return ResponseEntity.ok(schoolService.create(auth.getName(), info));
	}

	@GetMapping("/{id}")
	public ResponseEntity<SchoolInfo> get(@PathVariable Long id) {
		return ResponseEntity.ok(schoolService.getOne(id));
	}

	public SchoolController(SchoolService school) {
		this.schoolService = school;
	}
}