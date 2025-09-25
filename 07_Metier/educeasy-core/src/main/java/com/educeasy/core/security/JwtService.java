package com.educeasy.core.security;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final Key key;
	
	private final long expirationMinutes;

	public JwtService(@Value("${app.security.jwt-secret}") String secret, @Value("${app.security.jwt-expiration-minutes}") long expirationMinutes) {
		byte[] bytes;
		try {
			bytes = Decoders.BASE64.decode(secret);
		} catch (Exception e) {
			bytes = secret.getBytes();
		}
		this.key = Keys.hmacShaKeyFor(bytes);
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
		Instant now = Instant.now();
		return Jwts.builder().setClaims(extraClaims).setSubject(user.getUsername()).setIssuedAt(Date.from(now)).setExpiration(Date.from(now.plusSeconds(expirationMinutes * 60))).signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String generateToken(UserDetails user) {
		return generateToken(user, Map.of("roles", user.getAuthorities()));
	}

	public boolean isTokenValid(String token, UserDetails user) {
		final String username = extractUsername(token);
		return username.equals(user.getUsername()) && !isExpired(token);
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return resolver.apply(claims);
	}

	private boolean isExpired(String token) {
		Date exp = extractClaim(token, Claims::getExpiration);
		return exp.before(new Date());
	}
}
