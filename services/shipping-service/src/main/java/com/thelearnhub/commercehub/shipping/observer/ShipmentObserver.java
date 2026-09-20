package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;

/**
 * Observer interface in the Observer Pattern.
 * Components implementing this interface will receive notifications when a shipment's status changes.
 */
public interface ShipmentObserver {

    /**
     * Called when a shipment status transition occurs.
     *
     * @param shipment the shipment entity whose status changed
     * @param previousState the status prior to update
     * @param newState the new updated status
     */
    void onStatusChanged(Shipment shipment, ShipmentState previousState, ShipmentState newState);
}
