package com.tosin.koins.notification.repository;

import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.common.enums.NotificationType;
import com.tosin.koins.notification.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    /**
     * Useful for preventing duplicate reminders for the same schedule/day/reference.
     */
    boolean existsByReferenceAndTypeAndChannel(
            String reference,
            NotificationType type,
            NotificationChannel channel
    );

    Optional<NotificationLog> findByReferenceAndTypeAndChannel(
            String reference,
            NotificationType type,
            NotificationChannel channel
    );
}