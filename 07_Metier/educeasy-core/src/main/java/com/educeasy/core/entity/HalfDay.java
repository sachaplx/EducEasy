package com.educeasy.core.entity;

public enum HalfDay {
	MORNING(1, "Matin"), AFTERNOON(2, "Après-midi"), FULL(3, "Journée");

	private final int code;
	private final String label;

	HalfDay(int code, String label) {
		this.code = code;
		this.label = label;
	}

	public int code() {
		return code;
	}

	public String label() {
		return label;
	}

	public static HalfDay fromCode(Integer code) {
		if (code == null) {
			return null;
		}
		for (var v : values()) {
			if (v.code == code) {
				return v;
			}
		}
		throw new IllegalArgumentException("Unknown HalfDay code: " + code);
	}
}
