package com.educeasy.core.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.AbsenceInfo;
import com.educeasy.core.dto.EntryDTO;
import com.educeasy.core.dto.NoteInfo;
import com.educeasy.core.dto.PupilInfo;
import com.educeasy.core.dto.RemarqueInfo;
import com.educeasy.core.entity.RemarkLevel;
import com.educeasy.core.service.AbsenceService;
import com.educeasy.core.service.NoteService;
import com.educeasy.core.service.PupilService;
import com.educeasy.core.service.RemarqueService;

@RestController
@RequestMapping("/pupils")
@PreAuthorize("hasAnyRole('PRINCIPAL','TEACHER')")
public class PupilController {

	private final PupilService pupilService;

	private final NoteService noteService;

	private final AbsenceService absenceService;

	private final RemarqueService remarqueService;

	@GetMapping("/search")
	public ResponseEntity<List<PupilInfo>> search(@RequestParam(name = "query", required = false) String query, Authentication auth) {
		return ResponseEntity.ok(pupilService.searchVisible(auth.getName(), query));
	}

	@GetMapping("{pupilId}")
	public ResponseEntity<PupilInfo> pupil(@PathVariable Long pupilId, Authentication auth) {
		return ResponseEntity.ok(pupilService.getIfVisible(auth.getName(), pupilId));
	}

	@GetMapping("/{pupilId}/grades")
	public List<NoteInfo> grades(@PathVariable Long pupilId, @RequestParam(required = false) String matiere, @RequestParam(required = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
		return noteService.listForPupil(pupilId, matiere, from, to);
	}

	@PostMapping("/{pupilId}/grades/add")
	@PreAuthorize("hasAnyRole('TEACHER', 'PRINCIPAL')")
	public ResponseEntity<NoteInfo> addGrade(@PathVariable Long pupilId, @RequestBody EntryDTO.CreateNoteRequest req, Authentication auth) throws Exception {
		return ResponseEntity.ok(noteService.addNote(pupilId, req, auth));
	}

	@DeleteMapping("/{pupilId}/grades/{noteId}")
	public ResponseEntity<Void> deleteNote(@PathVariable Long pupilId, @PathVariable Long noteId, Authentication auth) throws Exception {
		noteService.deleteNote(pupilId, noteId, auth);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{pupilId}/absences")
	public List<AbsenceInfo> absences(@PathVariable Long pupilId, @RequestParam(required = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to, @RequestParam(required = false) Integer halfDay, @RequestParam(required = false) Boolean justifie) {
		return absenceService.listForPupil(pupilId, from, to, halfDay, justifie);
	}

	@PostMapping("/{pupilId}/absences/add")
	@PreAuthorize("hasAnyRole('TEACHER', 'PRINCIPAL')")
	public ResponseEntity<AbsenceInfo> addAbsence(@PathVariable Long pupilId, @RequestBody EntryDTO.CreateAbsenceRequest req, Authentication auth) throws Exception {
		return ResponseEntity.ok(absenceService.addAbsence(pupilId, req, auth));
	}

	@DeleteMapping("/{pupilId}/absences/{absenceId}")
	public ResponseEntity<Void> deleteAbsence(@PathVariable Long pupilId, @PathVariable Long absenceId, Authentication auth) throws Exception {
		absenceService.deleteAbsence(pupilId, absenceId, auth);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{pupilId}/remarks")
	public List<RemarqueInfo> remarks(@PathVariable Long pupilId, @RequestParam(required = false) RemarkLevel type) {
		return remarqueService.listForPupil(pupilId, type);

	}

	@PostMapping("/{pupilId}/remarks/add")
	@PreAuthorize("hasAnyRole('TEACHER', 'PRINCIPAL')")
	public ResponseEntity<RemarqueInfo> addRemarks(@PathVariable Long pupilId, @RequestBody EntryDTO.CreateRemarkRequest req, Authentication auth) throws Exception {
		return ResponseEntity.ok(remarqueService.addRemark(pupilId, req, auth));
	}

	@DeleteMapping("/{pupilId}/remarks/{remarqueId}")
	public ResponseEntity<Void> deleteRemarque(@PathVariable Long pupilId, @PathVariable Long remarqueId, Authentication auth) throws Exception {
		remarqueService.deleteRemarque(pupilId, remarqueId, auth);
		return ResponseEntity.noContent().build();
	}

	public PupilController(PupilService pupil, NoteService note, AbsenceService absence, RemarqueService remarque) {
		this.pupilService = pupil;
		this.noteService = note;
		this.absenceService = absence;
		this.remarqueService = remarque;
	}
}
