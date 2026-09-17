package com.thelearnhub.commercehub.order.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartClient {

    public record CartItemDto(
            String sku,
            String name,
            BigDecimal unitPrice,
            int quantity
    ) {}

    public record CartDto(
            String userEmail,
            List<CartItemDto> items,
            BigDecimal grandTotal
    ) {}

    public CartDto getCart(String userEmail) {
        // Simulated / placeholder cart fetch for order integration or mockable in tests
        return new CartDto(userEmail, List.of(), BigDecimal.ZERO);
    }

    public void clearCart(String userEmail) {
        // Simulated / placeholder clear cart
    }
}
