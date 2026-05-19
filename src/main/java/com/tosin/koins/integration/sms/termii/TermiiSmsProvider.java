package com.tosin.koins.integration.sms.termii;

import com.tosin.koins.common.exception.SmsException;
import com.tosin.koins.integration.sms.SmsProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Real Termii SMS provider implementation.
 *
 * Enabled when:
 * app.sms.provider=termii
 */
@Slf4j
@Service
@ConditionalOnProperty(
        name = "app.sms.provider",
        havingValue = "termii"
)
public class TermiiSmsProvider implements SmsProvider {

    private final RestClient restClient;
    private final TermiiProperties properties;

    public TermiiSmsProvider(
            RestClient.Builder restClientBuilder,
            TermiiProperties properties
    ) {
        this.properties = properties;
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        validateConfiguration();

        TermiiSendSmsRequest request = new TermiiSendSmsRequest(
                properties.apiKey(),
                normalizePhoneNumber(phoneNumber),
                properties.senderId(),
                message,
                properties.messageType(),
                properties.channel()
        );

        try {
            TermiiSendSmsResponse response = restClient.post()
                    .uri("/api/sms/send")
                    .body(request)
                    .retrieve()
                    .body(TermiiSendSmsResponse.class);

            log.info(
                    "Termii SMS sent. recipient={}, messageId={}, code={}",
                    phoneNumber,
                    response != null ? response.messageId() : null,
                    response != null ? response.code() : null
            );

        } catch (Exception ex) {
            log.error("Termii SMS sending failed. recipient={}", phoneNumber, ex);
            throw new SmsException("SMS delivery failed");
        }
    }

    private void validateConfiguration() {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new SmsException("Termii API key is not configured");
        }

        if (properties.senderId() == null || properties.senderId().isBlank()) {
            throw new SmsException("Termii sender ID is not configured");
        }
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            throw new SmsException("Phone number is required");
        }

        return phoneNumber.trim().replace("+", "");
    }
}