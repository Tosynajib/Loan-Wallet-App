package com.tosin.koins.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentVerificationResponse(boolean successful,
                                          String reference,
                                          String providerReference,
                                          BigDecimal amount,
                                          String status
) {
}
