package com.thelearnhub.commercehub.notification.adapter;

import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailChannelAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(EmailChannelAdapter.class);

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.EMAIL == channel;
    }

    @Override
    public void send(String recipient, String subject, String content) {
        log.info("[EMAIL ADAPTER] Sending email to '{}' with subject '{}': {}", recipient, subject, content);
    }
}
