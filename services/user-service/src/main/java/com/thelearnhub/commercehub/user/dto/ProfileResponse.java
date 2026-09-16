package com.thelearnhub.commercehub.user.dto;

import java.time.Instant;
import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
