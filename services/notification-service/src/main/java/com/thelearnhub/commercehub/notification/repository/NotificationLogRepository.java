package com.thelearnhub.commercehub.notification.repository;

import com.thelearnhub.commercehub.notification.domain.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByRecipientOrderByCreatedAtDesc(String recipient);
    List<NotificationLog> findAllByOrderByCreatedAtDesc();
}
