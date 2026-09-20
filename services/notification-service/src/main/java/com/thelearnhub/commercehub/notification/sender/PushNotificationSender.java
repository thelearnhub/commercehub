package com.thelearnhub.commercehub.notification.sender;

import com.thelearnhub.commercehub.notification.adapter.PushChannelAdapter;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;
import org.springframework.stereotype.Component;

@Component
public class PushNotificationSender extends AbstractNotificationSender {

    private final PushChannelAdapter pushAdapter;

    public PushNotificationSender(NotificationLogRepository logRepository, PushChannelAdapter pushAdapter) {
        super(logRepository);
        this.pushAdapter = pushAdapter;
    }

    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.PUSH == channel;
    }

    @Override
    protected String prepareContent(NotificationRequest request) {
        return "{\"title\": \"" + (request.getSubject() != null ? request.getSubject() : "Notification") + "\", \"body\": \"" + request.getContent() + "\"}";
    }

    @Override
    protected void dispatch(NotificationRequest request, String preparedContent) {
        pushAdapter.send(request.getRecipient(), request.getSubject(), preparedContent);
    }
}
