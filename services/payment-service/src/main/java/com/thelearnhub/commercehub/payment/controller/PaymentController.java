package com.thelearnhub.commercehub.payment.controller;

import com.thelearnhub.commercehub.payment.dto.ChargeRequest;
import com.thelearnhub.commercehub.payment.dto.PaymentAuditLogResponse;
import com.thelearnhub.commercehub.payment.dto.PaymentResponse;
import com.thelearnhub.commercehub.payment.dto.RefundRequest;
import com.thelearnhub.commercehub.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment API", description = "Endpoints for processing payments, refunds, idempotency, and audit trails")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/charge")
    @Operation(summary = "Charge payment", description = "Executes a payment charge using the specified provider adapter with optional Idempotency-Key support")
    public ResponseEntity<PaymentResponse> charge(
            @Valid @RequestBody ChargeRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            Authentication authentication
    ) {
        String userId = authentication != null ? authentication.getName() : null;
        PaymentResponse response = paymentService.charge(request, idempotencyKey, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refund")
    @Operation(summary = "Refund payment", description = "Refunds a previously completed payment charge")
    public ResponseEntity<PaymentResponse> refund(
            @Valid @RequestBody RefundRequest request,
            Authentication authentication
    ) {
        String userId = authentication != null ? authentication.getName() : null;
        PaymentResponse response = paymentService.refund(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment transaction details by UUID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Retrieves payment transaction details associated with an order ID")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/audit-logs")
    @Operation(summary = "Get payment audit logs", description = "Retrieves all audit history entries for a specific payment")
    public ResponseEntity<List<PaymentAuditLogResponse>> getAuditLogs(@PathVariable UUID id) {
        List<PaymentAuditLogResponse> logs = paymentService.getAuditLogs(id);
        return ResponseEntity.ok(logs);
    }
}
