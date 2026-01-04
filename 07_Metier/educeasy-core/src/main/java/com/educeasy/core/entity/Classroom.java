package com.educeasy.core.entity;

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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "educeasy_classe", indexes = @Index(name = "idx_classe_ecole", columnList = "ecole_id"), uniqueConstraints = @UniqueConstraint(name = "uk_classe_unique", columnNames = { "ecole_id", "nom", "annee_scolaire" }))
public class Classroom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ecole_id")
	private School school;

	@Column(nullable = false, length = 50)
	private String nom;

	private String niveau;

	@Column(name = "annee_scolaire", nullable = false, length = 9)
	private String anneeScolaire;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "maitre_id")
	private Professor maitre;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNiveau() {
		return niveau;
	}

	public void setNiveau(String niveau) {
		this.niveau = niveau;
	}

	public String getAnneeScolaire() {
		return anneeScolaire;
	}

	public void setAnneeScolaire(String anneeScolaire) {
		this.anneeScolaire = anneeScolaire;
	}

	public Professor getMaitre() {
		return maitre;
	}

	public void setMaitre(Professor maitre) {
		this.maitre = maitre;
	}
}
