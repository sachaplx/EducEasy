package com.educeasy.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.entity.School;
import com.educeasy.core.repository.SchoolRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/schools")
@RequiredArgsConstructor
public class SchoolController {

	private final SchoolRepository schoolRepository;

	@GetMapping
	public List<School> list() {
		return schoolRepository.findAll();
	}

	@PreAuthorize("hasRole('PRINCIPAL')")
	@PostMapping
	public School create(@Valid	@RequestBody School e) {
		return schoolRepository.save(e);
	}

	@GetMapping("/{id}")
	public ResponseEntity<School> get(@PathVariable Long id) {
		return schoolRepository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	public SchoolController(SchoolRepository school) {
		this.schoolRepository = school;
	}
}