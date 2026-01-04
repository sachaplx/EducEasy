package com.educeasy.core.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.educeasy.core.dto.CustomUserDetails;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameOrEmailIgnoreCase(identifier, identifier).orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new CustomUserDetails(user);
	}
}
