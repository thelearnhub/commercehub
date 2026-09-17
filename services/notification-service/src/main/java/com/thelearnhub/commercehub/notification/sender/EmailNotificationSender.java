package com.thelearnhub.commercehub.notification.sender;

import com.thelearnhub.commercehub.notification.adapter.EmailChannelAdapter;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender extends AbstractNotificationSender {

    private final EmailChannelAdapter emailAdapter;

    public EmailNotificationSender(NotificationLogRepository logRepository, EmailChannelAdapter emailAdapter) {
        super(logRepository);
        this.emailAdapter = emailAdapter;
    }

    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.EMAIL == channel;
    }

    @Override
    protected String prepareContent(NotificationRequest request) {
        String subject = request.getSubject() != null ? request.getSubject() : "Notification from CommerceHub";
        return "[Email Header] Subject: " + subject + "\nBody: " + request.getContent();
    }

    @Override
    protected void dispatch(NotificationRequest request, String preparedContent) {
        emailAdapter.send(request.getRecipient(), request.getSubject(), preparedContent);
    }
}
