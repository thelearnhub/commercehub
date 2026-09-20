package com.thelearnhub.commercehub.inventory.service;

import com.thelearnhub.commercehub.inventory.domain.Inventory;
import com.thelearnhub.commercehub.inventory.domain.InventoryAuditLog;
import com.thelearnhub.commercehub.inventory.domain.OperationType;
import com.thelearnhub.commercehub.inventory.dto.*;
import com.thelearnhub.commercehub.inventory.exception.InsufficientStockException;
import com.thelearnhub.commercehub.inventory.exception.InventoryNotFoundException;
import com.thelearnhub.commercehub.inventory.lock.RedisDistributedLock;
import com.thelearnhub.commercehub.inventory.mapper.InventoryMapper;
import com.thelearnhub.commercehub.inventory.repository.InventoryAuditLogRepository;
import com.thelearnhub.commercehub.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryAuditLogRepository auditLogRepository;
    private final RedisDistributedLock distributedLock;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryAuditLogRepository auditLogRepository,
            RedisDistributedLock distributedLock
    ) {
        this.inventoryRepository = inventoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.distributedLock = distributedLock;
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getStock(String sku) {
        Inventory inventory = inventoryRepository.findBySku(sku)
                .orElseThrow(() -> new InventoryNotFoundException(sku));
        return InventoryMapper.toResponse(inventory);
    }

    @Override
    @Transactional
    public InventoryResponse reserveStock(ReserveStockRequest request) {
        return distributedLock.executeWithLock(request.sku(), Duration.ofSeconds(5), () -> {
            Inventory inventory = inventoryRepository.findBySku(request.sku())
                    .orElseThrow(() -> new InventoryNotFoundException(request.sku()));

            if (inventory.getAvailableQuantity() < request.quantity()) {
                throw new InsufficientStockException(request.sku(), request.quantity(), inventory.getAvailableQuantity());
            }

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.quantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());
            inventoryRepository.save(inventory);

            auditLogRepository.save(new InventoryAuditLog(
                    request.sku(), OperationType.RESERVE, request.quantity(), request.referenceId(), request.reason()
            ));

            return InventoryMapper.toResponse(inventory);
        });
    }

    @Override
    @Transactional
    public InventoryResponse releaseStock(ReleaseStockRequest request) {
        return distributedLock.executeWithLock(request.sku(), Duration.ofSeconds(5), () -> {
            Inventory inventory = inventoryRepository.findBySku(request.sku())
                    .orElseThrow(() -> new InventoryNotFoundException(request.sku()));

            int releaseQty = Math.min(inventory.getReservedQuantity(), request.quantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - releaseQty);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + releaseQty);
            inventoryRepository.save(inventory);

            auditLogRepository.save(new InventoryAuditLog(
                    request.sku(), OperationType.RELEASE, releaseQty, request.referenceId(), request.reason()
            ));

            return InventoryMapper.toResponse(inventory);
        });
    }

    @Override
    @Transactional
    public InventoryResponse deductStock(DeductStockRequest request) {
        return distributedLock.executeWithLock(request.sku(), Duration.ofSeconds(5), () -> {
            Inventory inventory = inventoryRepository.findBySku(request.sku())
                    .orElseThrow(() -> new InventoryNotFoundException(request.sku()));

            int deductQty = Math.min(inventory.getReservedQuantity(), request.quantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - deductQty);
            inventory.setAllocatedQuantity(inventory.getAllocatedQuantity() + deductQty);
            inventoryRepository.save(inventory);

            auditLogRepository.save(new InventoryAuditLog(
                    request.sku(), OperationType.DEDUCT, deductQty, request.referenceId(), request.reason()
            ));

            return InventoryMapper.toResponse(inventory);
        });
    }

    @Override
    @Transactional
    public InventoryResponse replenishStock(ReplenishStockRequest request) {
        return distributedLock.executeWithLock(request.sku(), Duration.ofSeconds(5), () -> {
            Inventory inventory = inventoryRepository.findBySku(request.sku())
                    .orElseGet(() -> new Inventory(request.sku(), 0));

            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.quantity());
            inventoryRepository.save(inventory);

            auditLogRepository.save(new InventoryAuditLog(
                    request.sku(), OperationType.REPLENISH, request.quantity(), request.referenceId(), request.reason()
            ));

            return InventoryMapper.toResponse(inventory);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogs(String sku) {
        return auditLogRepository.findBySkuOrderByCreatedAtDesc(sku).stream()
                .map(InventoryMapper::toAuditResponse)
                .toList();
    }
}
