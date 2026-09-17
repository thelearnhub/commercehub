package com.thelearnhub.commercehub.notification.adapter;

import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;

public interface NotificationChannelAdapter {
    boolean supports(NotificationChannel channel);
    void send(String recipient, String subject, String content);
}
