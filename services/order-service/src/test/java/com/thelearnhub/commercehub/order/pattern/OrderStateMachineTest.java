package com.thelearnhub.commercehub.order.pattern;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.exception.InvalidOrderStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStateMachineTest {

    private OrderStateMachine stateMachine;
    private Order order;

    @BeforeEach
    void setUp() {
        stateMachine = new OrderStateMachine();
        order = new Order();
    }

    @Test
    @DisplayName("Should transition from CREATED to PENDING_PAYMENT successfully")
    void testCreatedToPendingPayment() {
        order.setStatus(OrderState.CREATED);
        stateMachine.transition(order, OrderState.PENDING_PAYMENT);
        assertThat(order.getStatus()).isEqualTo(OrderState.PENDING_PAYMENT);
    }

    @Test
    @DisplayName("Should transition from CREATED to CANCELLED successfully")
    void testCreatedToCancelled() {
        order.setStatus(OrderState.CREATED);
        stateMachine.transition(order, OrderState.CANCELLED);
        assertThat(order.getStatus()).isEqualTo(OrderState.CANCELLED);
    }

    @Test
    @DisplayName("Should transition from PENDING_PAYMENT to PAID successfully")
    void testPendingPaymentToPaid() {
        order.setStatus(OrderState.PENDING_PAYMENT);
        stateMachine.transition(order, OrderState.PAID);
        assertThat(order.getStatus()).isEqualTo(OrderState.PAID);
    }

    @Test
    @DisplayName("Should transition from PENDING_PAYMENT to CANCELLED successfully")
    void testPendingPaymentToCancelled() {
        order.setStatus(OrderState.PENDING_PAYMENT);
        stateMachine.transition(order, OrderState.CANCELLED);
        assertThat(order.getStatus()).isEqualTo(OrderState.CANCELLED);
    }

    @Test
    @DisplayName("Should transition from PAID to SHIPPED successfully")
    void testPaidToShipped() {
        order.setStatus(OrderState.PAID);
        stateMachine.transition(order, OrderState.SHIPPED);
        assertThat(order.getStatus()).isEqualTo(OrderState.SHIPPED);
    }

    @Test
    @DisplayName("Should transition from PAID to CANCELLED successfully")
    void testPaidToCancelled() {
        order.setStatus(OrderState.PAID);
        stateMachine.transition(order, OrderState.CANCELLED);
        assertThat(order.getStatus()).isEqualTo(OrderState.CANCELLED);
    }

    @Test
    @DisplayName("Should transition from SHIPPED to DELIVERED successfully")
    void testShippedToDelivered() {
        order.setStatus(OrderState.SHIPPED);
        stateMachine.transition(order, OrderState.DELIVERED);
        assertThat(order.getStatus()).isEqualTo(OrderState.DELIVERED);
    }

    @ParameterizedTest
    @CsvSource({
            "CREATED, SHIPPED",
            "CREATED, DELIVERED",
            "PENDING_PAYMENT, DELIVERED",
            "PENDING_PAYMENT, CREATED",
            "PAID, CREATED",
            "PAID, PENDING_PAYMENT",
            "SHIPPED, CANCELLED",
            "SHIPPED, CREATED",
            "DELIVERED, CANCELLED",
            "DELIVERED, PAID",
            "CANCELLED, PAID",
            "CANCELLED, CREATED"
    })
    @DisplayName("Should throw InvalidOrderStateTransitionException for invalid transitions")
    void testInvalidTransitions(OrderState current, OrderState target) {
        order.setStatus(current);
        assertThatThrownBy(() -> stateMachine.transition(order, target))
                .isInstanceOf(InvalidOrderStateTransitionException.class)
                .hasMessageContaining("Invalid transition from " + current + " to " + target);
    }

    @Test
    @DisplayName("canTransition should return correct boolean for state checks")
    void testCanTransition() {
        assertThat(stateMachine.canTransition(OrderState.CREATED, OrderState.PENDING_PAYMENT)).isTrue();
        assertThat(stateMachine.canTransition(OrderState.CREATED, OrderState.DELIVERED)).isFalse();
        assertThat(stateMachine.canTransition(OrderState.DELIVERED, OrderState.CANCELLED)).isFalse();
        assertThat(stateMachine.canTransition(null, OrderState.PAID)).isFalse();
    }
}
