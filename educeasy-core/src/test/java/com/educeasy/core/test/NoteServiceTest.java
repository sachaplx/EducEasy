package com.educeasy.core.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.educeasy.core.dto.EntryDTO;
import com.educeasy.core.entity.Note;
import com.educeasy.core.entity.Pupil;
import com.educeasy.core.entity.Role;
import com.educeasy.core.entity.User;
import com.educeasy.core.repository.InscriptionRepository;
import com.educeasy.core.repository.NoteRepository;
import com.educeasy.core.repository.PupilRepository;
import com.educeasy.core.repository.UserRepository;
import com.educeasy.core.service.NoteService;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
	@Mock NoteRepository noteRepository;
	@Mock PupilRepository pupilRepository;
	@Mock UserRepository userRepository;
	@Mock InscriptionRepository inscriptionRepository;
	@InjectMocks NoteService noteService;

	@Test
	void addNote_refuseNoteHorsBornes() {
		var auth = auth("prof");
		User userWithId1 = new User();
		userWithId1.setId(1L);
		userWithId1.setUsername("t1");
		userWithId1.setRole(Role.TEACHER);
		when(userRepository.findById(1L)).thenReturn(Optional.of(userWithId1));
		when(inscriptionRepository.existsActiveForTeacher(1L, 1L)).thenReturn(true);
		var req = new EntryDTO.CreateNoteRequest("Math", new BigDecimal("21"), LocalDate.now(), "x");
		assertThrows(IllegalArgumentException.class, () -> noteService.addNote(1L, req, auth));
	}

	@Test
	void addNote_ok() throws Exception {
		var auth = auth("prof");
		User userWithId1 = new User();
		userWithId1.setId(1L);
		userWithId1.setUsername("t1");
		userWithId1.setRole(Role.TEACHER);
		when(userRepository.findByUsernameIgnoreCase("t1")).thenReturn(Optional.of(userWithId1));
		when(inscriptionRepository.existsActiveForTeacher(anyLong(), anyLong())).thenReturn(true);
		when(pupilRepository.findById(1L)).thenReturn(Optional.of(new Pupil()));

		var req = new EntryDTO.CreateNoteRequest("Math", new BigDecimal("18"), LocalDate.now(), "Bien");
		when(noteRepository.save(any())).thenAnswer(inv -> {
			Note n = inv.getArgument(0);
			n.setId(99L);
			return n;
		});

		var dto = noteService.addNote(1L, req, auth);
		assertEquals(99L, dto.getId());
		assertEquals("Math", dto.getMatiere());
	}

	// helpers
	private Authentication auth(String username) {
		return new UsernamePasswordAuthenticationToken(username, "");
	}

	private User user(Role r, Long id) {
		var u = new User();
		u.setId(id);
		u.setUsername("prof");
		u.setRole(r);
		return u;
	}
}