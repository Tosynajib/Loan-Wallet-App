package com.tosin.koins.integration.sms.termii;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.termii")
public record TermiiProperties(
        String baseUrl,
        String apiKey,
        String senderId,
        String channel,
        String messageType
) {
}