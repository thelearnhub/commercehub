package com.thelearnhub.commercehub.inventory.repository;

import com.thelearnhub.commercehub.inventory.domain.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, UUID> {
    List<InventoryAuditLog> findBySkuOrderByCreatedAtDesc(String sku);
}
