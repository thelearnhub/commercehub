package com.thelearnhub.commercehub.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReplenishStockRequest(
        @NotBlank String sku,
        @Min(1) int quantity,
        String referenceId,
        String reason
) {
}
