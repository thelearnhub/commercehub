package com.thelearnhub.commercehub.payment.service;

import com.thelearnhub.commercehub.payment.dto.ChargeRequest;
import com.thelearnhub.commercehub.payment.dto.PaymentAuditLogResponse;
import com.thelearnhub.commercehub.payment.dto.PaymentResponse;
import com.thelearnhub.commercehub.payment.dto.RefundRequest;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse charge(ChargeRequest request, String idempotencyKey, String userId);

    PaymentResponse refund(RefundRequest request, String userId);

    PaymentResponse getPaymentById(UUID id);

    PaymentResponse getPaymentByOrderId(String orderId);

    List<PaymentAuditLogResponse> getAuditLogs(UUID paymentId);
}
