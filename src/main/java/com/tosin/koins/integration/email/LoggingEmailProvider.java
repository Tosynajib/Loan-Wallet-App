package com.tosin.koins.integration.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Local email provider.
 *
 * Used when app.email.provider=logging.
 * This prevents real email delivery during local development.
 */
@Slf4j
@Service
@ConditionalOnProperty(
        name = "app.email.provider",
        havingValue = "logging",
        matchIfMissing = true
)
public class LoggingEmailProvider implements EmailProvider {

    @Override
    public void sendEmail(String recipient, String subject, String message) {
        log.info("LOCAL EMAIL LOG | To: {} | Subject: {} | Message: {}", recipient, subject, message);
    }
}