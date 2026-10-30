package com.serviceplus.metadata.service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class ServiceRequestService {

	@Value("${service.secret.key}")
	private String secretKey;

	@Value("${service.expiration.time}")
	private long serviceExpiration;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String generateCheckSum(Integer serviceId) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("serviceId", serviceId);
		return generateChecksum(claims,serviceId);
	}

	public Integer extractClaimNode(String token) {
		Claims claim = Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
		return (Integer) claim.get("serviceId");
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateChecksum(Map<String, Object> extraClaims,Integer serviceId) {
		return buildToken(extraClaims, serviceId);
	}

	private String buildToken(Map<String, Object> extraClaims,Integer serviceId) {
		return Jwts.builder().setClaims(extraClaims).setSubject(serviceId.toString())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+serviceExpiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}

	public boolean isChecksumValid(String token,Integer serviceId) {
		final Integer sessionServiceId = extractClaimNode(token);
		return sessionServiceId.equals(serviceId) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}

