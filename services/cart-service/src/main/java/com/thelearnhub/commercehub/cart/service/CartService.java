package com.thelearnhub.commercehub.cart.service;

import com.thelearnhub.commercehub.cart.dto.*;

public interface CartService {
    CartResponse getCart(String cartId, String userEmail);
    CartResponse addItem(String cartId, String userEmail, AddCartItemRequest request);
    CartResponse updateItemQuantity(String cartId, String userEmail, String sku, UpdateCartItemRequest request);
    CartResponse removeItem(String cartId, String userEmail, String sku);
    void clearCart(String cartId, String userEmail);
    CartResponse mergeCart(String userCartId, String userEmail, MergeCartRequest request);
}
