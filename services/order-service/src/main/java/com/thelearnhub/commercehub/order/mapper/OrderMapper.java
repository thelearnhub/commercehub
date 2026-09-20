package com.thelearnhub.commercehub.order.mapper;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderItem;
import com.thelearnhub.commercehub.order.dto.OrderItemResponse;
import com.thelearnhub.commercehub.order.dto.OrderResponse;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemResponse> itemResponses = order.getItems() == null
                ? Collections.emptyList()
                : order.getItems().stream()
                .map(OrderMapper::toItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public static OrderItemResponse toItemResponse(OrderItem item) {
        if (item == null) {
            return null;
        }

        return new OrderItemResponse(
                item.getId(),
                item.getSku(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getTotalPrice()
        );
    }
}
