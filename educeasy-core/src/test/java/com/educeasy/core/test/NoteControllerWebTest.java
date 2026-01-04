package com.educeasy.core.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.educeasy.core.dto.NoteInfo;
import com.educeasy.core.service.NoteService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@Import({
		NoteService.class
})
class NoteControllerWebTest {

	@Autowired MockMvc mvc;
	@MockBean NoteService noteService;

	@Test
	@WithMockUser(username = "t1", roles = {
			"TEACHER","PRINCIPAL"
	})
	void post_addGrade_ok() throws Exception {
		var payload = """
				  {"matiere":"Arts","note":12.5,"dateNote":"2025-09-18","commentaire":"RAS"}
				""";

		var returned = new NoteInfo();
		returned.setId(1L);
		returned.setMatiere("Arts");
		returned.setNote(BigDecimal.valueOf(12.5));
		returned.setDate(LocalDate.parse("2025-09-11"));
		returned.setCommentaire("RAS");
		returned.setPupilId(1L);

		when(noteService.addNote(eq(1L), any(), any())).thenReturn(returned);

		mvc.perform(post("/pupils/1/grades/add").contentType(MediaType.APPLICATION_JSON).content(payload).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.matiere").value("Arts")).andExpect(jsonPath("$.note").value(12.5));
	}

	@Test
  void post_addGrade_forbidden_whenNoAuth() throws Exception {
	    mvc.perform(
	            post("/pupils/1/grades/add")
	              .contentType(MediaType.APPLICATION_JSON)
	              .content("{\"matiere\":\"X\",\"note\":10,\"dateNote\":\"2025-09-18\"}")
	              .with(csrf())
	          )
	          .andExpect(status().isForbidden());
  }
}