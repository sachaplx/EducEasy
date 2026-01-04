package com.educeasy.core.dto;

import java.time.LocalDate;

public class AbsenceInfo {

	private Long id;

	private Long pupilId;

	private LocalDate date;

	private Integer halfDayCode;

	private String halfDayLabel;

	private boolean justifie;

	private String motif;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isJustifie() {
		return justifie;
	}

	public void setJustifie(boolean justifie) {
		this.justifie = justifie;
	}

	public String getMotif() {
		return motif;
	}

	public void setMotif(String motif) {
		this.motif = motif;
	}

	public Integer getHalfDayCode() {
		return halfDayCode;
	}

	public void setHalfDayCode(Integer halfDayCode) {
		this.halfDayCode = halfDayCode;
	}

	public String getHalfDayLabel() {
		return halfDayLabel;
	}

	public void setHalfDayLabel(String halfDayLabel) {
		this.halfDayLabel = halfDayLabel;
	}

	public Long getPupilId() {
		return pupilId;
	}

	public void setPupilId(Long pupilId) {
		this.pupilId = pupilId;
	}
}
