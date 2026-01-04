package com.educeasy.core.dto;

import java.time.LocalDate;

public record PresenceRateResponse(LocalDate from, LocalDate to, int schoolDays, long pupilCount, long absentSlots, long totalSlots, long presentSlots, Double rate) {
}
