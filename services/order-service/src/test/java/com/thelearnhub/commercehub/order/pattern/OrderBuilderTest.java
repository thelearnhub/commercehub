package com.thelearnhub.commercehub.order.pattern;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OrderBuilderTest {

    @Test
    @DisplayName("Should build Order object correctly using OrderBuilder")
    void testBuildOrder() {
        Order order = OrderBuilder.builder()
                .userEmail("test@example.com")
                .shippingAddress("123 Main St, Tech City")
                .paymentMethod("CREDIT_CARD")
                .totalAmount(new BigDecimal("99.99"))
                .status(OrderState.CREATED)
                .addItem("SKU-100", "Wireless Mouse", new BigDecimal("49.99"), 1)
                .addItem("SKU-200", "Mouse Pad", new BigDecimal("25.00"), 2)
                .build();

        assertThat(order.getUserEmail()).isEqualTo("test@example.com");
        assertThat(order.getShippingAddress()).isEqualTo("123 Main St, Tech City");
        assertThat(order.getPaymentMethod()).isEqualTo("CREDIT_CARD");
        assertThat(order.getTotalAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(order.getStatus()).isEqualTo(OrderState.CREATED);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getItems().get(0).getSku()).isEqualTo("SKU-100");
        assertThat(order.getItems().get(0).getTotalPrice()).isEqualTo(new BigDecimal("49.99"));
        assertThat(order.getItems().get(1).getTotalPrice()).isEqualTo(new BigDecimal("50.00"));
    }
}
