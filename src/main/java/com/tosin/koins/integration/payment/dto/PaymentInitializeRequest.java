package com.tosin.koins.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentInitializeRequest(String email,
                                       BigDecimal amount,
                                       String reference
) {
}
