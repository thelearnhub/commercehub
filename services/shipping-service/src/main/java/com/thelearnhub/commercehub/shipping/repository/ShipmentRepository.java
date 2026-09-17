package com.thelearnhub.commercehub.shipping.repository;

import com.thelearnhub.commercehub.shipping.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    Optional<Shipment> findByOrderId(String orderId);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    boolean existsByOrderId(String orderId);

    boolean existsByTrackingNumber(String trackingNumber);
}
