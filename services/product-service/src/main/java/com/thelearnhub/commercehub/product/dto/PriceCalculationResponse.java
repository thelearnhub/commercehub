package com.thelearnhub.commercehub.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceCalculationResponse(
        UUID productId,
        String sku,
        BigDecimal basePrice,
        BigDecimal finalPrice,
        BigDecimal discountPercentage,
        BigDecimal taxPercentage,
        String promoCode,
        BigDecimal promoDiscountAmount
) {
}
