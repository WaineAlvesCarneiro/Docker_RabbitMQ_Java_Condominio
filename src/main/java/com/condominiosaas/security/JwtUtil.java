package com.condominiosaas.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Intentionally use javax.crypto.SecretKey; jakarta has no replacement for SecretKey.
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

	private final SecretKey key;
	private final String issuer;
	private final String audience;
	private final int expireMinutes;

	public JwtUtil(@Value("${app.jwt.key}") String secret,
				   @Value("${app.jwt.issuer}") String issuer,
				   @Value("${app.jwt.audience}") String audience,
				   @Value("${app.jwt.expireMinutes}") int expireMinutes) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes());
		this.issuer = issuer;
		this.audience = audience;
		this.expireMinutes = expireMinutes;
	}

	public String generateToken(String subject, Map<String, Object> claims) {
		Date now = new Date();
		Date exp = new Date(now.getTime() + expireMinutes * 60L * 1000L);

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuer(issuer)
				.setAudience(audience)
				.setIssuedAt(now)
				.setExpiration(exp)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}
}
