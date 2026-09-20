package com.thelearnhub.commercehub.shipping.dto;

import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        ShipmentState status,

        String comment
) {
}
