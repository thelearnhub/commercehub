package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;

/**
 * Component interface for the Decorator Pattern pricing pipeline.
 */
public interface PriceCalculator {
    BigDecimal calculatePrice(Product product, PriceContext context);
}
