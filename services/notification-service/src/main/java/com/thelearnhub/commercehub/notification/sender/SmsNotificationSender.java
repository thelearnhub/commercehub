package com.thelearnhub.commercehub.notification.sender;

import com.thelearnhub.commercehub.notification.adapter.SmsChannelAdapter;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;
import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender extends AbstractNotificationSender {

    private final SmsChannelAdapter smsAdapter;

    public SmsNotificationSender(NotificationLogRepository logRepository, SmsChannelAdapter smsAdapter) {
        super(logRepository);
        this.smsAdapter = smsAdapter;
    }

    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.SMS == channel;
    }

    @Override
    protected String prepareContent(NotificationRequest request) {
        return "[CommerceHub SMS] " + request.getContent();
    }

    @Override
    protected void dispatch(NotificationRequest request, String preparedContent) {
        smsAdapter.send(request.getRecipient(), request.getSubject(), preparedContent);
    }
}
