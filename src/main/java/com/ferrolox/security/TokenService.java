package com.ferrolox.security;

import com.ferrolox.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class TokenService {

	private final SecretKey key;
	private final Duration expiration;

	public TokenService(String secret, Duration expiration) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiration = expiration;
	}

	public String generate(User user) {
		Instant now = Instant.now();

		return Jwts.builder()
		           .subject(String.valueOf(user.getId()))
		           .claim("username", user.getUsername())
		           .issuedAt(Date.from(now))
		           .expiration(Date.from(now.plus(expiration)))
		           .signWith(key)
		           .compact();
	}

	public int getUserId(String token) {
		Claims claims = Jwts.parser()
		                    .verifyWith(key)
		                    .build()
		                    .parseSignedClaims(token)
		                    .getPayload();

		return Integer.parseInt(claims.getSubject());
	}
}