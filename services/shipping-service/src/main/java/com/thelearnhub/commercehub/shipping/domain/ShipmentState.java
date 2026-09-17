package com.thelearnhub.commercehub.shipping.domain;

/**
 * State Pattern implementation defining shipment lifecycle states and state transition rules.
 */
public enum ShipmentState {

    LABEL_CREATED {
        @Override
        public boolean canTransitionTo(ShipmentState nextState) {
            return nextState == IN_TRANSIT || nextState == FAILED;
        }
    },
    IN_TRANSIT {
        @Override
        public boolean canTransitionTo(ShipmentState nextState) {
            return nextState == OUT_FOR_DELIVERY || nextState == FAILED;
        }
    },
    OUT_FOR_DELIVERY {
        @Override
        public boolean canTransitionTo(ShipmentState nextState) {
            return nextState == DELIVERED || nextState == FAILED;
        }
    },
    DELIVERED {
        @Override
        public boolean canTransitionTo(ShipmentState nextState) {
            return false;
        }
    },
    FAILED {
        @Override
        public boolean canTransitionTo(ShipmentState nextState) {
            return false;
        }
    };

    /**
     * Determines whether transitioning from current state to nextState is permitted.
     *
     * @param nextState target state
     * @return true if valid transition, false otherwise
     */
    public abstract boolean canTransitionTo(ShipmentState nextState);

    /**
     * Validates state transition and throws IllegalStateException if invalid.
     *
     * @param nextState target state
     */
    public void validateTransition(ShipmentState nextState) {
        if (!canTransitionTo(nextState)) {
            throw new IllegalStateException(
                    String.format("Invalid shipment state transition from %s to %s", this, nextState)
            );
        }
    }
}
