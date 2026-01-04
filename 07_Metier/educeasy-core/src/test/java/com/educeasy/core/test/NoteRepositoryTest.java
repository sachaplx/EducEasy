package com.educeasy.core.test;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.educeasy.core.entity.Note;
import com.educeasy.core.entity.Pupil;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.NoteRepository;

@DataJpaTest
@ActiveProfiles("test")
class NoteRepositoryTest {
	@Autowired NoteRepository noteRepository;
	@Autowired TestEntityManager em;

	@Test
	void filtersWork() {
		Pupil pupil = new Pupil();
		pupil.setNom("Doe");
		pupil.setPrenom("Jane");
		em.persist(pupil);

		User u = new User();
		u.setUsername("t1");
		u.setRole(Role.TEACHER);
		u.setActif(true);
		em.persist(u);

		Note n = new Note();
		n.setPupil(pupil);
		n.setMatiere("Math");
		n.setNote(new BigDecimal("12"));
		n.setDateNote(LocalDate.now());
		em.persistAndFlush(n);

		var res = noteRepository.findForPupilWithFilters(pupil.getId(), "Math", null, null);
		assertFalse(res.isEmpty());
	}
}