package com.tosin.koins.integration.payment.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaystackVerifyResponse (
        Boolean status,
        String message,
        Data data
){
    public record Data(
            String status,
            String reference,

            @JsonProperty("id")
            Long providerTransactionId,

            Long amount
    ) {
    }

}
