package com.bookshop.security;

import com.bookshop.config.JwtProperties;
import com.bookshop.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    /**
     * Generate signing key.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Generate JWT Token.
     */
    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("role", user.getRole().name());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + jwtProperties.getExpiration()
                        )
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract Email.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract Role.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Validate Token.
     */
    public boolean isTokenValid(String token, User user) {

        String email = extractEmail(token);

        return email.equals(user.getEmail())
                && !isTokenExpired(token);
    }

    /**
     * Check Expiration.
     */
    private boolean isTokenExpired(String token) {

        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    /**
     * Extract One Claim.
     */
    private <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    /**
     * Read All Claims.
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}