package com.thelearnhub.commercehub.inventory.mapper;

import com.thelearnhub.commercehub.inventory.domain.Inventory;
import com.thelearnhub.commercehub.inventory.domain.InventoryAuditLog;
import com.thelearnhub.commercehub.inventory.dto.AuditLogResponse;
import com.thelearnhub.commercehub.inventory.dto.InventoryResponse;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryResponse toResponse(Inventory inventory) {
        if (inventory == null) return null;
        return new InventoryResponse(
                inventory.getId(),
                inventory.getSku(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getAllocatedQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }

    public static AuditLogResponse toAuditResponse(InventoryAuditLog auditLog) {
        if (auditLog == null) return null;
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getSku(),
                auditLog.getOperationType(),
                auditLog.getQuantity(),
                auditLog.getReferenceId(),
                auditLog.getReason(),
                auditLog.getCreatedAt()
        );
    }
}
