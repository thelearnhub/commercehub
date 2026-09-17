package com.thelearnhub.commercehub.notification.adapter;

import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PushChannelAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(PushChannelAdapter.class);

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.PUSH == channel;
    }

    @Override
    public void send(String recipient, String subject, String content) {
        log.info("[PUSH ADAPTER] Sending Push Notification to '{}' with title '{}': {}", recipient, subject, content);
    }
}
