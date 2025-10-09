package com.EmpTimeHub.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class JwtServiceImpl {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expirationInMs;

	private Key getKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String userName, List<String> roles,UUID deviceId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		claims.put("deviceId", deviceId.toString());
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(userName)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expirationInMs))
				.signWith(getKey(), SignatureAlgorithm.HS256)
				.compact();
	}
	public UUID extractDeviceId(String token) {
		return UUID.fromString(getAllClaims(token).get("deviceId", String.class));
	}

	public boolean validateToken(String token) {
		try {
			getAllClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}
	private Claims getAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}



public String extractUserName(String token) {
    // extract the username from jwt token
    return extractClaim(token, Claims::getSubject);
}
	public List<String> extractRoles(String token) {
		Claims claims = getAllClaims(token);
		return (List<String>) claims.get("roles");
	}


	private <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaims(token);
		return claimsResolver.apply(claims);
	}




}
