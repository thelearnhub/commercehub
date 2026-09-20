package com.thelearnhub.commercehub.shipping.mapper;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentStatusHistory;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.ShipmentStatusHistoryResponse;

import java.util.Collections;
import java.util.List;

/**
 * Hand-written static mapper for converting Shipment entities and status history to DTOs.
 */
public class ShipmentMapper {

    private ShipmentMapper() {
    }

    public static ShipmentResponse toResponse(Shipment shipment) {
        if (shipment == null) {
            return null;
        }

        List<ShipmentStatusHistoryResponse> historyResponses = shipment.getStatusHistory() != null
                ? shipment.getStatusHistory().stream()
                .map(ShipmentMapper::toHistoryResponse)
                .toList()
                : Collections.emptyList();

        return new ShipmentResponse(
                shipment.getId(),
                shipment.getOrderId(),
                shipment.getTrackingNumber(),
                shipment.getCarrier(),
                shipment.getStatus(),
                shipment.getRecipientName(),
                shipment.getShippingAddress(),
                shipment.getEstimatedDeliveryDate(),
                shipment.getCreatedAt(),
                shipment.getUpdatedAt(),
                historyResponses
        );
    }

    public static ShipmentStatusHistoryResponse toHistoryResponse(ShipmentStatusHistory history) {
        if (history == null) {
            return null;
        }
        return new ShipmentStatusHistoryResponse(
                history.getId(),
                history.getStatus(),
                history.getComment(),
                history.getCreatedAt()
        );
    }
}
