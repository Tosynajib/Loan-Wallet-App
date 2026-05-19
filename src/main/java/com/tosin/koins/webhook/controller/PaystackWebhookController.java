package com.tosin.koins.webhook.controller;

import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.webhook.service.PaymentWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Payment provider webhook APIs")
public class PaystackWebhookController {

    private final PaymentWebhookService paymentWebhookService;

    @PostMapping("/paystack")
    @Operation(summary = "Receive Paystack payment confirmation webhook")
    public ApiResponse<Void> handlePaystackWebhook(
            @RequestHeader("x-paystack-signature") String signature,
            @RequestBody String payload
    ) {
        paymentWebhookService.processPaystackWebhook(payload, signature);

        return ApiResponse.success("Webhook processed successfully");
    }
}