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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "educeasy_inscription", indexes = { @Index(name = "idx_inscription_eleve", columnList = "eleve_id, date_sortie"), @Index(name = "idx_inscription_classe", columnList = "classe_id, date_sortie") })
public class Inscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "eleve_id")
	private Pupil eleve;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "classe_id")
	private Classroom classe;

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

	public Pupil getEleve() {
		return eleve;
	}

	public void setEleve(Pupil eleve) {
		this.eleve = eleve;
	}

	public Classroom getClasse() {
		return classe;
	}

	public void setClasse(Classroom classe) {
		this.classe = classe;
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
