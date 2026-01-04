package com.educeasy.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.educeasy.core.dto.MyselfInfo;
import com.educeasy.core.service.MyselfService;

@RestController
public class MyselfController {

	private final MyselfService myselfService;

	@GetMapping("/whoami")
	public ResponseEntity<MyselfInfo> me(Authentication auth) {
		var body = myselfService.me(auth.getName());
		return ResponseEntity.ok().cacheControl(org.springframework.http.CacheControl.noStore()).header("Pragma", "no-cache").header("Expires", "0").body(body);
	}

	public MyselfController(MyselfService myselfService) {
		this.myselfService = myselfService;
	}
}
