package com.tosin.koins.integration.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
        name = "app.sms.provider",
        havingValue = "logging",
        matchIfMissing = true
)
public class LoggingSmsProvider implements SmsProvider {

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("LOCAL SMS LOG | To: {} | Message: {}", phoneNumber, message);
    }
}