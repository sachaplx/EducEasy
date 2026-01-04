package com.educeasy.core.utils;

import com.educeasy.core.entity.HalfDay;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class HalfDayConverter implements AttributeConverter<HalfDay, Integer> {

	@Override
	public Integer convertToDatabaseColumn(HalfDay halfDay) {
		return halfDay == null ? null : halfDay.code();
	}

	@Override
	public HalfDay convertToEntityAttribute(Integer dbValue) {
		return HalfDay.fromCode(dbValue);
	}
}
