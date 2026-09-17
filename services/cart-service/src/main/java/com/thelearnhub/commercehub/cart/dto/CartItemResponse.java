package com.thelearnhub.commercehub.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        String sku,
        String name,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal totalPrice,
        String imageUrl
) {
}
