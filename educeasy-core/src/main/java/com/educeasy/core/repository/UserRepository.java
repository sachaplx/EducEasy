package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsernameAndActifTrue(String username);
}