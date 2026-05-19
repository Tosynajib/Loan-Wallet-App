package com.tosin.koins.notification.event;

import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.common.enums.NotificationType;

import java.util.UUID;

/**
 * Message sent to RabbitMQ for async notification delivery.
 */
public record NotificationEvent(
        UUID notificationLogId,
        UUID userId,
        String reference,
        NotificationType type,
        NotificationChannel channel,
        String recipient,
        String subject,
        String message
) {
}