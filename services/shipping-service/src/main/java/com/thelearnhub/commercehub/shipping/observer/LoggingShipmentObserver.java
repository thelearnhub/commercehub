package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete observer that logs shipment status transitions.
 */
@Component
public class LoggingShipmentObserver implements ShipmentObserver {

    private static final Logger log = LoggerFactory.getLogger(LoggingShipmentObserver.class);

    @Override
    public void onStatusChanged(Shipment shipment, ShipmentState previousState, ShipmentState newState) {
        log.info("Shipment [{}] status changed from {} to {}",
                shipment.getTrackingNumber(), previousState, newState);
    }
}
