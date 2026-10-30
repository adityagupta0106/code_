package com.serviceplus.metadata.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class GenericJwtService {

    private static final Logger logger = LoggerFactory.getLogger(GenericJwtService.class);

    @Value("${service.secret.key}")
    private String secretKey;

    // 3 hours expiration in milliseconds (3 * 60 * 60 * 1000 = 10,800,000 ms)
    public static final long DEFAULT_EXPIRATION_TIME_MS = 3 * 60 * 60 * 1000L;

    /**
     * Generates a generic signed JWT with arbitrary custom claims, subject, and 3 hours expiry.
     *
     * @param subject the subject (e.g., token ID, user ID, or service identifier)
     * @param claims  map of arbitrary claims
     * @return signed JWT string
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, DEFAULT_EXPIRATION_TIME_MS);
    }

    /**
     * Generates a generic signed JWT with arbitrary custom claims, subject, and custom expiration.
     */
    public String generateToken(String subject, Map<String, Object> claims, long expirationTimeMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and retrieves all claims from a JWT token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a specific claim using a resolver function.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the subject from the token.
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts an individual claim value by key.
     */
    public Object getClaim(String token, String claimKey) {
        Claims claims = extractAllClaims(token);
        return claims != null ? claims.get(claimKey) : null;
    }

    /**
     * Extracts a typed claim value by key.
     */
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String token, String claimKey, Class<T> clazz) {
        Object value = getClaim(token, claimKey);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        if (Number.class.isAssignableFrom(clazz) && value instanceof Number) {
            Number num = (Number) value;
            if (clazz.equals(Long.class)) {
                return (T) Long.valueOf(num.longValue());
            } else if (clazz.equals(Integer.class)) {
                return (T) Integer.valueOf(num.intValue());
            } else if (clazz.equals(Double.class)) {
                return (T) Double.valueOf(num.doubleValue());
            }
        }
        return clazz.cast(value);
    }

    /**
     * Validates whether a token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.error("Error validating token expiration: {}", e.getMessage());
            return true;
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
