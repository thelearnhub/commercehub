package com.thelearnhub.commercehub.notification.sender;

import com.thelearnhub.commercehub.notification.adapter.EmailChannelAdapter;
import com.thelearnhub.commercehub.notification.adapter.PushChannelAdapter;
import com.thelearnhub.commercehub.notification.adapter.SmsChannelAdapter;
import com.thelearnhub.commercehub.notification.domain.entity.NotificationLog;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationStatus;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationTemplateMethodTest {

    private NotificationLogRepository logRepository;
    private EmailChannelAdapter emailAdapter;
    private SmsChannelAdapter smsAdapter;
    private PushChannelAdapter pushAdapter;

    private EmailNotificationSender emailSender;
    private SmsNotificationSender smsSender;
    private PushNotificationSender pushSender;

    @BeforeEach
    void setUp() {
        logRepository = mock(NotificationLogRepository.class);
        emailAdapter = mock(EmailChannelAdapter.class);
        smsAdapter = mock(SmsChannelAdapter.class);
        pushAdapter = mock(PushChannelAdapter.class);

        emailSender = new EmailNotificationSender(logRepository, emailAdapter);
        smsSender = new SmsNotificationSender(logRepository, smsAdapter);
        pushSender = new PushNotificationSender(logRepository, pushAdapter);

        when(logRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void sendEmailNotificationExecutesTemplateMethodStepsSuccessfully() {
        NotificationRequest request = new NotificationRequest(
                "user@example.com",
                NotificationChannel.EMAIL,
                "Order Confirmation",
                "Your order #1001 has been placed successfully."
        );

        NotificationLog result = emailSender.sendNotification(request);

        // 1. Verify dispatch step was invoked with prepared content
        verify(emailAdapter).send(
                eq("user@example.com"),
                eq("Order Confirmation"),
                contains("[Email Header]")
        );

        // 2. Verify audit log step saved log with SENT status
        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());

        NotificationLog savedLog = captor.getValue();
        assertThat(savedLog.getRecipient()).isEqualTo("user@example.com");
        assertThat(savedLog.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(savedLog.getSubject()).isEqualTo("Order Confirmation");
        assertThat(savedLog.getContent()).contains("[Email Header]");
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(savedLog.getErrorMessage()).isNull();

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void sendSmsNotificationExecutesTemplateMethodStepsSuccessfully() {
        NotificationRequest request = new NotificationRequest(
                "+15551234567",
                NotificationChannel.SMS,
                null,
                "Your OTP code is 482910."
        );

        NotificationLog result = smsSender.sendNotification(request);

        verify(smsAdapter).send(
                eq("+15551234567"),
                isNull(),
                eq("[CommerceHub SMS] Your OTP code is 482910.")
        );

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());

        NotificationLog savedLog = captor.getValue();
        assertThat(savedLog.getRecipient()).isEqualTo("+15551234567");
        assertThat(savedLog.getChannel()).isEqualTo(NotificationChannel.SMS);
        assertThat(savedLog.getContent()).isEqualTo("[CommerceHub SMS] Your OTP code is 482910.");
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void sendPushNotificationExecutesTemplateMethodStepsSuccessfully() {
        NotificationRequest request = new NotificationRequest(
                "device-token-abc-123",
                NotificationChannel.PUSH,
                "Price Drop Alert",
                "Item in your wishlist is on sale!"
        );

        NotificationLog result = pushSender.sendNotification(request);

        verify(pushAdapter).send(
                eq("device-token-abc-123"),
                eq("Price Drop Alert"),
                contains("\"title\": \"Price Drop Alert\"")
        );

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());

        NotificationLog savedLog = captor.getValue();
        assertThat(savedLog.getRecipient()).isEqualTo("device-token-abc-123");
        assertThat(savedLog.getChannel()).isEqualTo(NotificationChannel.PUSH);
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void sendNotificationHandlesDispatchFailureAndAuditsFailedStatus() {
        NotificationRequest request = new NotificationRequest(
                "user@example.com",
                NotificationChannel.EMAIL,
                "Welcome",
                "Welcome to CommerceHub!"
        );

        doThrow(new RuntimeException("SMTP Server Unreachable"))
                .when(emailAdapter).send(any(), any(), any());

        NotificationLog result = emailSender.sendNotification(request);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);
        verify(logRepository).save(captor.capture());

        NotificationLog savedLog = captor.getValue();
        assertThat(savedLog.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(savedLog.getErrorMessage()).isEqualTo("SMTP Server Unreachable");

        assertThat(result.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(result.getErrorMessage()).isEqualTo("SMTP Server Unreachable");
    }
}
