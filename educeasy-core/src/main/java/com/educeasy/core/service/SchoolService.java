package com.educeasy.core.service;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.educeasy.core.dto.SchoolInfo;
import com.educeasy.core.entity.Principal;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.School;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.PrincipalRepository;
import com.educeasy.core.repository.SchoolRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class SchoolService {

	private final SchoolRepository schoolRepository;

	private UserRepository userRepository;

	private final PrincipalRepository principalRepository;

	public List<SchoolInfo> listMine(String username) {
		User me = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new IllegalArgumentException("User not found"));
		List<School> schools = schoolRepository.findByPrincipalUserId(me.getId());
		return schools.stream().map(this::toDTO).toList();
	}

	@Transactional
	public SchoolInfo create(String username, SchoolInfo info) throws Exception {
		User me = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new IllegalArgumentException("User not found"));

		if (me.getRole() != Role.PRINCIPAL) {
			throw new AccessDeniedException("Access denied : you are not a principal");
		}

		Principal principal = principalRepository.findByUserId(me.getId()).orElseThrow(() -> new IllegalStateException("Missing Principal row for this user"));

		School s = new School();
		s.setNom(info.getNom());
		s.setVille(info.getCity());
		s.setCodePostal(info.getPostalCode());
		s.setAdresse(info.getAddress());
		s.setPrincipal(principal);

		School saved = schoolRepository.save(s);
		return toDTO(saved);
	}

	public SchoolInfo getOne(Long id) {
		School s = schoolRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("School not found"));
		return toDTO(s);
	}

	private SchoolInfo toDTO(School s) {
		Long principalId = (s.getPrincipal() != null) ? s.getPrincipal().getId() : null;
		Long principalUserId = (s.getPrincipal() != null && s.getPrincipal().getUser() != null) ? s.getPrincipal().getUser().getId() : null;

		SchoolInfo info = new SchoolInfo();
		info.setId(s.getId());
		info.setNom(s.getNom());
		info.setCity(s.getVille());
		info.setPostalCode(s.getCodePostal());
		info.setAddress(s.getAdresse());
		info.setPrincipalId(principalId);
		info.setPrincipalUserId(principalUserId);

		return info;
	}

	public SchoolService(SchoolRepository schoolRepository, UserRepository userRepository, PrincipalRepository principalRepository) {
		this.schoolRepository = schoolRepository;
		this.userRepository = userRepository;
		this.principalRepository = principalRepository;
	}
}
