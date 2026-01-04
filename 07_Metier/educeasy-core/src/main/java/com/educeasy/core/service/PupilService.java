package com.educeasy.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.educeasy.core.dto.PupilInfo;
import com.educeasy.core.entity.Pupil;
import com.educeasy.core.entity.Role;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.UserRepository;

@Service
public class PupilService {

	private final PupilRepository pupilRepository;

	private final UserRepository userRepository;

	private final InscriptionRepository inscriptionRepository;

	public List<PupilInfo> getListOrSearch(String query) {
		List<Pupil> pupils = (query != null && !query.isBlank()) ? pupilRepository.search(query) : pupilRepository.findAllByOrderByNomAsc();

		if (pupils.size() > 10) {
			pupils.subList(0, 10);
		}

		return getListPupilInfo(pupils);
	}

	public List<PupilInfo> searchVisible(String username, String query) {
		var me = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
		List<Pupil> list = (me.getRole() == Role.TEACHER) ? pupilRepository.searchVisibleForTeacher(me.getId(), query) : pupilRepository.searchVisibleForPrincipal(me.getId(), query);

		return list.stream().map(this::toDTO).toList();
	}

	public List<PupilInfo> getListPupilInfo(List<Pupil> pupils) {
		List<PupilInfo> list = new ArrayList<>();
		if (pupils == null || pupils.isEmpty()) {
			return list;
		}
		for (Pupil p : pupils) {
			list.add(toDTO(p));
		}

		return list;
	}

	private PupilInfo toDTO(Pupil p) {
		PupilInfo info = new PupilInfo();
		info.setId(p.getId());
		info.setNom(p.getNom());
		info.setPrenom(p.getPrenom());
		info.setGender(p.getGender() != null ? p.getGender().name() : null);
		
		inscriptionRepository.findCurrentByPupil(p.getId()).ifPresent(i -> {
			var c = i.getClassroom();
			var s = c.getSchool();
			if (c != null) {
				info.setClassroomId(c.getId());
				info.setClassroom(c.getNom());
			}
			if (s != null) {
				info.setSchoolId(s.getId());
				info.setSchool(s.getNom());
			}
		});

		return info;
	}

	public PupilInfo getIfVisible(String username, Long pupilId) {
		var me = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
		boolean ok = (me.getRole() == Role.TEACHER) ? pupilRepository.existsByIdAndActiveInTeacherClassrooms(pupilId, me.getId()) : pupilRepository.existsByIdAndActiveInSchoolsOfPrincipal(pupilId, me.getId());
		if (!ok)
			throw new SecurityException("Forbidden");
		var p = pupilRepository.findById(pupilId).orElseThrow();
		return toDTO(p);
	}

	public PupilService(PupilRepository pupilRepository, UserRepository userRepository, InscriptionRepository inscriptionRepository) {
		this.pupilRepository = pupilRepository;
		this.userRepository = userRepository;
		this.inscriptionRepository = inscriptionRepository;
	}
}
