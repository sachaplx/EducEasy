package com.educeasy.core.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NoteInfo {

	private Long id;

	private Long pupilId;
	
	private String matiere;

	private BigDecimal note;

	private String commentaire;

	private LocalDate date;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMatiere() {
		return matiere;
	}

	public void setMatiere(String matiere) {
		this.matiere = matiere;
	}

	public BigDecimal getNote() {
		return note;
	}

	public void setNote(BigDecimal note) {
		this.note = note;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Long getPupilId() {
		return pupilId;
	}

	public void setPupilId(Long pupilId) {
		this.pupilId = pupilId;
	}
}
