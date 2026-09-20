package com.thelearnhub.commercehub.payment.repository;

import com.thelearnhub.commercehub.payment.domain.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentAuditLogRepository extends JpaRepository<PaymentAuditLog, UUID> {

    List<PaymentAuditLog> findByPaymentIdOrderByCreatedAtDesc(UUID paymentId);
}
