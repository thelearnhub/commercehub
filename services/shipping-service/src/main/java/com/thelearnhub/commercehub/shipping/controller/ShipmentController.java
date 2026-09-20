package com.thelearnhub.commercehub.shipping.controller;

import com.thelearnhub.commercehub.shipping.dto.CreateShipmentRequest;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.UpdateStatusRequest;
import com.thelearnhub.commercehub.shipping.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
@Tag(name = "Shipping", description = "Shipping catalog, label creation, tracking, and status transitions")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping("/shipments")
    @Operation(summary = "Create shipping label", description = "Generates a new shipping label and tracking number for an order")
    public ResponseEntity<ShipmentResponse> createShipment(@Valid @RequestBody CreateShipmentRequest request) {
        ShipmentResponse response = shipmentService.createShipment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/shipments/order/{orderId}")
    @Operation(summary = "Get shipment by order ID", description = "Retrieves shipment details using the order ID")
    public ResponseEntity<ShipmentResponse> getShipmentByOrderId(@PathVariable String orderId) {
        ShipmentResponse response = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/track/{trackingNumber}")
    @Operation(summary = "Track shipment by tracking number", description = "Retrieves tracking history and current status using tracking number")
    public ResponseEntity<ShipmentResponse> trackShipment(@PathVariable String trackingNumber) {
        ShipmentResponse response = shipmentService.getShipmentByTrackingNumber(trackingNumber);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/shipments/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARRIER')")
    @Operation(summary = "Update shipment status", description = "Updates shipment status. Restricted to ADMIN or CARRIER roles.")
    public ResponseEntity<ShipmentResponse> updateShipmentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        ShipmentResponse response = shipmentService.updateShipmentStatus(id, request);
        return ResponseEntity.ok(response);
    }
}
