package com.tosin.koins.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration.
 *
 * We use RabbitMQ for async notification processing and payment confirmation events.
 */
@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqProperties properties;

    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder
                .topicExchange(properties.notificationExchange())
                .durable(true)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(properties.notificationQueue())
                .build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(properties.notificationRoutingKey());
    }

    @Bean
    public TopicExchange paymentExchange() {
        return ExchangeBuilder
                .topicExchange(properties.paymentExchange())
                .durable(true)
                .build();
    }

    @Bean
    public Queue paymentConfirmationQueue() {
        return QueueBuilder
                .durable(properties.paymentConfirmationQueue())
                .build();
    }

    @Bean
    public Binding paymentConfirmationBinding() {
        return BindingBuilder
                .bind(paymentConfirmationQueue())
                .to(paymentExchange())
                .with(properties.paymentConfirmationRoutingKey());
    }

    /**
     * Converts Java objects to JSON messages in RabbitMQ.
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate is used by publishers to send messages.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    /**
     * Listener factory is used by consumers to receive JSON messages.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}