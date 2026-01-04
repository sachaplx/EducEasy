package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.Professor;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {

	Optional<Professor> findByUserId(Long userId);
}
