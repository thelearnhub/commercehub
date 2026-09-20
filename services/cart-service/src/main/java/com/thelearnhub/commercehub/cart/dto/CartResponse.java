package com.thelearnhub.commercehub.cart.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(
        String cartId,
        String userEmail,
        List<CartItemResponse> items,
        BigDecimal grandTotal,
        Instant updatedAt
) {
}
