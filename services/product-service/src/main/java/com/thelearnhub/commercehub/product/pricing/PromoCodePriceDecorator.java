package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Concrete Decorator: Applies fixed promo code discounts.
 */
public class PromoCodePriceDecorator extends AbstractPriceDecorator {

    public PromoCodePriceDecorator(PriceCalculator delegate) {
        super(delegate);
    }

    @Override
    public BigDecimal calculatePrice(Product product, PriceContext context) {
        BigDecimal price = super.calculatePrice(product, context);
        if (context.promoDiscountAmount() != null && context.promoDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discounted = price.subtract(context.promoDiscountAmount());
            return discounted.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : discounted.setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }
}
