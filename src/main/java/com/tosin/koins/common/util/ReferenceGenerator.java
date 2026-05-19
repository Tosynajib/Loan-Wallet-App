package com.tosin.koins.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Generates unique transaction references.
 *
 * Why:
 * - Every financial transaction should have a unique reference.
 * - External providers like Paystack also rely on references.
 */
public final class ReferenceGenerator {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private ReferenceGenerator() {
    }

    public static String generate(String prefix) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String randomPart = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();

        return prefix + "-" + timestamp + "-" + randomPart;
    }
}