package com.educeasy.core.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class VerificationToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	@Column(nullable = false)
	private LocalDateTime expiryDate;

	private boolean used = false;

	public static VerificationToken create(User user, String token, long minutesValid) {
		VerificationToken vt = new VerificationToken();
		vt.setToken(token);
		vt.setUser(user);
		vt.setExpiryDate(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(minutesValid));
		vt.setUsed(false);
		return vt;
	}

	public boolean isExpired() {
		return LocalDateTime.now(ZoneOffset.UTC).isAfter(expiryDate);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
}
