package com.thelearnhub.commercehub.payment.dto;

import com.thelearnhub.commercehub.payment.domain.PaymentProvider;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentAuditLogResponse(
        UUID id,
        UUID paymentId,
        String action,
        BigDecimal amount,
        PaymentProvider provider,
        String details,
        Instant createdAt
) {
}
