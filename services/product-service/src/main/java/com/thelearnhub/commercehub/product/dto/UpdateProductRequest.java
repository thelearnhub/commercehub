package com.thelearnhub.commercehub.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @NotBlank String name,
        String description,
        @NotNull @Min(0) BigDecimal basePrice,
        @Min(0) int stockQuantity,
        @NotNull UUID categoryId
) {
}
