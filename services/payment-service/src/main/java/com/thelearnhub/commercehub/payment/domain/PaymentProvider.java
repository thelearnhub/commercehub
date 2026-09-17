package com.thelearnhub.commercehub.payment.domain;

public enum PaymentProvider {
    STRIPE,
    PAYPAL,
    MOCK;

    public static PaymentProvider fromString(String providerStr) {
        if (providerStr == null) {
            return MOCK;
        }
        for (PaymentProvider provider : PaymentProvider.values()) {
            if (provider.name().equalsIgnoreCase(providerStr.trim())) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unsupported payment provider: " + providerStr);
    }
}
