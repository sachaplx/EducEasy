package com.educeasy.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthFilter jwtFilter;

	private final DaoAuthenticationProvider daoProvider;

	public SecurityConfig(JwtAuthFilter jwtFilter, DaoAuthenticationProvider daoProvider) {
		this.jwtFilter = jwtFilter;
		this.daoProvider = daoProvider;
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		http.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/auth/login", "/auth/register", "/auth/confirm", "/auth/forgot-password", "/auth/reset-password", "/auth/reset-password/validate", "/auth/invite", "/auth/invite/complete", "/ping").permitAll().anyRequest().authenticated());
		http.authenticationProvider(daoProvider);
		http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
