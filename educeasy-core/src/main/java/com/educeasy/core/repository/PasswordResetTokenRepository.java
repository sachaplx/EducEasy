package com.educeasy.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.educeasy.core.entity.PasswordResetToken;
import com.educeasy.core.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	Optional<PasswordResetToken> findByToken(String token);

	void deleteByUser(User user); // Useful for cleaning old tokens.
}
