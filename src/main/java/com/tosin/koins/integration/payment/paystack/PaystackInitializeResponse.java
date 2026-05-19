package com.tosin.koins.integration.payment.paystack;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public record PaystackInitializeResponse(Boolean status,
                                         String message,
                                         Data data
) {public record Data(
        @JsonProperty("authorization_url")
        String authorizationUrl,

        @JsonProperty("access_code")
        String accessCode,

        String reference
) {

 }
}
