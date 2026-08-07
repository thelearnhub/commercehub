package com.thelearnhub.commercehub.auth.security;

import com.thelearnhub.commercehub.auth.domain.Role;
import com.thelearnhub.commercehub.auth.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String TEST_SECRET = "test-secret-key-that-is-long-enough-for-hs256-signing";

    private final JwtService jwtService = new JwtService(TEST_SECRET, 900, 604800);

    @Test
    void issuesAnAccessTokenThatCarriesTheUsersRole() {
        User user = new User("jane@example.com", "hashed-password", Role.CUSTOMER);

        String token = jwtService.generateAccessToken(user);
        Optional<Claims> claims = jwtService.parseClaims(token);

        assertThat(claims).isPresent();
        assertThat(claims.get().getSubject()).isEqualTo("jane@example.com");
        assertThat(claims.get().get("role", String.class)).isEqualTo("CUSTOMER");
        assertThat(jwtService.isRefreshToken(claims.get())).isFalse();
    }

    @Test
    void refreshTokensAreDistinguishableFromAccessTokens() {
        User user = new User("jane@example.com", "hashed-password", Role.CUSTOMER);

        String refreshToken = jwtService.generateRefreshToken(user);
        Claims claims = jwtService.parseClaims(refreshToken).orElseThrow();

        assertThat(jwtService.isRefreshToken(claims)).isTrue();
    }

    @Test
    void rejectsATamperedToken() {
        User user = new User("jane@example.com", "hashed-password", Role.CUSTOMER);
        String token = jwtService.generateAccessToken(user);

        String tampered = token.substring(0, token.length() - 4) + "abcd";

        assertThat(jwtService.parseClaims(tampered)).isEmpty();
    }
}
