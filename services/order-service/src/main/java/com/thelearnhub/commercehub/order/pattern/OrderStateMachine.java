package com.thelearnhub.commercehub.order.pattern;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.exception.InvalidOrderStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class OrderStateMachine {

    private static final Map<OrderState, Set<OrderState>> ALLOWED_TRANSITIONS = new EnumMap<>(OrderState.class);

    static {
        ALLOWED_TRANSITIONS.put(OrderState.CREATED, EnumSet.of(OrderState.PENDING_PAYMENT, OrderState.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderState.PENDING_PAYMENT, EnumSet.of(OrderState.PAID, OrderState.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderState.PAID, EnumSet.of(OrderState.SHIPPED, OrderState.CANCELLED));
        ALLOWED_TRANSITIONS.put(OrderState.SHIPPED, EnumSet.of(OrderState.DELIVERED));
        ALLOWED_TRANSITIONS.put(OrderState.DELIVERED, EnumSet.noneOf(OrderState.class));
        ALLOWED_TRANSITIONS.put(OrderState.CANCELLED, EnumSet.noneOf(OrderState.class));
    }

    public boolean canTransition(OrderState current, OrderState target) {
        if (current == null || target == null) {
            return false;
        }
        Set<OrderState> validNextStates = ALLOWED_TRANSITIONS.get(current);
        return validNextStates != null && validNextStates.contains(target);
    }

    public Order transition(Order order, OrderState targetState) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        OrderState currentState = order.getStatus();
        if (!canTransition(currentState, targetState)) {
            throw new InvalidOrderStateTransitionException(
                    String.format("Invalid transition from %s to %s", currentState, targetState)
            );
        }
        order.setStatus(targetState);
        return order;
    }
}
