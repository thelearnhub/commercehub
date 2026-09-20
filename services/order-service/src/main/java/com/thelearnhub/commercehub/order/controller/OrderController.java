package com.thelearnhub.commercehub.order.controller;

import com.thelearnhub.commercehub.order.dto.CheckoutRequest;
import com.thelearnhub.commercehub.order.dto.OrderResponse;
import com.thelearnhub.commercehub.order.dto.UpdateStatusRequest;
import com.thelearnhub.commercehub.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Management", description = "Endpoints for checking out, viewing, cancelling, and updating orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Initiate checkout facade process")
    public ResponseEntity<OrderResponse> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request
    ) {
        String email = authentication.getName();
        OrderResponse response = orderService.checkout(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "List current user's orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getMyOrders(email));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details by ID")
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(orderService.getOrderById(id, email, isAdmin));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order (uses State Machine)")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable UUID id
    ) {
        String email = authentication.getName();
        boolean isAdmin = isAdmin(authentication);
        return ResponseEntity.ok(orderService.cancelOrder(id, email, isAdmin));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update order status (ADMIN only)")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.status()));
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }
}
