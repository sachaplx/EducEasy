package com.educeasy.core.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.ClassroomInfo;
import com.educeasy.core.dto.SchoolInfo;
import com.educeasy.core.service.ClassroomService;
import com.educeasy.core.service.SchoolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/schools")
public class SchoolController {

	private final SchoolService schoolService;

	private final ClassroomService classroomService;

	@GetMapping("/mine")
	@PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN')")
	public ResponseEntity<List<SchoolInfo>> mySchools(Authentication auth) {
		return ResponseEntity.ok(schoolService.listMine(auth.getName()));
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN')")
	public ResponseEntity<SchoolInfo> create(@Valid
	@RequestBody
	SchoolInfo info, Authentication auth) throws Exception {
		return ResponseEntity.ok(schoolService.create(auth.getName(), info));
	}

	@GetMapping("/{id}")
	public ResponseEntity<SchoolInfo> get(@PathVariable
	Long id) {
		return ResponseEntity.ok(schoolService.getOne(id));
	}

	@PostMapping("/{id}/classrooms")
	@PreAuthorize("hasAnyRole('PRINCIPAL', 'ADMIN')")
	public ResponseEntity<ClassroomInfo> createClassroom(@PathVariable
	Long id, @RequestBody
	Map<String, String> body, Authentication auth) throws Exception {
		return ResponseEntity.ok(classroomService.createForSchool(auth.getName(), id, body));
	}

	public SchoolController(SchoolService school, ClassroomService classroom) {
		this.schoolService = school;
		this.classroomService = classroom;
	}
}