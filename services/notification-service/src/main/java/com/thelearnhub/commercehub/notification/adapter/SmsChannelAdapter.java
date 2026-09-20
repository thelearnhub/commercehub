package com.thelearnhub.commercehub.notification.adapter;

import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SmsChannelAdapter implements NotificationChannelAdapter {

    private static final Logger log = LoggerFactory.getLogger(SmsChannelAdapter.class);

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.SMS == channel;
    }

    @Override
    public void send(String recipient, String subject, String content) {
        log.info("[SMS ADAPTER] Sending SMS to '{}': {}", recipient, content);
    }
}
