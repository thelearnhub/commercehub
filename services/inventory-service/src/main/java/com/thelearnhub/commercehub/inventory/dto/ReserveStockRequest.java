package com.thelearnhub.commercehub.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReserveStockRequest(
        @NotBlank String sku,
        @Min(1) int quantity,
        @NotBlank String referenceId, // orderId or cartId
        String reason
) {
}
