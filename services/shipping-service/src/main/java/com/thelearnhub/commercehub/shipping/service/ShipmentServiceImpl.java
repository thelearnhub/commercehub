package com.thelearnhub.commercehub.shipping.service;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import com.thelearnhub.commercehub.shipping.domain.ShipmentStatusHistory;
import com.thelearnhub.commercehub.shipping.dto.CreateShipmentRequest;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.UpdateStatusRequest;
import com.thelearnhub.commercehub.shipping.exception.ShipmentAlreadyExistsException;
import com.thelearnhub.commercehub.shipping.exception.ShipmentNotFoundException;
import com.thelearnhub.commercehub.shipping.mapper.ShipmentMapper;
import com.thelearnhub.commercehub.shipping.observer.ShipmentSubject;
import com.thelearnhub.commercehub.shipping.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentSubject shipmentSubject;

    public ShipmentServiceImpl(ShipmentRepository shipmentRepository,
                               ShipmentSubject shipmentSubject) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentSubject = shipmentSubject;
    }

    @Override
    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        if (shipmentRepository.existsByOrderId(request.orderId())) {
            throw new ShipmentAlreadyExistsException("Shipment already exists for orderId: " + request.orderId());
        }

        String trackingNumber = generateTrackingNumber();

        Shipment shipment = new Shipment(
                request.orderId(),
                trackingNumber,
                request.carrier(),
                ShipmentState.LABEL_CREATED,
                request.recipientName(),
                request.shippingAddress(),
                request.estimatedDeliveryDate()
        );

        ShipmentStatusHistory initialHistory = new ShipmentStatusHistory(
                shipment,
                ShipmentState.LABEL_CREATED,
                "Shipping label created"
        );
        shipment.addStatusHistory(initialHistory);

        Shipment savedShipment = shipmentRepository.save(shipment);
        return ShipmentMapper.toResponse(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByOrderId(String orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for orderId: " + orderId));
        return ShipmentMapper.toResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for trackingNumber: " + trackingNumber));
        return ShipmentMapper.toResponse(shipment);
    }

    @Override
    public ShipmentResponse updateShipmentStatus(Long id, UpdateStatusRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));

        ShipmentState previousState = shipment.getStatus();
        ShipmentState newState = request.status();

        // State Pattern: Validate transition rules
        previousState.validateTransition(newState);

        shipment.setStatus(newState);
        ShipmentStatusHistory history = new ShipmentStatusHistory(
                shipment,
                newState,
                request.comment() != null ? request.comment() : "Status updated to " + newState
        );
        shipment.addStatusHistory(history);

        Shipment updatedShipment = shipmentRepository.save(shipment);

        // Observer Pattern: Notify subscribers
        shipmentSubject.notifyObservers(updatedShipment, previousState, newState);

        return ShipmentMapper.toResponse(updatedShipment);
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
