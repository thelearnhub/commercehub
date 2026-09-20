package com.thelearnhub.commercehub.payment.service;

import com.thelearnhub.commercehub.payment.adapter.PaymentProviderAdapter;
import com.thelearnhub.commercehub.payment.domain.*;
import com.thelearnhub.commercehub.payment.dto.*;
import com.thelearnhub.commercehub.payment.exception.PaymentException;
import com.thelearnhub.commercehub.payment.exception.PaymentNotFoundException;
import com.thelearnhub.commercehub.payment.factory.PaymentProviderFactory;
import com.thelearnhub.commercehub.payment.mapper.PaymentMapper;
import com.thelearnhub.commercehub.payment.repository.PaymentAuditLogRepository;
import com.thelearnhub.commercehub.payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String IDEMPOTENCY_PREFIX = "payment:idempotency:";

    private final PaymentRepository paymentRepository;
    private final PaymentAuditLogRepository auditLogRepository;
    private final PaymentProviderFactory providerFactory;
    private final RedisTemplate<String, Object> redisTemplate;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentAuditLogRepository auditLogRepository,
                              PaymentProviderFactory providerFactory,
                              RedisTemplate<String, Object> redisTemplate) {
        this.paymentRepository = paymentRepository;
        this.auditLogRepository = auditLogRepository;
        this.providerFactory = providerFactory;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public PaymentResponse charge(ChargeRequest request, String idempotencyKey, String userId) {
        // Idempotency check: Return existing payment if already processed
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("Idempotent request detected for key {}. Returning existing payment {}", idempotencyKey, existing.get().getId());
                return PaymentMapper.toResponse(existing.get());
            }
        }

        PaymentProvider provider = PaymentProvider.fromString(request.provider());
        PaymentProviderAdapter adapter = providerFactory.getAdapter(provider);

        Payment payment = new Payment(
                request.orderId(),
                userId,
                request.amount(),
                request.currency(),
                provider,
                request.paymentMethod(),
                idempotencyKey
        );

        // Save initial payment state
        payment = paymentRepository.save(payment);

        // Audit Trail: CHARGE_INITIATED
        saveAuditLog(payment.getId(), "CHARGE_INITIATED", payment.getAmount(), provider,
                "Initiating charge for order " + request.orderId());

        PaymentResult result = adapter.processPayment(request.amount(), request.currency(), request.paymentMethod());

        if (result.success()) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(result.transactionId());
            saveAuditLog(payment.getId(), "CHARGE_SUCCESS", payment.getAmount(), provider,
                    "Transaction successful: " + result.transactionId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(result.errorMessage());
            saveAuditLog(payment.getId(), "CHARGE_FAILED", payment.getAmount(), provider,
                    "Transaction failed: " + result.errorMessage());
        }

        payment = paymentRepository.save(payment);

        // Cache Idempotency Key in Redis
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            try {
                redisTemplate.opsForValue().set(IDEMPOTENCY_PREFIX + idempotencyKey, payment.getId().toString(), Duration.ofHours(24));
            } catch (Exception e) {
                log.warn("Failed to cache idempotency key in Redis: {}", e.getMessage());
            }
        }

        if (payment.getStatus() == PaymentStatus.FAILED) {
            throw new PaymentException("Payment processing failed: " + result.errorMessage());
        }

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public PaymentResponse refund(RefundRequest request, String userId) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + request.paymentId()));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentException("Payment has already been refunded");
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot refund payment with status: " + payment.getStatus());
        }

        PaymentProviderAdapter adapter = providerFactory.getAdapter(payment.getProvider());

        // Audit Trail: REFUND_INITIATED
        saveAuditLog(payment.getId(), "REFUND_INITIATED", request.amount(), payment.getProvider(),
                "Initiating refund. Reason: " + request.reason());

        PaymentResult result = adapter.refundPayment(payment.getTransactionId(), request.amount(), request.reason());

        if (result.success()) {
            payment.setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(payment);

            saveAuditLog(payment.getId(), "REFUND_SUCCESS", request.amount(), payment.getProvider(),
                    "Refund successful. Transaction ID: " + result.transactionId());
        } else {
            saveAuditLog(payment.getId(), "REFUND_FAILED", request.amount(), payment.getProvider(),
                    "Refund failed: " + result.errorMessage());
            throw new PaymentException("Refund failed: " + result.errorMessage());
        }

        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentAuditLogResponse> getAuditLogs(UUID paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new PaymentNotFoundException("Payment not found with ID: " + paymentId);
        }
        return auditLogRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId)
                .stream()
                .map(PaymentMapper::toAuditLogResponse)
                .toList();
    }

    private void saveAuditLog(UUID paymentId, String action, java.math.BigDecimal amount,
                              PaymentProvider provider, String details) {
        PaymentAuditLog auditLog = new PaymentAuditLog(paymentId, action, amount, provider, details);
        auditLogRepository.save(auditLog);
    }
}
