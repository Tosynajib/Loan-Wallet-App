package com.tosin.koins.common.messaging;

import com.tosin.koins.common.config.RabbitMqProperties;
import com.tosin.koins.integration.payment.event.PaymentConfirmationEvent;
import com.tosin.koins.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Central RabbitMQ publisher.
 *
 * Business services should not know exchange/routing-key details.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMqPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties properties;

    public void publishNotification(NotificationEvent event) {
        rabbitTemplate.convertAndSend(
                properties.notificationExchange(),
                properties.notificationRoutingKey(),
                event
        );

        log.info(
                "Notification event published. notificationLogId={}, type={}, channel={}",
                event.notificationLogId(),
                event.type(),
                event.channel()
        );
    }

    public void publishPaymentConfirmation(PaymentConfirmationEvent event) {
        rabbitTemplate.convertAndSend(
                properties.paymentExchange(),
                properties.paymentConfirmationRoutingKey(),
                event
        );

        log.info(
                "Payment confirmation event published. transactionReference={}",
                event.transactionReference()
        );
    }
}