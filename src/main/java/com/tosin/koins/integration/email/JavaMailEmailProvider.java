package com.tosin.koins.integration.email;

import com.tosin.koins.common.exception.EmailException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Real email provider using JavaMail.
 *
 * Enabled when:
 * app.email.provider=javamail
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.email.provider",
        havingValue = "javamail"
)
public class JavaMailEmailProvider implements EmailProvider {

    private final JavaMailSender javaMailSender;
    private final EmailProperties emailProperties;

    @Override
    public void sendEmail(String recipient, String subject, String message) {
        validateConfiguration();

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(message, false);
            helper.setFrom(emailProperties.fromAddress(), emailProperties.fromName());

            javaMailSender.send(mimeMessage);

            log.info("JavaMail email sent successfully. recipient={}, subject={}", recipient, subject);

        } catch (Exception ex) {
            log.error("JavaMail email delivery failed. recipient={}, subject={}", recipient, subject, ex);
            throw new EmailException("Email delivery failed");
        }
    }

    private void validateConfiguration() {
        if (emailProperties.fromAddress() == null || emailProperties.fromAddress().isBlank()) {
            throw new EmailException("Email from-address is not configured");
        }
    }
}