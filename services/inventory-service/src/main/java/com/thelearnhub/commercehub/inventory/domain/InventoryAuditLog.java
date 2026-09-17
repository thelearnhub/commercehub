package com.thelearnhub.commercehub.inventory.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_audit_logs")
public class InventoryAuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 100)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 50)
    private OperationType operationType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected InventoryAuditLog() {
    }

    public InventoryAuditLog(String sku, OperationType operationType, int quantity, String referenceId, String reason) {
        this.sku = sku;
        this.operationType = operationType;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public String getReason() {
        return reason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
