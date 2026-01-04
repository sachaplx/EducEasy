package com.educeasy.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthInfo {

	public record LoginRequest(@NotBlank
	String identifier, @NotBlank
	String password) {
	}

	public record AuthResponse(String token, Long userId, String username, String role) {
	}

	public record RegisterResponse(Long userId, String username, String role, String message) {
	}

	public record RegisterRequest(@NotBlank
	@Email
	String email, @NotBlank
	@Size(min = 3, max = 50)
	String username, @NotBlank
	@Size(min = 6, max = 100)
	String password, @NotBlank
	String role, @NotBlank
	String firstName, @NotBlank
	String lastName) {
	}

	public record ForgotPasswordRequest(@NotBlank
	String identifier) {
	}

	public record ForgotPasswordResponse(String message) {
	}

	public record ResetPasswordRequest(@NotBlank
	String token, @NotBlank
	String newPassword) {
	}

	public record ResetPasswordResponse(String message) {
	}

	public record ResetPasswordValidateRequest(@NotBlank
	String token) {
	}

	public record ResetPasswordValidateResponse(boolean valid, String message) {
	}
	
	public record ProfileResponse(Long id, String username, String email, String role, String firstName, String lastName) {
	}
	 
	public record UpdateEmailRequest(@NotBlank @Email String email, @NotBlank String currentPassword) {
	}
	
	public record UpdateEmailResponse(String email, String message) {
	}
	
	public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {
	}
	
	public record ChangePasswordResponse(String message) {
	}
}
