package com.thelearnhub.commercehub.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public record CreateShipmentRequest(
        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Carrier is required")
        String carrier,

        @NotBlank(message = "Recipient name is required")
        String recipientName,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        Instant estimatedDeliveryDate
) {
}
