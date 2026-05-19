package com.tosin.koins.integration.sms.termii;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TermiiSendSmsResponse(

        @JsonProperty("message_id")
        String messageId,

        String message,

        String code,

        String balance,

        String user
) {
}