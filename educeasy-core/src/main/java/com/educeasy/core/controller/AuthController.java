package com.educeasy.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.AuthInfo.AuthResponse;
import com.educeasy.core.dto.AuthInfo.ChangePasswordRequest;
import com.educeasy.core.dto.AuthInfo.ChangePasswordResponse;
import com.educeasy.core.dto.AuthInfo.ForgotPasswordRequest;
import com.educeasy.core.dto.AuthInfo.ForgotPasswordResponse;
import com.educeasy.core.dto.AuthInfo.LoginRequest;
import com.educeasy.core.dto.AuthInfo.ProfileResponse;
import com.educeasy.core.dto.AuthInfo.RegisterRequest;
import com.educeasy.core.dto.AuthInfo.RegisterResponse;
import com.educeasy.core.dto.AuthInfo.ResetPasswordRequest;
import com.educeasy.core.dto.AuthInfo.ResetPasswordResponse;
import com.educeasy.core.dto.AuthInfo.ResetPasswordValidateRequest;
import com.educeasy.core.dto.AuthInfo.ResetPasswordValidateResponse;
import com.educeasy.core.dto.AuthInfo.UpdateEmailRequest;
import com.educeasy.core.dto.AuthInfo.UpdateEmailResponse;
import com.educeasy.core.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid
	@RequestBody
	LoginRequest req) {
		return ResponseEntity.ok(authService.login(req));
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@Valid
	@RequestBody
	RegisterRequest req) {
		return ResponseEntity.ok(authService.register(req));
	}

	@GetMapping("/confirm")
	public ResponseEntity<AuthResponse> confirmEmail(@RequestParam("token")
	String token) {
		return ResponseEntity.ok(authService.confirm(token));
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid
	@RequestBody
	ForgotPasswordRequest req) {
		return ResponseEntity.ok(authService.forgotPassword(req));
	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid
	@RequestBody
	ResetPasswordRequest req) {
		return ResponseEntity.ok(authService.resetPassword(req));
	}

	@PostMapping("/reset-password/validate")
	public ResponseEntity<ResetPasswordValidateResponse> validateResetPasswordToken(@Valid
	@RequestBody
	ResetPasswordValidateRequest req) {
		return ResponseEntity.ok(authService.validateResetPasswordToken(req));
	}

	@GetMapping("/me")
	public ResponseEntity<ProfileResponse> me() {
		return ResponseEntity.ok(authService.getProfile());
	}

	@PutMapping("/me/email")
	public ResponseEntity<UpdateEmailResponse> updateEmail(@Valid
	@RequestBody
	UpdateEmailRequest req) {
		return ResponseEntity.ok(authService.updateEmail(req));
	}

	@PutMapping("/me/password")
	public ResponseEntity<ChangePasswordResponse> changePassword(@Valid
	@RequestBody
	ChangePasswordRequest req) {
		return ResponseEntity.ok(authService.changePassword(req));
	}

	public AuthController(AuthService authService) {
		this.authService = authService;
	}
}