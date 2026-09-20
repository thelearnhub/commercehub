package com.thelearnhub.commercehub.order.domain;

public enum OrderState {
    CREATED,
    PENDING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
