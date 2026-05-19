package com.tosin.koins.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public record RabbitMqProperties (String notificationExchange,
                                 String notificationQueue,
                                 String notificationRoutingKey,
                                 String paymentExchange,
                                 String paymentConfirmationQueue,
                                 String paymentConfirmationRoutingKey
){
}
