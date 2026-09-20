package com.thelearnhub.commercehub.inventory.service;

import com.thelearnhub.commercehub.inventory.dto.*;

import java.util.List;

public interface InventoryService {
    InventoryResponse getStock(String sku);
    InventoryResponse reserveStock(ReserveStockRequest request);
    InventoryResponse releaseStock(ReleaseStockRequest request);
    InventoryResponse deductStock(DeductStockRequest request);
    InventoryResponse replenishStock(ReplenishStockRequest request);
    List<AuditLogResponse> getAuditLogs(String sku);
}
