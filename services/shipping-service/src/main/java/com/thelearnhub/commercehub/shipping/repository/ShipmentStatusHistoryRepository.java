package com.thelearnhub.commercehub.shipping.repository;

import com.thelearnhub.commercehub.shipping.domain.ShipmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentStatusHistoryRepository extends JpaRepository<ShipmentStatusHistory, Long> {

    List<ShipmentStatusHistory> findByShipmentIdOrderByCreatedAtAsc(Long shipmentId);
}
