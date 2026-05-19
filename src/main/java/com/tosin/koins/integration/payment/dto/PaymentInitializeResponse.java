package com.tosin.koins.integration.payment.dto;

public record PaymentInitializeResponse(String reference,
                                        String authorizationUrl,
                                        String accessCode
) {
}
