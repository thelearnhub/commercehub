package com.thelearnhub.commercehub.order.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {
}
