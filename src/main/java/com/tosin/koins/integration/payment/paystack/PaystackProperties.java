package com.tosin.koins.integration.payment.paystack;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.paystack")
public record PaystackProperties(String baseUrl,
                                 String secretKey,
                                 String callbackUrl
) {
}
