package com.thelearnhub.commercehub.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiresInSeconds,
        String role
) {
}
