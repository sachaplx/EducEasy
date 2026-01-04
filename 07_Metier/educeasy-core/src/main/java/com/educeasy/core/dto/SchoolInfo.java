package com.educeasy.core.dto;

public class SchoolInfo {
	
	private Long id;
	
	private String city;
	
	private String nom;
	
	private Long principalId;
	
	private Long principalUserId;

	private String address;
	
	private String postalCode;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Long getPrincipalId() {
		return principalId;
	}

	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Long getPrincipalUserId() {
		return principalUserId;
	}

	public void setPrincipalUserId(Long principalUserId) {
		this.principalUserId = principalUserId;
	}
}
