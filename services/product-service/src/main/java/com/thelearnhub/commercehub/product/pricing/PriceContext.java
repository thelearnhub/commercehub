package com.thelearnhub.commercehub.product.pricing;

import java.math.BigDecimal;

/**
 * Parameter context passed through the Decorator pricing pipeline.
 */
public record PriceContext(
        BigDecimal discountPercentage,
        BigDecimal taxPercentage,
        String promoCode,
        BigDecimal promoDiscountAmount
) {
    public static PriceContext empty() {
        return new PriceContext(BigDecimal.ZERO, BigDecimal.ZERO, null, BigDecimal.ZERO);
    }
}
