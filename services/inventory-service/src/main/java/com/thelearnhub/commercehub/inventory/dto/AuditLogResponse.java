package com.thelearnhub.commercehub.inventory.dto;

import com.thelearnhub.commercehub.inventory.domain.OperationType;

import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String sku,
        OperationType operationType,
        int quantity,
        String referenceId,
        String reason,
        Instant createdAt
) {
}
