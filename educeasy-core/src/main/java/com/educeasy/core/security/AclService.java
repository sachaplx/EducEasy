package com.educeasy.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.educeasy.core.entity.Role;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.UserRepository;

@Component("acl")
public class AclService {

	private final UserRepository userRepository;

	private final PupilRepository pupilRepository;

	@Transactional(readOnly = true)
	public boolean canViewPupil(Authentication auth, Long pupilId) {
		if (auth == null || auth.getName() == null) {
			return false;
		}
		var me = userRepository.findByUsernameIgnoreCase(auth.getName()).orElse(null);

		if (me == null) {
			return false;
		}

		if (me.getRole() == Role.TEACHER) {
			return pupilRepository.existsByIdAndActiveInTeacherClassrooms(pupilId, me.getId());
		} else if (me.getRole() == Role.PRINCIPAL) {
			return pupilRepository.existsByIdAndActiveInSchoolsOfPrincipal(pupilId, me.getId());
		}
		return false;
	}

	public AclService(UserRepository userRepository, PupilRepository pupilRepository) {
		this.userRepository = userRepository;
		this.pupilRepository = pupilRepository;
	}
}
