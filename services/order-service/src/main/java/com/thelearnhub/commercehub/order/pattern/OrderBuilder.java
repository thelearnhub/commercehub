package com.thelearnhub.commercehub.order.pattern;

import com.thelearnhub.commercehub.order.domain.Order;
import com.thelearnhub.commercehub.order.domain.OrderItem;
import com.thelearnhub.commercehub.order.domain.OrderState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderBuilder {

    private String userEmail;
    private OrderState status = OrderState.CREATED;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private String shippingAddress;
    private String paymentMethod;
    private final List<OrderItem> items = new ArrayList<>();

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    public OrderBuilder userEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public OrderBuilder status(OrderState status) {
        this.status = status;
        return this;
    }

    public OrderBuilder totalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    public OrderBuilder shippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
        return this;
    }

    public OrderBuilder paymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderBuilder addItem(String sku, String productName, BigDecimal unitPrice, int quantity) {
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        OrderItem item = new OrderItem(sku, productName, unitPrice, quantity, totalPrice);
        this.items.add(item);
        return this;
    }

    public OrderBuilder addItem(OrderItem item) {
        if (item != null) {
            this.items.add(item);
        }
        return this;
    }

    public OrderBuilder items(List<OrderItem> items) {
        if (items != null) {
            this.items.addAll(items);
        }
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setUserEmail(this.userEmail);
        order.setStatus(this.status);
        order.setTotalAmount(this.totalAmount);
        order.setShippingAddress(this.shippingAddress);
        order.setPaymentMethod(this.paymentMethod);
        for (OrderItem item : this.items) {
            order.addItem(item);
        }
        return order;
    }
}
