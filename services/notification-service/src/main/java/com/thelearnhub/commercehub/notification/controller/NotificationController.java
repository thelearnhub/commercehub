package com.thelearnhub.commercehub.notification.controller;

import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.dto.NotificationResponse;
import com.thelearnhub.commercehub.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification", description = "Notification dispatch via EMAIL, SMS, and PUSH, history, and admin logs.")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    @Operation(summary = "Send notification (EMAIL, SMS, PUSH)")
    public ResponseEntity<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @GetMapping("/history")
    @Operation(summary = "Get notification history for recipient")
    public ResponseEntity<List<NotificationResponse>> getHistory(
            @RequestParam(name = "recipient", required = false) String recipient,
            Authentication authentication
    ) {
        String targetRecipient = recipient;
        if (targetRecipient == null || targetRecipient.isBlank()) {
            if (authentication != null && authentication.isAuthenticated()) {
                targetRecipient = authentication.getName();
            }
        }
        if (targetRecipient == null || targetRecipient.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(notificationService.getHistory(targetRecipient));
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all notification logs (ADMIN only)")
    public ResponseEntity<List<NotificationResponse>> getAllLogs() {
        return ResponseEntity.ok(notificationService.getAllLogs());
    }
}
