package com.thelearnhub.commercehub.order.facade;

import com.thelearnhub.commercehub.order.client.CartClient;
import com.thelearnhub.commercehub.order.client.InventoryClient;
import com.thelearnhub.commercehub.order.client.PaymentClient;
import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.dto.CheckoutRequest;
import com.thelearnhub.commercehub.order.dto.OrderResponse;
import com.thelearnhub.commercehub.order.exception.CheckoutException;
import com.thelearnhub.commercehub.order.mapper.OrderMapper;
import com.thelearnhub.commercehub.order.pattern.OrderBuilder;
import com.thelearnhub.commercehub.order.pattern.OrderStateMachine;
import com.thelearnhub.commercehub.order.repository.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class CheckoutFacade {

    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final OrderRepository orderRepository;
    private final OrderStateMachine orderStateMachine;

    public CheckoutFacade(CartClient cartClient,
                          InventoryClient inventoryClient,
                          PaymentClient paymentClient,
                          OrderRepository orderRepository,
                          OrderStateMachine orderStateMachine) {
        this.cartClient = cartClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
        this.orderRepository = orderRepository;
        this.orderStateMachine = orderStateMachine;
    }

    @Transactional
    public OrderResponse checkout(String userEmail, CheckoutRequest request) {
        // 1. Fetch user's cart
        CartClient.CartDto cart = cartClient.getCart(userEmail);
        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            throw new CheckoutException("Cannot process checkout: Shopping cart is empty for user " + userEmail);
        }

        // 2. Reserve stock in inventory
        boolean stockReserved = inventoryClient.reserveStock(cart.items());
        if (!stockReserved) {
            throw new CheckoutException("Stock reservation failed for order items.");
        }

        // 3. Build order domain object using OrderBuilder
        BigDecimal totalAmount = (cart.grandTotal() != null && cart.grandTotal().compareTo(BigDecimal.ZERO) > 0)
                ? cart.grandTotal()
                : computeTotal(cart);

        OrderBuilder builder = OrderBuilder.builder()
                .userEmail(userEmail)
                .shippingAddress(request.shippingAddress())
                .paymentMethod(request.paymentMethod())
                .totalAmount(totalAmount)
                .status(OrderState.CREATED);

        for (CartClient.CartItemDto item : cart.items()) {
            builder.addItem(item.sku(), item.name(), item.unitPrice(), item.quantity());
        }

        Order order = builder.build();
        Order savedOrder = orderRepository.save(order);

        // 4. Transition state CREATED -> PENDING_PAYMENT
        orderStateMachine.transition(savedOrder, OrderState.PENDING_PAYMENT);
        savedOrder = orderRepository.save(savedOrder);

        // 5. Charge payment
        boolean paymentSuccess = paymentClient.chargePayment(userEmail, totalAmount, request.paymentMethod());
        if (!paymentSuccess) {
            inventoryClient.releaseStock(cart.items());
            orderStateMachine.transition(savedOrder, OrderState.CANCELLED);
            orderRepository.save(savedOrder);
            throw new CheckoutException("Payment failed for user " + userEmail);
        }

        // 6. Transition PENDING_PAYMENT -> PAID
        orderStateMachine.transition(savedOrder, OrderState.PAID);
        savedOrder = orderRepository.save(savedOrder);

        // 7. Clear user's cart
        cartClient.clearCart(userEmail);

        return OrderMapper.toResponse(savedOrder);
    }

    private BigDecimal computeTotal(CartClient.CartDto cart) {
        return cart.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
