package com.educeasy.core.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;

import com.educeasy.core.dto.PresenceRateResponse;
import com.educeasy.core.repository.AbsenceRepository;
import com.educeasy.core.repository.InscriptionRepository;

@Service
public class AttendanceStatsService {

	private static final ZoneId ZONE = ZoneId.of("Europe/Paris");

	private final AbsenceRepository absenceRepository;
	private final InscriptionRepository inscriptionRepository;

	public PresenceRateResponse getNetworkPresenceRate(Long principalUserId, LocalDate from, LocalDate to) {

		LocalDate today = LocalDate.now(ZONE);
		LocalDate start = (from != null) ? from : today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate end = (to != null) ? to : today.with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		if (end.isBefore(start))
			throw new IllegalArgumentException("to must be >= from");

		int schoolDays = countWeekdays(start, end);

		long pupilCount = inscriptionRepository.countActivePupilsForPrincipalInPeriod(principalUserId, start, end);
		long absentSlots = absenceRepository.countAbsentSlotsForPrincipal(principalUserId, start, end);

		long totalSlots = pupilCount * (long) schoolDays * 2L;

		if (totalSlots == 0) {
			return new PresenceRateResponse(start, end, schoolDays, pupilCount, absentSlots, 0, 0, null);
		}

		long presentSlots = Math.max(0, totalSlots - absentSlots);
		double rate = Math.round((presentSlots * 10000.0) / totalSlots) / 100.0;

		return new PresenceRateResponse(start, end, schoolDays, pupilCount, absentSlots, totalSlots, presentSlots, rate);
	}

	private static int countWeekdays(LocalDate start, LocalDate end) {
		int days = 0;
		for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
			DayOfWeek dow = d.getDayOfWeek();
			if (dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY)
				days++;
		}
		return days;
	}

	public AttendanceStatsService(AbsenceRepository absenceRepository, InscriptionRepository inscriptionRepository) {
		this.absenceRepository = absenceRepository;
		this.inscriptionRepository = inscriptionRepository;
	}
}
