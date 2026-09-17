package com.thelearnhub.commercehub.order.client;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryClient {

    public boolean reserveStock(List<CartClient.CartItemDto> items) {
        // Returns true if inventory stock reserved successfully
        return items != null && !items.isEmpty();
    }

    public void releaseStock(List<CartClient.CartItemDto> items) {
        // Release reserved stock on cancellation / payment failure
    }
}
