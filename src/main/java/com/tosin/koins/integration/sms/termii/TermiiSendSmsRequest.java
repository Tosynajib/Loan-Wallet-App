package com.tosin.koins.integration.sms.termii;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body sent to Termii SMS API.
 */
public record TermiiSendSmsRequest(

        @JsonProperty("api_key")
        String apiKey,

        String to,

        String from,

        String sms,

        String type,

        String channel
) {
}