package com.thelearnhub.commercehub.payment.factory;

import com.thelearnhub.commercehub.payment.adapter.PaymentProviderAdapter;
import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.exception.InvalidPaymentProviderException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentProviderFactory {

    private final Map<PaymentProvider, PaymentProviderAdapter> adapterMap = new EnumMap<>(PaymentProvider.class);

    public PaymentProviderFactory(List<PaymentProviderAdapter> adapters) {
        for (PaymentProviderAdapter adapter : adapters) {
            adapterMap.put(adapter.getProvider(), adapter);
        }
    }

    public PaymentProviderAdapter getAdapter(PaymentProvider provider) {
        if (provider == null || !adapterMap.containsKey(provider)) {
            throw new InvalidPaymentProviderException("No adapter found for payment provider: " + provider);
        }
        return adapterMap.get(provider);
    }

    public PaymentProviderAdapter getAdapter(String providerStr) {
        if (providerStr == null || providerStr.isBlank()) {
            throw new InvalidPaymentProviderException("Payment provider cannot be empty");
        }
        try {
            PaymentProvider provider = PaymentProvider.fromString(providerStr);
            return getAdapter(provider);
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentProviderException("Invalid payment provider: " + providerStr);
        }
    }
}
