package com.educeasy.core.dto;

import java.time.Instant;

public class RemarqueInfo {

	private Long id;

	private Long pupilId;

	private String auteurFirstName;

	private String auteurLastName;
	
	private Long auteurId;

	private String type;

	private String contenu;

	private Instant createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAuteurLastName() {
		return auteurLastName;
	}

	public void setAuteurLastName(String auteurLastName) {
		this.auteurLastName = auteurLastName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Long getPupilId() {
		return pupilId;
	}

	public void setPupilId(Long pupilId) {
		this.pupilId = pupilId;
	}

	public Long getAuteurId() {
		return auteurId;
	}

	public void setAuteurId(Long auteurId) {
		this.auteurId = auteurId;
	}

	public String getAuteurFirstName() {
		return auteurFirstName;
	}

	public void setAuteurFirstName(String auteurFirstName) {
		this.auteurFirstName = auteurFirstName;
	}
}
