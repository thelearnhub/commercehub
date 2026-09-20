package com.thelearnhub.commercehub.inventory.dto;

import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        String sku,
        int availableQuantity,
        int reservedQuantity,
        int allocatedQuantity,
        Instant createdAt,
        Instant updatedAt
) {
}
