package com.thelearnhub.commercehub.notification.dto;

import com.thelearnhub.commercehub.notification.domain.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {

    @NotBlank(message = "Recipient cannot be blank")
    private String recipient;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    private String subject;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    public NotificationRequest() {
    }

    public NotificationRequest(String recipient, NotificationChannel channel, String subject, String content) {
        this.recipient = recipient;
        this.channel = channel;
        this.subject = subject;
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
