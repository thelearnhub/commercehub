package com.thelearnhub.commercehub.payment.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment_audit_logs")
public class PaymentAuditLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PaymentProvider provider;

    @Column(length = 512)
    private String details;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected PaymentAuditLog() {
    }

    public PaymentAuditLog(UUID paymentId, String action, BigDecimal amount, PaymentProvider provider, String details) {
        this.paymentId = paymentId;
        this.action = action;
        this.amount = amount;
        this.provider = provider;
        this.details = details;
    }

    public UUID getId() {
        return id;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public String getAction() {
        return action;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getDetails() {
        return details;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
