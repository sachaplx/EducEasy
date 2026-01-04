package com.educeasy.core.service;

import org.springframework.stereotype.Service;

import com.educeasy.core.dto.MyselfInfo;
import com.educeasy.core.repository.PrincipalRepository;
import com.educeasy.core.repository.ProfessorRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class MyselfService {

	private final UserRepository userRepository;

	private final PrincipalRepository principalRepository;

	private final ProfessorRepository professorRepository;

	public MyselfInfo me(String username) {
		var u = userRepository.findByUsernameIgnoreCase(username).orElse(null);

		String firstName = null;
		String lastName = null;

		switch (u.getRole()) {
		case PRINCIPAL:
			var p = principalRepository.findByUserId(u.getId()).orElse(null);
			if (p != null) {
				firstName = p.getPrenom();
				lastName = p.getNom();
			}
			break;
		case TEACHER:
			var t = professorRepository.findByUserId(u.getId()).orElse(null);
			if (t != null) {
				firstName = t.getPrenom();
				lastName = t.getNom();
			}
			break;
		default:
			break;
		}
		MyselfInfo info = new MyselfInfo();
		info.setUserId(u.getId());
		info.setUsername(username);;
		info.setRole(u.getRole().name());;
		info.setFirstName(firstName);
		info.setLastName(lastName);
	
		return info;
	}

	public MyselfService(UserRepository userRepository, PrincipalRepository principalRepository, ProfessorRepository professorRepository) {
		this.userRepository = userRepository;
		this.principalRepository = principalRepository;
		this.professorRepository = professorRepository;
	}
}
