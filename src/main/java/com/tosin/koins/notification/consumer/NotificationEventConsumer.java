package com.tosin.koins.notification.consumer;

import com.tosin.koins.common.config.RabbitMqProperties;
import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.integration.email.EmailProvider;
import com.tosin.koins.integration.sms.SmsProvider;
import com.tosin.koins.notification.entity.NotificationLog;
import com.tosin.koins.notification.event.NotificationEvent;
import com.tosin.koins.notification.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes notification events from RabbitMQ and sends SMS/email.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final SmsProvider smsProvider;
    private final EmailProvider emailProvider;
    private final NotificationLogRepository notificationLogRepository;

    @RabbitListener(queues = "${app.rabbitmq.notification-queue}")
    public void consume(NotificationEvent event) {
        log.info(
                "Notification event received. notificationLogId={}, type={}, channel={}",
                event.notificationLogId(),
                event.type(),
                event.channel()
        );

        NotificationLog logRecord = notificationLogRepository.findById(event.notificationLogId())
                .orElse(null);

        if (logRecord == null) {
            log.warn("Notification log not found. notificationLogId={}", event.notificationLogId());
            return;
        }

        try {
            if (event.channel() == NotificationChannel.SMS) {
                smsProvider.sendSms(event.recipient(), event.message());
            } else if (event.channel() == NotificationChannel.EMAIL) {
                emailProvider.sendEmail(event.recipient(), event.subject(), event.message());
            }

            logRecord.markSent();

        } catch (Exception ex) {
            log.error("Notification delivery failed. notificationLogId={}", event.notificationLogId(), ex);
            logRecord.markFailed(ex.getMessage());
        }

        notificationLogRepository.save(logRecord);
    }
}