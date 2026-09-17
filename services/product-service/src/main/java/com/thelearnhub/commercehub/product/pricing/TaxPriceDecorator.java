package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator: Applies percentage sales tax to the discounted price.
 */
public class TaxPriceDecorator extends AbstractPriceDecorator {

    public TaxPriceDecorator(PriceCalculator delegate) {
        super(delegate);
    }

    @Override
    public BigDecimal calculatePrice(Product product, PriceContext context) {
        BigDecimal price = super.calculatePrice(product, context);
        if (context.taxPercentage() != null && context.taxPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multiplier = BigDecimal.ONE.add(context.taxPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            return price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
