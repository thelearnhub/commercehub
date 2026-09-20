package com.thelearnhub.commercehub.product.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String sku,
        String description,
        BigDecimal basePrice,
        int stockQuantity,
        CategoryResponse category,
        Instant createdAt,
        Instant updatedAt
) {
}
