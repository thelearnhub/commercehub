package com.thelearnhub.commercehub.payment.dto;

import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        PaymentProvider provider,
        String paymentMethod,
        PaymentStatus status,
        String transactionId,
        String idempotencyKey,
        String failureReason,
        Instant createdAt,
        Instant updatedAt
) {
}
