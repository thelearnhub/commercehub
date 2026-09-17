package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;

/**
 * Subject interface in the Observer Pattern.
 * Manages subscriber registration, removal, and notification dispatching.
 */
public interface ShipmentSubject {

    /**
     * Registers a new observer subscriber.
     *
     * @param observer the observer to add
     */
    void registerObserver(ShipmentObserver observer);

    /**
     * Removes an existing registered observer.
     *
     * @param observer the observer to remove
     */
    void removeObserver(ShipmentObserver observer);

    /**
     * Notifies all registered observers of a shipment status change event.
     *
     * @param shipment the shipment entity
     * @param previousState the previous shipment state
     * @param newState the new shipment state
     */
    void notifyObservers(Shipment shipment, ShipmentState previousState, ShipmentState newState);
}
