package com.tosin.koins.integration.payment.consumer;

import com.tosin.koins.integration.payment.event.PaymentConfirmationEvent;
import com.tosin.koins.notification.service.NotificationService;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Handles payment confirmation events published after Paystack webhook succeeds.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmationConsumer {

    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @RabbitListener(queues = "${app.rabbitmq.payment-confirmation-queue}")
    public void consume(PaymentConfirmationEvent event) {
        log.info(
                "Payment confirmation event received. transactionReference={}",
                event.transactionReference()
        );

        User user = userRepository.findById(event.userId())
                .orElse(null);

        if (user == null) {
            log.warn("User not found for payment confirmation event. userId={}", event.userId());
            return;
        }

        notificationService.sendPaymentConfirmationNotification(
                user,
                event.transactionReference(),
                event.amount(),
                event.currency()
        );
    }
}