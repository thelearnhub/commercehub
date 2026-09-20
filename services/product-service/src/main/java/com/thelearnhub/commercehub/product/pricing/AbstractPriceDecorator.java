package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;

/**
 * Base Decorator in the Decorator Pattern.
 */
public abstract class AbstractPriceDecorator implements PriceCalculator {

    protected final PriceCalculator delegate;

    public AbstractPriceDecorator(PriceCalculator delegate) {
        this.delegate = delegate;
    }

    @Override
    public BigDecimal calculatePrice(Product product, PriceContext context) {
        return delegate.calculatePrice(product, context);
    }
}
