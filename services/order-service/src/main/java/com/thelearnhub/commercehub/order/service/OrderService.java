package com.thelearnhub.commercehub.order.service;

import com.thelearnhub.commercehub.order.domain.OrderState;
import com.thelearnhub.commercehub.order.dto.CheckoutRequest;
import com.thelearnhub.commercehub.order.dto.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse checkout(String userEmail, CheckoutRequest request);

    List<OrderResponse> getMyOrders(String userEmail);

    OrderResponse getOrderById(UUID id, String userEmail, boolean isAdmin);

    OrderResponse cancelOrder(UUID id, String userEmail, boolean isAdmin);

    OrderResponse updateOrderStatus(UUID id, OrderState newStatus);
}
