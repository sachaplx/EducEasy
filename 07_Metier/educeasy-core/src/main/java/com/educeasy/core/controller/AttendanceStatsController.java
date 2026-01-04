package com.educeasy.core.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.CustomUserDetails;
import com.educeasy.core.dto.PresenceRateResponse;
import com.educeasy.core.service.AttendanceStatsService;

@RestController
@RequestMapping("/attendance")
public class AttendanceStatsController {

	private final AttendanceStatsService statsService;

	@GetMapping("/presence-rate")
	public PresenceRateResponse getPresenceRate(Authentication auth, @RequestParam(required = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	LocalDate from, @RequestParam(required = false)
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	LocalDate to) {
		CustomUserDetails me = (CustomUserDetails) auth.getPrincipal();
		return statsService.getNetworkPresenceRate(me.getId(), from, to);
	}

	public AttendanceStatsController(AttendanceStatsService statsService) {
		this.statsService = statsService;
	}
}
