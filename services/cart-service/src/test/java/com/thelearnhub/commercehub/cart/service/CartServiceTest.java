package com.thelearnhub.commercehub.cart.service;

import com.thelearnhub.commercehub.cart.domain.Cart;
import com.thelearnhub.commercehub.cart.domain.CartItem;
import com.thelearnhub.commercehub.cart.dto.AddCartItemRequest;
import com.thelearnhub.commercehub.cart.dto.CartResponse;
import com.thelearnhub.commercehub.cart.dto.MergeCartRequest;
import com.thelearnhub.commercehub.cart.dto.UpdateCartItemRequest;
import com.thelearnhub.commercehub.cart.repository.RedisCartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private RedisCartRepository cartRepository;
    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(RedisCartRepository.class);
        cartService = new CartServiceImpl(cartRepository);
    }

    @Test
    void addItemToNewCartCalculatesTotalCorrectly() {
        when(cartRepository.findById(any())).thenReturn(Optional.empty());

        AddCartItemRequest request = new AddCartItemRequest(
                "SKU-MOUSE", "Wireless Mouse", new BigDecimal("25.00"), 2, "http://example.com/mouse.jpg"
        );

        CartResponse response = cartService.addItem(null, "user@example.com", request);

        assertThat(response.userEmail()).isEqualTo("user@example.com");
        assertThat(response.items()).hasSize(1);
        assertThat(response.grandTotal()).isEqualByComparingTo("50.00");

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void mergeGuestCartCombinesQuantities() {
        Cart guestCart = new Cart("guest:guest123", null);
        guestCart.getItems().add(new CartItem("SKU-MOUSE", "Wireless Mouse", new BigDecimal("25.00"), 1, null));

        Cart userCart = new Cart("user:user@example.com", "user@example.com");
        userCart.getItems().add(new CartItem("SKU-MOUSE", "Wireless Mouse", new BigDecimal("25.00"), 2, null));

        when(cartRepository.findById("guest:guest123")).thenReturn(Optional.of(guestCart));
        when(cartRepository.findById("user:user@example.com")).thenReturn(Optional.of(userCart));

        CartResponse response = cartService.mergeCart(null, "user@example.com", new MergeCartRequest("guest123"));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(3);
        assertThat(response.grandTotal()).isEqualByComparingTo("75.00");

        verify(cartRepository).deleteById("guest:guest123");
        verify(cartRepository).save(any(Cart.class));
    }
}
