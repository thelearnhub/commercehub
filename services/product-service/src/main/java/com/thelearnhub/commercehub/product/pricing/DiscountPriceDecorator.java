package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator: Applies a percentage discount to the current price.
 */
public class DiscountPriceDecorator extends AbstractPriceDecorator {

    public DiscountPriceDecorator(PriceCalculator delegate) {
        super(delegate);
    }

    @Override
    public BigDecimal calculatePrice(Product product, PriceContext context) {
        BigDecimal price = super.calculatePrice(product, context);
        if (context.discountPercentage() != null && context.discountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal multiplier = BigDecimal.ONE.subtract(context.discountPercentage().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            return price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
