package com.thelearnhub.commercehub.inventory.controller;

import com.thelearnhub.commercehub.inventory.dto.*;
import com.thelearnhub.commercehub.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Stock management, atomic Redis Distributed Locks, stock reservations, and audit history.")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}")
    @Operation(summary = "Get current stock for a SKU")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getStock(sku));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve available stock (protected by Redis Distributed Lock)")
    public ResponseEntity<InventoryResponse> reserveStock(@Valid @RequestBody ReserveStockRequest request) {
        return ResponseEntity.ok(inventoryService.reserveStock(request));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock (compensation step for cancelled orders)")
    public ResponseEntity<InventoryResponse> releaseStock(@Valid @RequestBody ReleaseStockRequest request) {
        return ResponseEntity.ok(inventoryService.releaseStock(request));
    }

    @PostMapping("/deduct")
    @Operation(summary = "Confirm & deduct reserved stock (after successful payment)")
    public ResponseEntity<InventoryResponse> deductStock(@Valid @RequestBody DeductStockRequest request) {
        return ResponseEntity.ok(inventoryService.deductStock(request));
    }

    @PostMapping("/replenish")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Replenish available stock for a SKU (ADMIN or SELLER only)")
    public ResponseEntity<InventoryResponse> replenishStock(@Valid @RequestBody ReplenishStockRequest request) {
        return ResponseEntity.ok(inventoryService.replenishStock(request));
    }

    @GetMapping("/{sku}/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Get full audit history trail for a SKU (ADMIN or SELLER only)")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getAuditLogs(sku));
    }
}
