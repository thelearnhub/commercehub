package com.thelearnhub.commercehub.auth.security;

import com.thelearnhub.commercehub.auth.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

/**
 * Issues and validates JWTs. Access tokens are short-lived; refresh tokens
 * are longer-lived and only ever exchanged for a new access token (never
 * accepted directly on business endpoints).
 *
 * Secret comes from the JWT_SECRET env var — never hardcode it, never commit
 * a real value. See application.yml.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-token-ttl-seconds:900}") long accessTokenTtlSeconds,
            @Value("${security.jwt.refresh-token-ttl-seconds:604800}") long refreshTokenTtlSeconds
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessTokenTtlSeconds, "access");
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTokenTtlSeconds, "refresh");
    }

    public long accessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }

    private String buildToken(User user, long ttlSeconds, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlSeconds * 1000);

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .claim("type", tokenType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    public Optional<Claims> parseClaims(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("type", String.class));
    }
}
