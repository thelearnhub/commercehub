package com.thelearnhub.commercehub.payment.factory;

import com.thelearnhub.commercehub.payment.adapter.MockPaymentAdapter;
import com.thelearnhub.commercehub.payment.adapter.PayPalPaymentAdapter;
import com.thelearnhub.commercehub.payment.adapter.PaymentProviderAdapter;
import com.thelearnhub.commercehub.payment.adapter.StripePaymentAdapter;
import com.thelearnhub.commercehub.payment.domain.PaymentProvider;
import com.thelearnhub.commercehub.payment.domain.PaymentStatus;
import com.thelearnhub.commercehub.payment.dto.PaymentResult;
import com.thelearnhub.commercehub.payment.exception.InvalidPaymentProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentFactoryTest {

    private PaymentProviderFactory factory;

    @BeforeEach
    void setUp() {
        List<PaymentProviderAdapter> adapters = List.of(
                new StripePaymentAdapter(),
                new PayPalPaymentAdapter(),
                new MockPaymentAdapter()
        );
        factory = new PaymentProviderFactory(adapters);
    }

    @Test
    @DisplayName("Should resolve Stripe adapter correctly by Enum and String")
    void testResolveStripeAdapter() {
        PaymentProviderAdapter enumAdapter = factory.getAdapter(PaymentProvider.STRIPE);
        assertNotNull(enumAdapter);
        assertEquals(PaymentProvider.STRIPE, enumAdapter.getProvider());
        assertInstanceOf(StripePaymentAdapter.class, enumAdapter);

        PaymentProviderAdapter stringAdapter = factory.getAdapter("STRIPE");
        assertNotNull(stringAdapter);
        assertInstanceOf(StripePaymentAdapter.class, stringAdapter);

        PaymentProviderAdapter lowerCaseAdapter = factory.getAdapter("stripe");
        assertNotNull(lowerCaseAdapter);
        assertInstanceOf(StripePaymentAdapter.class, lowerCaseAdapter);
    }

    @Test
    @DisplayName("Should resolve PayPal adapter correctly by Enum and String")
    void testResolvePayPalAdapter() {
        PaymentProviderAdapter enumAdapter = factory.getAdapter(PaymentProvider.PAYPAL);
        assertNotNull(enumAdapter);
        assertEquals(PaymentProvider.PAYPAL, enumAdapter.getProvider());
        assertInstanceOf(PayPalPaymentAdapter.class, enumAdapter);

        PaymentProviderAdapter stringAdapter = factory.getAdapter("PAYPAL");
        assertNotNull(stringAdapter);
        assertInstanceOf(PayPalPaymentAdapter.class, stringAdapter);
    }

    @Test
    @DisplayName("Should resolve Mock adapter correctly by Enum and String")
    void testResolveMockAdapter() {
        PaymentProviderAdapter enumAdapter = factory.getAdapter(PaymentProvider.MOCK);
        assertNotNull(enumAdapter);
        assertEquals(PaymentProvider.MOCK, enumAdapter.getProvider());
        assertInstanceOf(MockPaymentAdapter.class, enumAdapter);

        PaymentProviderAdapter stringAdapter = factory.getAdapter("MOCK");
        assertNotNull(stringAdapter);
        assertInstanceOf(MockPaymentAdapter.class, stringAdapter);
    }

    @Test
    @DisplayName("Should process payment through resolved adapter")
    void testAdapterExecution() {
        PaymentProviderAdapter adapter = factory.getAdapter("STRIPE");
        PaymentResult result = adapter.processPayment(new BigDecimal("99.99"), "USD", "CREDIT_CARD");

        assertTrue(result.success());
        assertEquals(PaymentStatus.SUCCESS, result.status());
        assertNotNull(result.transactionId());
        assertTrue(result.transactionId().startsWith("ch_stripe_"));
    }

    @Test
    @DisplayName("Should throw InvalidPaymentProviderException for unknown provider string")
    void testUnknownProviderString() {
        InvalidPaymentProviderException ex = assertThrows(
                InvalidPaymentProviderException.class,
                () -> factory.getAdapter("CRYPTO")
        );
        assertTrue(ex.getMessage().contains("Invalid payment provider"));
    }

    @Test
    @DisplayName("Should throw InvalidPaymentProviderException for null or blank provider")
    void testNullOrBlankProvider() {
        assertThrows(InvalidPaymentProviderException.class, () -> factory.getAdapter((String) null));
        assertThrows(InvalidPaymentProviderException.class, () -> factory.getAdapter("   "));
    }
}
