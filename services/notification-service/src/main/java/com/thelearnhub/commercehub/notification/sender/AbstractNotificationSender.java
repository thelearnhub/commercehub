package com.thelearnhub.commercehub.notification.sender;

import com.thelearnhub.commercehub.notification.domain.entity.NotificationLog;
import com.thelearnhub.commercehub.notification.domain.enums.NotificationStatus;
import com.thelearnhub.commercehub.notification.dto.NotificationRequest;
import com.thelearnhub.commercehub.notification.repository.NotificationLogRepository;

import java.time.LocalDateTime;

public abstract class AbstractNotificationSender {

    protected final NotificationLogRepository logRepository;

    public AbstractNotificationSender(NotificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /**
     * Template Method defining the step-by-step workflow for sending notifications.
     */
    public final NotificationLog sendNotification(NotificationRequest request) {
        String preparedContent = prepareContent(request);
        NotificationStatus status = NotificationStatus.SENT;
        String errorMessage = null;

        try {
            dispatch(request, preparedContent);
        } catch (Exception e) {
            status = NotificationStatus.FAILED;
            errorMessage = e.getMessage();
        }

        return auditLog(request, preparedContent, status, errorMessage);
    }

    /**
     * Primitive Operation 1: Prepare and format notification content.
     */
    protected abstract String prepareContent(NotificationRequest request);

    /**
     * Primitive Operation 2: Dispatch notification to channel adapter.
     */
    protected abstract void dispatch(NotificationRequest request, String preparedContent);

    /**
     * Primitive Operation 3: Audit log execution record in DB.
     */
    protected NotificationLog auditLog(NotificationRequest request, String content, NotificationStatus status, String errorMessage) {
        NotificationLog log = new NotificationLog();
        log.setRecipient(request.getRecipient());
        log.setChannel(request.getChannel());
        log.setSubject(request.getSubject());
        log.setContent(content);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);
        log.setCreatedAt(LocalDateTime.now());
        return logRepository.save(log);
    }
}
