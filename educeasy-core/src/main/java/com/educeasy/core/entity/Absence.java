package com.educeasy.core.entity;

import java.time.LocalDate;

import com.educeasy.core.utils.HalfDayConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "educeasy_absence")
public class Absence {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eleve_id", nullable = false)
	private Pupil pupil;

	@Column(nullable = false)
	private LocalDate date;
	
	private boolean justifie = false;
	
	private String motif;

	@Convert(converter = HalfDayConverter.class)
	@Column(name = "demi_journee", nullable = false, columnDefinition = "TINYINT")
	private HalfDay halfDay;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pupil getPupil() {
		return pupil;
	}

	public void setPupil(Pupil pupil) {
		this.pupil = pupil;
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

	public HalfDay getHalfDay() {
		return halfDay;
	}

	public void setHalfDay(HalfDay halfDay) {
		this.halfDay = halfDay;
	}
}
