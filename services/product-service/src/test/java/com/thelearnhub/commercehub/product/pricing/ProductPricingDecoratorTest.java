package com.thelearnhub.commercehub.product.pricing;

import com.thelearnhub.commercehub.product.domain.Category;
import com.thelearnhub.commercehub.product.domain.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductPricingDecoratorTest {

    @Test
    void basePriceCalculatorReturnsRawPrice() {
        Category category = new Category("Electronics", "Gadgets");
        Product product = new Product("Wireless Mouse", "SKU-MOUSE-01", "Ergonomic mouse", new BigDecimal("100.00"), 50, category);
        PriceContext context = PriceContext.empty();

        PriceCalculator calculator = new BasePriceCalculator();
        BigDecimal result = calculator.calculatePrice(product, context);

        assertThat(result).isEqualByComparingTo("100.00");
    }

    @Test
    void discountDecoratorAppliesPercentageDiscount() {
        Category category = new Category("Electronics", "Gadgets");
        Product product = new Product("Wireless Mouse", "SKU-MOUSE-01", "Ergonomic mouse", new BigDecimal("100.00"), 50, category);
        PriceContext context = new PriceContext(new BigDecimal("20.00"), BigDecimal.ZERO, null, BigDecimal.ZERO);

        PriceCalculator pipeline = new DiscountPriceDecorator(new BasePriceCalculator());
        BigDecimal result = pipeline.calculatePrice(product, context);

        assertThat(result).isEqualByComparingTo("80.00");
    }

    @Test
    void fullDecoratorPipelineAppliesDiscountPromoAndTaxInSequence() {
        Category category = new Category("Electronics", "Gadgets");
        Product product = new Product("Laptop", "SKU-LAPTOP-01", "Gaming laptop", new BigDecimal("1000.00"), 10, category);

        // 10% discount on $1000 -> $900
        // $50 promo discount on $900 -> $850
        // 10% tax on $850 -> $935.00
        PriceContext context = new PriceContext(
                new BigDecimal("10.00"),
                new BigDecimal("10.00"),
                "SAVE50",
                new BigDecimal("50.00")
        );

        PriceCalculator pipeline = new TaxPriceDecorator(
                new PromoCodePriceDecorator(
                        new DiscountPriceDecorator(
                                new BasePriceCalculator()
                        )
                )
        );

        BigDecimal finalPrice = pipeline.calculatePrice(product, context);

        assertThat(finalPrice).isEqualByComparingTo("935.00");
    }
}
