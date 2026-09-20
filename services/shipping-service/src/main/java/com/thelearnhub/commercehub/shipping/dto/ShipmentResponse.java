package com.thelearnhub.commercehub.shipping.dto;

import com.thelearnhub.commercehub.shipping.domain.ShipmentState;

import java.time.Instant;
import java.util.List;

public record ShipmentResponse(
        Long id,
        String orderId,
        String trackingNumber,
        String carrier,
        ShipmentState status,
        String recipientName,
        String shippingAddress,
        Instant estimatedDeliveryDate,
        Instant createdAt,
        Instant updatedAt,
        List<ShipmentStatusHistoryResponse> statusHistory
) {
}
