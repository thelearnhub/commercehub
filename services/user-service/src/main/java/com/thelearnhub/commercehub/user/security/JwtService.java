package com.thelearnhub.commercehub.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Optional;

/**
 * Parse-only JWT service — validates tokens issued by Auth Service but never
 * generates them. Uses the same HMAC secret so signature verification works.
 * Once {@code libs/common-security} is extracted, both Auth and User services
 * will share this class instead of each having a copy.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
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
