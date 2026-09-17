package com.thelearnhub.commercehub.payment.mapper;

import com.thelearnhub.commercehub.payment.domain.Payment;
import com.thelearnhub.commercehub.payment.domain.PaymentAuditLog;
import com.thelearnhub.commercehub.payment.dto.PaymentAuditLogResponse;
import com.thelearnhub.commercehub.payment.dto.PaymentResponse;

public class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getProvider(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getIdempotencyKey(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    public static PaymentAuditLogResponse toAuditLogResponse(PaymentAuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }
        return new PaymentAuditLogResponse(
                auditLog.getId(),
                auditLog.getPaymentId(),
                auditLog.getAction(),
                auditLog.getAmount(),
                auditLog.getProvider(),
                auditLog.getDetails(),
                auditLog.getCreatedAt()
        );
    }
}
