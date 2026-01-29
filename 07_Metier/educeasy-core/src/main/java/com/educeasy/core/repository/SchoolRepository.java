package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.School;

public interface SchoolRepository extends JpaRepository<School, Long> {

	Optional<School> findByPrincipalUserId(Long userId);

	boolean existsByIdAndPrincipalUserId(Long schoolId, Long userId);
}