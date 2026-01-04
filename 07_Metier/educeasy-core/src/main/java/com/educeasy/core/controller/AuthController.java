package com.educeasy.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.security.JwtService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authManager;

	private final JwtService jwtService;

	public AuthController(AuthenticationManager authManager, JwtService jwtService) {
		this.authManager = authManager;
		this.jwtService = jwtService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
		Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
		UserDetails user = (UserDetails) auth.getPrincipal();
		String token = jwtService.generateToken(user);
		return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getAuthorities().toString()));
	}

	public record AuthRequest(@NotBlank String username, @NotBlank String password) {
	}

	public record AuthResponse(String token, String username, String roles) {
	}
}
