package com.educeasy.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.School;

public interface SchoolRepository extends JpaRepository<School, Long> {

	List<School> findByPrincipalUserId(Long userId);
}