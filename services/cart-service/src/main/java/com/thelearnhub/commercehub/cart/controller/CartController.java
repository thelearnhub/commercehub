package com.thelearnhub.commercehub.cart.controller;

import com.thelearnhub.commercehub.cart.dto.*;
import com.thelearnhub.commercehub.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@Tag(name = "Cart", description = "Active shopping cart sessions, items, TTL expiration, and guest cart merging.")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    private String getEmail(Authentication authentication) {
        return (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal()))
                ? authentication.getName()
                : null;
    }

    @GetMapping
    @Operation(summary = "Get current cart (authenticated user or via X-Guest-ID header)")
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication,
            @RequestHeader(name = "X-Guest-ID", required = false) String guestId
    ) {
        return ResponseEntity.ok(cartService.getCart(guestId, getEmail(authentication)));
    }

    @PostMapping("/items")
    @Operation(summary = "Add an item to the cart")
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @RequestHeader(name = "X-Guest-ID", required = false) String guestId,
            @Valid @RequestBody AddCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.addItem(guestId, getEmail(authentication), request));
    }

    @PutMapping("/items/{sku}")
    @Operation(summary = "Update item quantity (set to 0 to remove)")
    public ResponseEntity<CartResponse> updateItemQuantity(
            Authentication authentication,
            @RequestHeader(name = "X-Guest-ID", required = false) String guestId,
            @PathVariable String sku,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(cartService.updateItemQuantity(guestId, getEmail(authentication), sku, request));
    }

    @DeleteMapping("/items/{sku}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @RequestHeader(name = "X-Guest-ID", required = false) String guestId,
            @PathVariable String sku
    ) {
        return ResponseEntity.ok(cartService.removeItem(guestId, getEmail(authentication), sku));
    }

    @DeleteMapping
    @Operation(summary = "Clear the entire cart")
    public ResponseEntity<Void> clearCart(
            Authentication authentication,
            @RequestHeader(name = "X-Guest-ID", required = false) String guestId
    ) {
        cartService.clearCart(guestId, getEmail(authentication));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/merge")
    @Operation(summary = "Merge a guest cart into the authenticated user's cart upon login")
    public ResponseEntity<CartResponse> mergeCart(
            Authentication authentication,
            @Valid @RequestBody MergeCartRequest request
    ) {
        String email = getEmail(authentication);
        if (email == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(cartService.mergeCart(null, email, request));
    }
}
