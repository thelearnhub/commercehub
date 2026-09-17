package com.thelearnhub.commercehub.order.dto;

import com.thelearnhub.commercehub.order.domain.OrderState;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        OrderState status
) {
}
