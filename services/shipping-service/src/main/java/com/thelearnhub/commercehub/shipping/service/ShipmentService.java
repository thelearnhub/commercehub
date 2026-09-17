package com.thelearnhub.commercehub.shipping.service;

import com.thelearnhub.commercehub.shipping.dto.CreateShipmentRequest;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.UpdateStatusRequest;

public interface ShipmentService {

    ShipmentResponse createShipment(CreateShipmentRequest request);

    ShipmentResponse getShipmentByOrderId(String orderId);

    ShipmentResponse getShipmentByTrackingNumber(String trackingNumber);

    ShipmentResponse updateShipmentStatus(Long id, UpdateStatusRequest request);
}
