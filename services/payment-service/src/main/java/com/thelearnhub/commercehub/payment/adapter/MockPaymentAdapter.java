package com.thelearnhub.commercehub.payment.adapter;

import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.domain.PaymentStatus;
import com.thelearnhub.commercehub.payment.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class MockPaymentAdapter implements PaymentProviderAdapter {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.MOCK;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String currency, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid payment amount for Mock provider");
        }
        String txId = "mock_tx_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return PaymentResult.success(txId, PaymentStatus.SUCCESS);
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid refund amount for Mock provider");
        }
        String refundTxId = "mock_refund_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return PaymentResult.success(refundTxId, PaymentStatus.REFUNDED);
    }
}
