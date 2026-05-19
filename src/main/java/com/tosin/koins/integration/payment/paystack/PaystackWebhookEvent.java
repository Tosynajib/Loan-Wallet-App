package com.tosin.koins.integration.payment.paystack;

public record PaystackWebhookEvent(

        String event,
        Data data) {

    public record Data(
            String reference,
            String status,
            Long amount
    ) {
    }
}
