package com.educeasy.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EntryDTO {

	public record CreateNoteRequest(String matiere, BigDecimal note, LocalDate dateNote, String commentaire) {
	}
	
	public record CreateAbsenceRequest(LocalDate date, String halfDay, Boolean justifie, String motif) {
	}

	public record CreateRemarkRequest(String type, String contenu) {
	}

}
