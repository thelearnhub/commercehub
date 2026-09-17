package com.thelearnhub.commercehub.cart.service;

import com.thelearnhub.commercehub.cart.domain.Cart;
import com.thelearnhub.commercehub.cart.domain.CartItem;
import com.thelearnhub.commercehub.cart.dto.*;
import com.thelearnhub.commercehub.cart.mapper.CartMapper;
import com.thelearnhub.commercehub.cart.repository.RedisCartRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final RedisCartRepository cartRepository;

    public CartServiceImpl(RedisCartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    private String resolveEffectiveCartId(String cartId, String userEmail) {
        if (userEmail != null && !userEmail.isBlank()) {
            return "user:" + userEmail;
        }
        if (cartId != null && !cartId.isBlank()) {
            return "guest:" + cartId;
        }
        throw new IllegalArgumentException("Cart identifier or authenticated user email is required");
    }

    private Cart getOrCreateCart(String effectiveCartId, String userEmail) {
        return cartRepository.findById(effectiveCartId)
                .orElseGet(() -> new Cart(effectiveCartId, userEmail));
    }

    @Override
    public CartResponse getCart(String cartId, String userEmail) {
        String effectiveId = resolveEffectiveCartId(cartId, userEmail);
        Cart cart = getOrCreateCart(effectiveId, userEmail);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse addItem(String cartId, String userEmail, AddCartItemRequest request) {
        String effectiveId = resolveEffectiveCartId(cartId, userEmail);
        Cart cart = getOrCreateCart(effectiveId, userEmail);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getSku().equals(request.sku()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
            item.setUnitPrice(request.unitPrice());
        } else {
            CartItem newItem = new CartItem(
                    request.sku(), request.name(), request.unitPrice(),
                    request.quantity(), request.imageUrl()
            );
            cart.getItems().add(newItem);
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse updateItemQuantity(String cartId, String userEmail, String sku, UpdateCartItemRequest request) {
        String effectiveId = resolveEffectiveCartId(cartId, userEmail);
        Cart cart = getOrCreateCart(effectiveId, userEmail);

        if (request.quantity() <= 0) {
            cart.getItems().removeIf(item -> item.getSku().equals(sku));
        } else {
            cart.getItems().stream()
                    .filter(item -> item.getSku().equals(sku))
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(request.quantity()));
        }

        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public CartResponse removeItem(String cartId, String userEmail, String sku) {
        String effectiveId = resolveEffectiveCartId(cartId, userEmail);
        Cart cart = getOrCreateCart(effectiveId, userEmail);

        cart.getItems().removeIf(item -> item.getSku().equals(sku));
        cart.setUpdatedAt(Instant.now());
        cartRepository.save(cart);
        return CartMapper.toResponse(cart);
    }

    @Override
    public void clearCart(String cartId, String userEmail) {
        String effectiveId = resolveEffectiveCartId(cartId, userEmail);
        cartRepository.deleteById(effectiveId);
    }

    @Override
    public CartResponse mergeCart(String guestCartId, String userEmail, MergeCartRequest request) {
        String guestEffectiveId = "guest:" + request.guestCartId();
        String userEffectiveId = "user:" + userEmail;

        Optional<Cart> guestCartOpt = cartRepository.findById(guestEffectiveId);
        Cart userCart = getOrCreateCart(userEffectiveId, userEmail);

        if (guestCartOpt.isPresent()) {
            Cart guestCart = guestCartOpt.get();
            for (CartItem guestItem : guestCart.getItems()) {
                Optional<CartItem> userItemOpt = userCart.getItems().stream()
                        .filter(i -> i.getSku().equals(guestItem.getSku()))
                        .findFirst();

                if (userItemOpt.isPresent()) {
                    CartItem userItem = userItemOpt.get();
                    userItem.setQuantity(userItem.getQuantity() + guestItem.getQuantity());
                } else {
                    userCart.getItems().add(guestItem);
                }
            }
            cartRepository.deleteById(guestEffectiveId);
        }

        userCart.setUpdatedAt(Instant.now());
        cartRepository.save(userCart);
        return CartMapper.toResponse(userCart);
    }
}
