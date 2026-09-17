package com.thelearnhub.commercehub.notification.service;

import com.thelearnhub.commercehub.notification.domain.entity.NotificationLog;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.dto.NotificationResponse;
import com.thelearnhub.commercehub.notification.mapper.NotificationMapper;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;
import com.thelearnhub.commercehub.notification.sender.EmailNotificationSender;
import com.thelearnhub.commercehub.notification.sender.PushNotificationSender;
import com.thelearnhub.commercehub.notification.sender.SmsNotificationSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;
    private final PushNotificationSender pushSender;
    private final NotificationLogRepository logRepository;

    public NotificationService(
            EmailNotificationSender emailSender,
            SmsNotificationSender smsSender,
            PushNotificationSender pushSender,
            NotificationLogRepository logRepository
    ) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
        this.pushSender = pushSender;
        this.logRepository = logRepository;
    }

    public NotificationResponse sendNotification(NotificationRequest request) {
        NotificationLog log;
        switch (request.getChannel()) {
            case EMAIL:
                log = emailSender.sendNotification(request);
                break;
            case SMS:
                log = smsSender.sendNotification(request);
                break;
            case PUSH:
                log = pushSender.sendNotification(request);
                break;
            default:
                throw new IllegalArgumentException("Unsupported channel: " + request.getChannel());
        }
        return NotificationMapper.toResponse(log);
    }

    public List<NotificationResponse> getHistory(String recipient) {
        return logRepository.findByRecipientOrderByCreatedAtDesc(recipient)
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }

    public List<NotificationResponse> getAllLogs() {
        return logRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(NotificationMapper::toResponse)
                .toList();
    }
}
