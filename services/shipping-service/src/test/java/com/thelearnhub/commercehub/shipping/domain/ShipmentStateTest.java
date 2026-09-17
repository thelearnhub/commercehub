package com.thelearnhub.commercehub.shipping.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShipmentStateTest {

    @Test
    @DisplayName("LABEL_CREATED valid transitions")
    void labelCreatedTransitions() {
        assertThat(ShipmentState.LABEL_CREATED.canTransitionTo(ShipmentState.IN_TRANSIT)).isTrue();
        assertThat(ShipmentState.LABEL_CREATED.canTransitionTo(ShipmentState.FAILED)).isTrue();

        assertThat(ShipmentState.LABEL_CREATED.canTransitionTo(ShipmentState.OUT_FOR_DELIVERY)).isFalse();
        assertThat(ShipmentState.LABEL_CREATED.canTransitionTo(ShipmentState.DELIVERED)).isFalse();
        assertThat(ShipmentState.LABEL_CREATED.canTransitionTo(ShipmentState.LABEL_CREATED)).isFalse();
    }

    @Test
    @DisplayName("IN_TRANSIT valid transitions")
    void inTransitTransitions() {
        assertThat(ShipmentState.IN_TRANSIT.canTransitionTo(ShipmentState.OUT_FOR_DELIVERY)).isTrue();
        assertThat(ShipmentState.IN_TRANSIT.canTransitionTo(ShipmentState.FAILED)).isTrue();

        assertThat(ShipmentState.IN_TRANSIT.canTransitionTo(ShipmentState.DELIVERED)).isFalse();
        assertThat(ShipmentState.IN_TRANSIT.canTransitionTo(ShipmentState.LABEL_CREATED)).isFalse();
    }

    @Test
    @DisplayName("OUT_FOR_DELIVERY valid transitions")
    void outForDeliveryTransitions() {
        assertThat(ShipmentState.OUT_FOR_DELIVERY.canTransitionTo(ShipmentState.DELIVERED)).isTrue();
        assertThat(ShipmentState.OUT_FOR_DELIVERY.canTransitionTo(ShipmentState.FAILED)).isTrue();

        assertThat(ShipmentState.OUT_FOR_DELIVERY.canTransitionTo(ShipmentState.IN_TRANSIT)).isFalse();
        assertThat(ShipmentState.OUT_FOR_DELIVERY.canTransitionTo(ShipmentState.LABEL_CREATED)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ShipmentState.class)
    @DisplayName("DELIVERED is a terminal state")
    void deliveredTerminalState(ShipmentState targetState) {
        assertThat(ShipmentState.DELIVERED.canTransitionTo(targetState)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ShipmentState.class)
    @DisplayName("FAILED is a terminal state")
    void failedTerminalState(ShipmentState targetState) {
        assertThat(ShipmentState.FAILED.canTransitionTo(targetState)).isFalse();
    }

    @Test
    @DisplayName("validateTransition throws IllegalStateException on invalid transition")
    void validateTransitionThrowsOnInvalid() {
        assertThatThrownBy(() -> ShipmentState.LABEL_CREATED.validateTransition(ShipmentState.DELIVERED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid shipment state transition from LABEL_CREATED to DELIVERED");

        assertThatCode(() -> ShipmentState.LABEL_CREATED.validateTransition(ShipmentState.IN_TRANSIT))
                .doesNotThrowAnyException();
    }
}
