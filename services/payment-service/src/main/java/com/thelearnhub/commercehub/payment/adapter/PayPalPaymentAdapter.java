package com.thelearnhub.commercehub.payment.adapter;

import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.domain.PaymentStatus;
import com.thelearnhub.commercehub.payment.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PayPalPaymentAdapter implements PaymentProviderAdapter {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PAYPAL;
    }

    @Override
    public PaymentResult processPayment(BigDecimal amount, String currency, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid payment amount for PayPal");
        }
        String txId = "PAYID-" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 16);
        return PaymentResult.success(txId, PaymentStatus.SUCCESS);
    }

    @Override
    public PaymentResult refundPayment(String transactionId, BigDecimal amount, String reason) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Invalid refund amount for PayPal");
        }
        String refundTxId = "REFUND-" + UUID.randomUUID().toString().replace("-", "").toUpperCase().substring(0, 16);
        return PaymentResult.success(refundTxId, PaymentStatus.REFUNDED);
    }
}
