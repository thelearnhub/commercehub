package com.thelearnhub.commercehub.shipping.service;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import com.thelearnhub.commercehub.shipping.dto.CreateShipmentRequest;
import com.thelearnhub.commercehub.shipping.dto.ShipmentResponse;
import com.thelearnhub.commercehub.shipping.dto.UpdateStatusRequest;
import com.thelearnhub.commercehub.shipping.exception.ShipmentAlreadyExistsException;
import com.thelearnhub.commercehub.shipping.exception.ShipmentNotFoundException;
import com.thelearnhub.commercehub.shipping.observer.ShipmentSubject;
import com.thelearnhub.commercehub.shipping.repository.ShipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShipmentServiceTest {

    private ShipmentRepository shipmentRepository;
    private ShipmentSubject shipmentSubject;
    private ShipmentServiceImpl shipmentService;

    @BeforeEach
    void setUp() {
        shipmentRepository = mock(ShipmentRepository.class);
        shipmentSubject = mock(ShipmentSubject.class);
        shipmentService = new ShipmentServiceImpl(shipmentRepository, shipmentSubject);
    }

    @Test
    @DisplayName("createShipment creates new shipment entity and initial history")
    void createShipmentSuccess() {
        CreateShipmentRequest request = new CreateShipmentRequest(
                "ORD-100",
                "UPS",
                "John Doe",
                "456 Oak St",
                Instant.now().plusSeconds(86400)
        );

        when(shipmentRepository.existsByOrderId("ORD-100")).thenReturn(false);
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> {
            Shipment s = invocation.getArgument(0);
            s.setId(10L);
            return s;
        });

        ShipmentResponse response = shipmentService.createShipment(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.orderId()).isEqualTo("ORD-100");
        assertThat(response.carrier()).isEqualTo("UPS");
        assertThat(response.status()).isEqualTo(ShipmentState.LABEL_CREATED);
        assertThat(response.trackingNumber()).startsWith("TRK-");

        ArgumentCaptor<Shipment> captor = ArgumentCaptor.forClass(Shipment.class);
        verify(shipmentRepository).save(captor.capture());
        assertThat(captor.getValue().getStatusHistory()).hasSize(1);
    }

    @Test
    @DisplayName("createShipment throws ShipmentAlreadyExistsException if orderId exists")
    void createShipmentDuplicateOrderId() {
        CreateShipmentRequest request = new CreateShipmentRequest(
                "ORD-100", "UPS", "John Doe", "456 Oak St", null
        );
        when(shipmentRepository.existsByOrderId("ORD-100")).thenReturn(true);

        assertThatThrownBy(() -> shipmentService.createShipment(request))
                .isInstanceOf(ShipmentAlreadyExistsException.class)
                .hasMessageContaining("Shipment already exists for orderId: ORD-100");
    }

    @Test
    @DisplayName("getShipmentByOrderId returns shipment response when found")
    void getShipmentByOrderIdFound() {
        Shipment shipment = new Shipment("ORD-100", "TRK-001", "FEDEX", ShipmentState.LABEL_CREATED, "Jane", "Addr", null);
        shipment.setId(1L);

        when(shipmentRepository.findByOrderId("ORD-100")).thenReturn(Optional.of(shipment));

        ShipmentResponse response = shipmentService.getShipmentByOrderId("ORD-100");
        assertThat(response.orderId()).isEqualTo("ORD-100");
        assertThat(response.trackingNumber()).isEqualTo("TRK-001");
    }

    @Test
    @DisplayName("getShipmentByOrderId throws ShipmentNotFoundException when missing")
    void getShipmentByOrderIdNotFound() {
        when(shipmentRepository.findByOrderId("ORD-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shipmentService.getShipmentByOrderId("ORD-999"))
                .isInstanceOf(ShipmentNotFoundException.class)
                .hasMessageContaining("Shipment not found for orderId: ORD-999");
    }

    @Test
    @DisplayName("updateShipmentStatus transitions state and notifies observers")
    void updateShipmentStatusSuccess() {
        Shipment shipment = new Shipment("ORD-100", "TRK-001", "FEDEX", ShipmentState.LABEL_CREATED, "Jane", "Addr", null);
        shipment.setId(1L);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateStatusRequest updateRequest = new UpdateStatusRequest(ShipmentState.IN_TRANSIT, "Courier picked up parcel");

        ShipmentResponse response = shipmentService.updateShipmentStatus(1L, updateRequest);

        assertThat(response.status()).isEqualTo(ShipmentState.IN_TRANSIT);
        verify(shipmentSubject).notifyObservers(eq(shipment), eq(ShipmentState.LABEL_CREATED), eq(ShipmentState.IN_TRANSIT));
    }

    @Test
    @DisplayName("updateShipmentStatus throws IllegalStateException on invalid transition")
    void updateShipmentStatusInvalidTransition() {
        Shipment shipment = new Shipment("ORD-100", "TRK-001", "FEDEX", ShipmentState.LABEL_CREATED, "Jane", "Addr", null);
        shipment.setId(1L);

        when(shipmentRepository.findById(1L)).thenReturn(Optional.of(shipment));

        UpdateStatusRequest invalidRequest = new UpdateStatusRequest(ShipmentState.DELIVERED, "Direct delivery attempt");

        assertThatThrownBy(() -> shipmentService.updateShipmentStatus(1L, invalidRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid shipment state transition from LABEL_CREATED to DELIVERED");
    }
}
