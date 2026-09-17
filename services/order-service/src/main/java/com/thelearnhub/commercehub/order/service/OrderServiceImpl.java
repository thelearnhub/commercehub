package com.thelearnhub.commercehub.order.service;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.dto.CheckoutRequest;
import com.thelearnhub.commercehub.order.dto.OrderResponse;
import com.thelearnhub.commercehub.order.exception.OrderNotFoundException;
import com.thelearnhub.commercehub.order.facade.CheckoutFacade;
import com.thelearnhub.commercehub.order.mapper.OrderMapper;
import com.thelearnhub.commercehub.order.pattern.OrderStateMachine;
import com.thelearnhub.commercehub.order.repository.OrderRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final CheckoutFacade checkoutFacade;
    private final OrderRepository orderRepository;
    private final OrderStateMachine orderStateMachine;

    public OrderServiceImpl(CheckoutFacade checkoutFacade,
                            OrderRepository orderRepository,
                            OrderStateMachine orderStateMachine) {
        this.checkoutFacade = checkoutFacade;
        this.orderRepository = orderRepository;
        this.orderStateMachine = orderStateMachine;
    }

    @Override
    public OrderResponse checkout(String userEmail, CheckoutRequest request) {
        return checkoutFacade.checkout(userEmail, request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String userEmail) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream()
                .map(OrderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (!isAdmin && !order.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("Not authorized to access order with id: " + id);
        }

        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID id, String userEmail, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        if (!isAdmin && !order.getUserEmail().equalsIgnoreCase(userEmail)) {
            throw new AccessDeniedException("Not authorized to cancel order with id: " + id);
        }

        orderStateMachine.transition(order, OrderState.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(updatedOrder);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID id, OrderState newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + id));

        orderStateMachine.transition(order, newStatus);
        Order updatedOrder = orderRepository.save(order);
        return OrderMapper.toResponse(updatedOrder);
    }
}
