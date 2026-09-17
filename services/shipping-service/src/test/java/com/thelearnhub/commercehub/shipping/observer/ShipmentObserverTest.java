package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ShipmentObserverTest {

    private ShipmentSubjectImpl subject;
    private ShipmentObserver mockObserver1;
    private ShipmentObserver mockObserver2;
    private Shipment testShipment;

    @BeforeEach
    void setUp() {
        subject = new ShipmentSubjectImpl();
        mockObserver1 = Mockito.mock(ShipmentObserver.class);
        mockObserver2 = Mockito.mock(ShipmentObserver.class);

        testShipment = new Shipment(
                "ORD-12345",
                "TRK-987654321",
                "FEDEX",
                ShipmentState.LABEL_CREATED,
                "Jane Doe",
                "123 Main St, Springfield",
                Instant.now().plusSeconds(86400)
        );
        testShipment.setId(1L);
    }

    @Test
    @DisplayName("Observer registration adds observer to subject")
    void registerObserverAddsObserver() {
        subject.registerObserver(mockObserver1);
        assertThat(subject.getObservers()).contains(mockObserver1);
    }

    @Test
    @DisplayName("Observer removal removes observer from subject")
    void removeObserverRemovesObserver() {
        subject.registerObserver(mockObserver1);
        subject.registerObserver(mockObserver2);

        subject.removeObserver(mockObserver1);

        assertThat(subject.getObservers()).doesNotContain(mockObserver1);
        assertThat(subject.getObservers()).contains(mockObserver2);
    }

    @Test
    @DisplayName("notifyObservers invokes onStatusChanged on all registered observers")
    void notifyObserversDispatchesEventToSubscribers() {
        subject.registerObserver(mockObserver1);
        subject.registerObserver(mockObserver2);

        subject.notifyObservers(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);

        verify(mockObserver1).onStatusChanged(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);
        verify(mockObserver2).onStatusChanged(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);
    }

    @Test
    @DisplayName("Removed observer does not receive notification events")
    void removedObserverDoesNotReceiveNotifications() {
        subject.registerObserver(mockObserver1);
        subject.registerObserver(mockObserver2);

        subject.removeObserver(mockObserver1);
        subject.notifyObservers(testShipment, ShipmentState.IN_TRANSIT, ShipmentState.OUT_FOR_DELIVERY);

        verify(mockObserver1, never()).onStatusChanged(Mockito.any(), Mockito.any(), Mockito.any());
        verify(mockObserver2).onStatusChanged(testShipment, ShipmentState.IN_TRANSIT, ShipmentState.OUT_FOR_DELIVERY);
    }

    @Test
    @DisplayName("Exception in one observer does not break notification to other observers")
    void exceptionInObserverIsHandledGracefully() {
        doThrow(new RuntimeException("Simulated observer exception"))
                .when(mockObserver1)
                .onStatusChanged(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);

        subject.registerObserver(mockObserver1);
        subject.registerObserver(mockObserver2);

        subject.notifyObservers(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);

        verify(mockObserver1).onStatusChanged(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);
        verify(mockObserver2).onStatusChanged(testShipment, ShipmentState.LABEL_CREATED, ShipmentState.IN_TRANSIT);
    }
}
