package com.thelearnhub.commercehub.order.dto;

import com.thelearnhub.commercehub.order.domain.OrderState;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        String userEmail,
        OrderState status,
        BigDecimal totalAmount,
        String shippingAddress,
        String paymentMethod,
        List<OrderItemResponse> items,
        Instant createdAt,
        Instant updatedAt
) {
}
