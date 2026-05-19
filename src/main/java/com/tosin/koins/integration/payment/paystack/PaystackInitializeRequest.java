package com.tosin.koins.integration.payment.paystack;

public record PaystackInitializeRequest(String email,
                                        Long amount,
                                        String reference,
                                        String callback_url
) {
}
