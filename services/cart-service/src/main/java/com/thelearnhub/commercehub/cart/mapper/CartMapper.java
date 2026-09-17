package com.thelearnhub.commercehub.cart.mapper;

import com.thelearnhub.commercehub.cart.domain.Cart;
import com.thelearnhub.commercehub.cart.domain.CartItem;
import com.thelearnhub.commercehub.cart.dto.CartItemResponse;
import com.thelearnhub.commercehub.cart.dto.CartResponse;

import java.util.Collections;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        if (item == null) return null;
        return new CartItemResponse(
                item.getSku(),
                item.getName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getTotalPrice(),
                item.getImageUrl()
        );
    }

    public static CartResponse toResponse(Cart cart) {
        if (cart == null) return null;
        List<CartItemResponse> itemResponses = cart.getItems() != null
                ? cart.getItems().stream().map(CartMapper::toItemResponse).toList()
                : Collections.emptyList();

        return new CartResponse(
                cart.getCartId(),
                cart.getUserEmail(),
                itemResponses,
                cart.getGrandTotal(),
                cart.getUpdatedAt()
        );
    }
}
