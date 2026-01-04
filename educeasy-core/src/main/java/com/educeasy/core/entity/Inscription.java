package com.educeasy.core.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "educeasy_inscription", indexes = { @Index(name = "idx_inscription_eleve", columnList = "eleve_id, date_sortie"), @Index(name = "idx_inscription_classe", columnList = "classe_id, date_sortie") })
public class Inscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "eleve_id")
	private Pupil pupil;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "classe_id")
	private Classroom classroom;

	@Column(name = "date_entree", nullable = false)
	private LocalDate dateEntree;

	@Column(name = "date_sortie")
	private LocalDate dateSortie;

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

	public Classroom getClassroom() {
		return classroom;
	}

	public void setClassroom(Classroom classe) {
		this.classroom = classe;
	}

	public LocalDate getDateEntree() {
		return dateEntree;
	}

	public void setDateEntree(LocalDate dateEntree) {
		this.dateEntree = dateEntree;
	}

	public LocalDate getDateSortie() {
		return dateSortie;
	}

	public void setDateSortie(LocalDate dateSortie) {
		this.dateSortie = dateSortie;
	}
}
