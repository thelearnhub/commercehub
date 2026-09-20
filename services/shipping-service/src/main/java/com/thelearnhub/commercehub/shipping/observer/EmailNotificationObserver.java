package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete observer simulating email dispatch when shipment status changes (e.g. DELIVERED or FAILED).
 */
@Component
public class EmailNotificationObserver implements ShipmentObserver {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onStatusChanged(Shipment shipment, ShipmentState previousState, ShipmentState newState) {
        if (newState == ShipmentState.OUT_FOR_DELIVERY || newState == ShipmentState.DELIVERED || newState == ShipmentState.FAILED) {
            log.info("Dispatching email notification to recipient [{}] for tracking [{}] status: {}",
                    shipment.getRecipientName(), shipment.getTrackingNumber(), newState);
        }
    }
}
