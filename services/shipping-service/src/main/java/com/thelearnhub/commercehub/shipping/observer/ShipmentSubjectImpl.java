package com.thelearnhub.commercehub.shipping.observer;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import com.thelearnhub.commercehub.shipping.domain.ShipmentState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Default implementation of ShipmentSubject managing registered observers in a thread-safe list.
 */
@Component
public class ShipmentSubjectImpl implements ShipmentSubject {

    private final List<ShipmentObserver> observers = new CopyOnWriteArrayList<>();

    public ShipmentSubjectImpl() {
    }

    @Autowired(required = false)
    public ShipmentSubjectImpl(List<ShipmentObserver> initialObservers) {
        if (initialObservers != null) {
            this.observers.addAll(initialObservers);
        }
    }

    @Override
    public void registerObserver(ShipmentObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(ShipmentObserver observer) {
        if (observer != null) {
            observers.remove(observer);
        }
    }

    @Override
    public void notifyObservers(Shipment shipment, ShipmentState previousState, ShipmentState newState) {
        for (ShipmentObserver observer : observers) {
            try {
                observer.onStatusChanged(shipment, previousState, newState);
            } catch (Exception e) {
                // Log exception to prevent single observer failure from interrupting notification chain
                System.err.printf("Error notifying observer %s: %s%n", observer.getClass().getName(), e.getMessage());
            }
        }
    }

    public List<ShipmentObserver> getObservers() {
        return List.copyOf(observers);
    }
}
