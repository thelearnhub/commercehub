package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;

/**
 * Concrete Component in the Decorator Pattern: returns the product's raw base price.
 */
public class BasePriceCalculator implements PriceCalculator {

    @Override
    public BigDecimal calculatePrice(Product product, PriceContext context) {
        return product.getBasePrice();
    }
}
