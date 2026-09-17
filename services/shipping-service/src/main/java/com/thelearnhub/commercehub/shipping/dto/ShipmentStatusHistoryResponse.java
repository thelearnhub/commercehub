package com.thelearnhub.commercehub.shipping.dto;

import com.thelearnhub.commercehub.shipping.domain.ShipmentState;

import java.time.Instant;

public record ShipmentStatusHistoryResponse(
        Long id,
        ShipmentState status,
        String comment,
        Instant createdAt
) {
}
