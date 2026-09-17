package com.thelearnhub.commercehub.payment.adapter;

import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.dto.PaymentResult;

import java.math.BigDecimal;

public interface PaymentProviderAdapter {

    PaymentProvider getProvider();

    PaymentResult processPayment(BigDecimal amount, String currency, String paymentMethod);

    PaymentResult refundPayment(String transactionId, BigDecimal amount, String reason);
}
