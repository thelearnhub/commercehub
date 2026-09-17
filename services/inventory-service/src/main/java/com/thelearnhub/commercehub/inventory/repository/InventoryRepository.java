package com.thelearnhub.commercehub.inventory.repository;

import com.thelearnhub.commercehub.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<Inventory> findBySku(String sku);
    boolean existsBySku(String sku);
}
