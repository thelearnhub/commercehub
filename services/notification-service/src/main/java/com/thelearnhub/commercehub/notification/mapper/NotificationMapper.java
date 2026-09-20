package com.thelearnhub.commercehub.notification.mapper;

import com.thelearnhub.commercehub.notification.domain.entity.NotificationLog;
import com.thelearnhub.commercehub.notification.dto.NotificationResponse;

public class NotificationMapper {

    public static NotificationResponse toResponse(NotificationLog log) {
        if (log == null) {
            return null;
        }
        return new NotificationResponse(
                log.getId(),
                log.getRecipient(),
                log.getChannel(),
                log.getSubject(),
                log.getContent(),
                log.getStatus(),
                log.getErrorMessage(),
                log.getCreatedAt()
        );
    }
}
