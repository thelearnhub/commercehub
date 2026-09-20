package com.thelearnhub.commercehub.cart.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {

    private String cartId;
    private String userEmail;
    private List<CartItem> items = new ArrayList<>();
    private Instant updatedAt = Instant.now();

    public Cart() {
    }

    public Cart(String cartId, String userEmail) {
        this.cartId = cartId;
        this.userEmail = userEmail;
    }

    public BigDecimal getGrandTotal() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
