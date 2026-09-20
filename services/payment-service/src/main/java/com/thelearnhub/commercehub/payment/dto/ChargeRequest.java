package com.thelearnhub.commercehub.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ChargeRequest(
        @NotBlank(message = "orderId is required")
        String orderId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than zero")
        BigDecimal amount,

        String currency,

        @NotBlank(message = "provider is required")
        String provider,

        @NotBlank(message = "paymentMethod is required")
        String paymentMethod
) {
}
