package com.thelearnhub.commercehub.user.dto;

import java.time.Instant;
import java.util.UUID;

public record AddressResponse(
        UUID id,
        String label,
        String street,
        String city,
        String state,
        String zipCode,
        String country,
        boolean isDefault,
        Instant createdAt,
        Instant updatedAt
) {
}
