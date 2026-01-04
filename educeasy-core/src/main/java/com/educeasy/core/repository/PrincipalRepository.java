package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.Principal;

public interface PrincipalRepository extends JpaRepository<Principal, Long> {

	Optional<Principal> findByUserId(Long userId);

	boolean existsByUserId(Long userId);
}
