package com.thelearnhub.commercehub.order.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    public boolean chargePayment(String userEmail, BigDecimal amount, String paymentMethod) {
        // Charge payment via payment gateway / payment-service
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
