package com.thelearnhub.commercehub.payment.dto;

import com.thelearnhub.commercehub.payment.domain.PaymentStatus;

public record PaymentResult(
        boolean success,
        String transactionId,
        PaymentStatus status,
        String errorMessage
) {
    public static PaymentResult success(String transactionId, PaymentStatus status) {
        return new PaymentResult(true, transactionId, status, null);
    }

    public static PaymentResult failure(String errorMessage) {
        return new PaymentResult(false, null, PaymentStatus.FAILED, errorMessage);
    }
}
