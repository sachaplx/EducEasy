package com.educeasy.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.educeasy.core.repository.UserRepository;

@Configuration
public class AuthBeans {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(UserRepository repo) {
		return username -> repo.findByUsernameAndActifTrue(username).map(u -> User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
		var p = new DaoAuthenticationProvider(uds);
		p.setUserDetailsService(uds);
		p.setPasswordEncoder(encoder);
		return p;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}
}